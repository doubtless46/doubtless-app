package com.doubtless.doubtless.screens.doubt.usecases

import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.auth.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class VotingDoubtUseCase constructor(
    private val firestore: FirebaseFirestore,
    private val user: User
) {

    sealed class Result {
        class UpVoted(val doubtId: String) : Result()
        class UndoneUpVote(val doubtId: String) : Result()

        class DownVoted(val doubtId: String) : Result()
        class UndoneDownVote(val doubtId: String) : Result()

        class Error(val message: String) : Result()
    }

    suspend fun upvoteDoubt(doubtId: String): Result = withContext(Dispatchers.IO) {

        try {

            val result = firestore.collection(FirestoreCollection.VOTING_DATA)
                .document(doubtId)
                .collection(FirestoreCollection.UPVOTE_DATA_USERS)
                .document(user.id!!)
                .get().await()

            if (!result.exists()) {

                // User didn't upvote this doubt, could have downvoted but i am ignoring it as of now,
                // Ideally should save these operations locally and then send to server after a while.

                firestore.collection(FirestoreCollection.VOTING_DATA)
                    .document(doubtId)
                    .collection(FirestoreCollection.UPVOTE_DATA_USERS)
                    .document(user.id)
                    .set(user).await()

                firestore.runTransaction { transaction ->
                    val docx = firestore.collection(FirestoreCollection.AllDoubts)
                        .document(doubtId)

                    val new_votes = transaction.get(docx).get("net_votes") as Double + 1

                    transaction.update(docx, "net_votes", new_votes)
                }.await()


                return@withContext Result.UpVoted(doubtId)

            } else {
                // user already upvoted this, undo this operation.

                firestore.collection(FirestoreCollection.VOTING_DATA)
                    .document(doubtId)
                    .collection(FirestoreCollection.UPVOTE_DATA_USERS)
                    .document(user.id)
                    .delete().await()

                firestore.runTransaction { transaction ->
                    val docx = firestore.collection(FirestoreCollection.AllDoubts)
                        .document(doubtId)

                    val new_votes = transaction.get(docx).get("net_votes") as Double - 1

                    transaction.update(docx, "net_votes", new_votes)
                }.await()

                return@withContext Result.UndoneUpVote(doubtId)
            }

        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "error")
        }
    }

}