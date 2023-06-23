package com.doubtless.doubtless.screens.auth.usecases

import android.util.Log
import androidx.annotation.WorkerThread
import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.auth.UserAttributes
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UserDataServerUseCase constructor(
    private val firestore: FirebaseFirestore,
    private val gson: Gson
) {

    sealed class Result{
        class NewUser(val newUser: User) : Result()
        class OldUser(val oldUser: User) : Result()
        class OldUseWithNoOnboardingData(val oldUser: User): Result()
        class Error(val message: String) : Result()
    }

    @WorkerThread
    fun storeUserOnServerIfNewSync(user: User): Result { // TODO : wrap in result

        // check if user data is already present on server

        val querySnapshot = firestore.collection(FirestoreCollection.USER)
            // id is created new every time on frontend hence we check for email and then set the found user data locally.
            .whereEqualTo("email", user.email)
            .get()

        var latch = CountDownLatch(1)
        var isNew = false
        var isOldUserWithNoOnboarding = false
        var serverUser: User? = null
        var errorMessage: String? = null

        querySnapshot.addOnSuccessListener { userSnapshot->

            if (userSnapshot.documents.size == 0) {
                isNew = true
                latch.countDown()
                return@addOnSuccessListener
            }

            // old user
            try {
                val _serverUser =
                    userSnapshot.documents[0].toObject(User::class.java) // make ServerUser and map to User

                firestore.collection(FirestoreCollection.USER)
                    .document(userSnapshot.documents[0].id)
                    .collection(FirestoreCollection.USER_ATTR)
                    .get().addOnSuccessListener { attrSnapshot ->
                        try {

                            if (attrSnapshot.documents.size == 0) { // this user is present but his onboarding data is null, ask him to onboard!
                                isOldUserWithNoOnboarding = true
                                serverUser = _serverUser!!.copy(document_id = userSnapshot.documents[0].id)
                            } else {
                                val attr = attrSnapshot.documents[0].toObject(UserAttributes::class.java)
                                serverUser = _serverUser!!.copy(document_id = userSnapshot.documents[0].id, local_user_attr = attr)
                            }

                        } catch (e: Exception) {
                            errorMessage = e.localizedMessage
                        }

                        latch.countDown()
                    }

            } catch (e: Exception) {
                errorMessage = e.localizedMessage
                latch.countDown()
            }

        }.addOnFailureListener {
            errorMessage = it.message
            latch.countDown()
        }

        latch.await(20, TimeUnit.SECONDS)

        if (errorMessage != null)
            return Result.Error(errorMessage!!)

        if (isOldUserWithNoOnboarding) {
            return Result.OldUseWithNoOnboardingData(serverUser!!)
        }

        if (!isNew) {
            return Result.OldUser(serverUser!!)
        }

        // proceed for new user,
        // store new user data in db if he is new

        latch = CountDownLatch(1)

        var createdUser: User = user

        firestore.collection(FirestoreCollection.USER)
            .document(user.id.toString())
            .set(user)
            .addOnSuccessListener {
                createdUser = user.copy(document_id = user.id)
                latch.countDown()
            }.addOnFailureListener {
                errorMessage = it.message
                latch.countDown()
            }

        latch.await(10, TimeUnit.SECONDS)

        if (errorMessage != null)
            return Result.Error(errorMessage!!)

        return Result.NewUser(createdUser)
    }

}