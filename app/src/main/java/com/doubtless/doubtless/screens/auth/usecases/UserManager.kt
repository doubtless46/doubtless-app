package com.doubtless.doubtless.screens.auth.usecases

import androidx.annotation.WorkerThread
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.auth.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UserManager constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userDataServerUseCase: UserDataServerUseCase,
    private val userDataStorageUseCase: UserDataStorageUseCase
) {

    sealed class Result(user: User? = null, isNewUser: Boolean?, error: String? = null) {
        class Success(
            val user: User,
            val isNewUser: Boolean,
            val isOldUserWithNoOnboarding: Boolean
        ) : Result(user, isNewUser)

        class Error(val message: String) : Result(null, null, message)
        class LoggedOut : Result(null, null, null)
    }

    private var cachedUserData: User? = null

    fun getCachedUserData() = cachedUserData?.copy()

    fun setNewCachedUserData(cachedUserData: User) {
        this.cachedUserData = cachedUserData
    }

    fun storeCachedUserData() {
        userDataStorageUseCase.setUserData(cachedUserData!!)
    }

    /**
     * if null then user is not logged in
     */
    fun getLoggedInUser(): User? {
        if (firebaseAuth.currentUser == null)
            return null

        cachedUserData = userDataStorageUseCase.getUserData()
        return cachedUserData
    }

    @WorkerThread
    fun registerAndGetIfNewUserSync(serverUser: FirebaseUser): Result {

        // create user from firebase user
        val uniqueID: String = UUID.randomUUID().toString()
        val user = User(
            id = uniqueID,
            name = serverUser.displayName,
            email = serverUser.email,
            phoneNumber = serverUser.phoneNumber,
            photoUrl = serverUser.photoUrl.toString()
        )

        val result = userDataServerUseCase.storeUserOnServerIfNewSync(user)

        if (result is UserDataServerUseCase.Result.Error) {
            return Result.Error(result.message)
        }

        if (result is UserDataServerUseCase.Result.NewUser) {
            userDataStorageUseCase.setUserData(result.newUser)
            cachedUserData = result.newUser
            return Result.Success(
                result.newUser,
                isNewUser = true,
                isOldUserWithNoOnboarding = false
            )
        }

        if (result is UserDataServerUseCase.Result.OldUseWithNoOnboardingData) {
            userDataStorageUseCase.setUserData(result.oldUser)
            cachedUserData = result.oldUser
            return Result.Success(
                result.oldUser,
                isNewUser = false,
                isOldUserWithNoOnboarding = true
            )
        }

        if (result is UserDataServerUseCase.Result.OldUser) {
            userDataStorageUseCase.setUserData(result.oldUser)
            cachedUserData = result.oldUser
            return Result.Success(
                result.oldUser,
                isNewUser = false,
                isOldUserWithNoOnboarding = false
            )
        }

        return Result.Error("unknown state")
    }

    fun onUserLogoutSync(): Result {

        val latch = CountDownLatch(1)
        var error: String? = null

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(DoubtlessApp.getInstance().getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(DoubtlessApp.getInstance(), gso)

        googleSignInClient.signOut().addOnSuccessListener {
            userDataStorageUseCase.onLogout()
            latch.countDown()
        }.addOnFailureListener {
            error = it.message
            latch.countDown()
        }

        latch.await(10, TimeUnit.SECONDS)

        if (error != null)
            return Result.Error(error!!)

        cachedUserData = null

        return Result.LoggedOut()
    }

}