package com.doubtless.doubtless.screens.auth.usecases

import android.util.Log
import androidx.annotation.WorkerThread
import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.auth.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UserDataServerUseCase constructor(
    private val firestore: FirebaseFirestore,
    private val gson: Gson
) {

    sealed class Result(
        user: User? = null,
        error: String? = null
    ) {
        class NewUser(val newUser: User) : Result(newUser, null)
        class OldUser(val oldUser: User) : Result(oldUser, null)
        class Error(val message: String) : Result(null, message)
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
        var serverUser: User? = null
        var errorMessage: String? = null

        querySnapshot.addOnSuccessListener {

            if (it.documents.size == 0) {
                isNew = true
                latch.countDown()
                return@addOnSuccessListener
            }

            // old user
            serverUser =
                it.documents[0].toObject(User::class.java) // make ServerUser and map to User

            latch.countDown()

        }.addOnFailureListener {
            errorMessage = it.message
            latch.countDown()
        }

        latch.await(20, TimeUnit.SECONDS)

        if (errorMessage != null)
            return Result.Error(errorMessage!!)

        if (!isNew) {
            return Result.OldUser(serverUser!!)
        }

        // store user data in db if he is new

        latch = CountDownLatch(1)

        firestore.collection(FirestoreCollection.USER)
            .add(user)
            .addOnSuccessListener {
                latch.countDown()
            }.addOnFailureListener {
                errorMessage = it.message
                latch.countDown()
            }

        latch.await(10, TimeUnit.SECONDS)

        if (errorMessage != null)
            return Result.Error(errorMessage!!)

        return Result.NewUser(user)
    }

}