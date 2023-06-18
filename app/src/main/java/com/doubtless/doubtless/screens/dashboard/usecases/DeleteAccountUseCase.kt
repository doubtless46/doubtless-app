package com.doubtless.doubtless.screens.dashboard.usecases

import androidx.annotation.WorkerThread
import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DeleteAccountUseCase(
    private val firestore: FirebaseFirestore,
) {

    sealed class Result {
        class Success(val message: String) : Result()
        class Error(val message: String) : Result()
    }

    suspend fun deleteAccount(userManager: UserManager): Result = withContext(Dispatchers.IO) {
        try {

//            Delete Doubts Posted by User
            var db = firestore.collection(FirestoreCollection.AllDoubts)
            var query =
                db.whereEqualTo("author_id", userManager.getCachedUserData()!!.id).get().await()

            for (document in query.documents) {
                db.document(document.id).delete().await()
            }

//            Delete User Details
            db = firestore.collection(FirestoreCollection.USER)
            query = db.whereEqualTo("id", userManager.getCachedUserData()!!.id).get().await()

            for (document in query.documents) {
                db.document(document.id).delete().await()
            }


//            Delete user credentials from authentication
            val mAuth = FirebaseAuth.getInstance();
            val user = mAuth.currentUser;
            user!!.delete()


            return@withContext Result.Success(message = "Account Successfully Deleted")
        } catch (e: Exception) {

            return@withContext Result.Error(e.message ?: "Failed to delete account")
        }
    }


}