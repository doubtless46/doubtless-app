package com.doubtless.doubtless.screens.doubt.usecases

import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.answers.AnswerData
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class VotingUseCase constructor(
    private val firestore: FirebaseFirestore,
    private val user: User,
    private val isForAnswer: Boolean, // else for doubt.
    private val answerData: AnswerData? = null,
    private val doubtData: DoubtData? = null,
) {

    sealed class Result {
        class UpVoted() : Result()
        class UndoneUpVote() : Result()

        class DownVoted() : Result()
        class UndoneDownVote() : Result()

        class Error(val message: String) : Result()
    }

    companion object {
        const val UPVOTED = 1
        const val DOWNVOTED = 2
        const val NONE = 3
        const val NOT_SET = 4
    }

    private var currentState: Int = NOT_SET

    private val docRef = if (isForAnswer) {
        firestore.collection(FirestoreCollection.ANSWER_VOTING_DATA)
            .document(answerData!!.id!!)
    } else {
        firestore.collection(FirestoreCollection.DOUBT_VOTING_DATA)
            .document(doubtData!!.id!!)
    }

    suspend fun getUserCurrentState(): Int = withContext(Dispatchers.IO) {

        if (currentState != NOT_SET)
            return@withContext currentState

        try {

            val userUpvoted = docRef
                .collection(FirestoreCollection.UPVOTE_DATA_USERS)
                .document(user.id!!)
                .get().await()

            if (userUpvoted.exists()) {
                currentState = UPVOTED
                return@withContext currentState
            }

            val userDownvoted = docRef
                .collection(FirestoreCollection.DOWNVOTE_DATA_USERS)
                .document(user.id!!)
                .get().await()

            if (userDownvoted.exists()) {
                currentState = DOWNVOTED
                return@withContext DOWNVOTED
            }

            currentState = NONE
            return@withContext currentState

        } catch (e: Exception) {
            currentState = NOT_SET
            return@withContext currentState
        }
    }

    private val lock = Mutex()

    suspend fun upvoteDoubt(): Result = withContext(Dispatchers.IO) {

        lock.withLock {

            val currentState = getUserCurrentState()

            if (currentState == NOT_SET) {
                return@withContext Result.Error("error!")
            }

            try {

                if (currentState == NONE) {
                    // User didn't upvote this doubt, could have downvoted but i am ignoring it as of now,
                    // Ideally should save these operations locally and then send to server after a while.

                    docRef
                        .collection(FirestoreCollection.UPVOTE_DATA_USERS)
                        .document(user.id!!)
                        .set(user).await()

                    firestore.runTransaction { transaction ->

                        val docx = if (isForAnswer) {
                            firestore.collection(FirestoreCollection.AllDoubts)
                                .document(answerData!!.doubtId!!)
                                .collection(FirestoreCollection.DoubtAnswer)
                                .document(answerData.id!!)
                        } else {
                            firestore.collection(FirestoreCollection.AllDoubts)
                                .document(doubtData!!.id!!)
                        }


                        val new_votes = if (isForAnswer) {
                            transaction.get(docx).get("net_votes") as Long + 1
                        } else {
                            transaction.get(docx).get("net_votes") as Double + 1
                        }

                        transaction.update(docx, "net_votes", new_votes)
                    }.await()

                    this@VotingUseCase.currentState = UPVOTED
                    return@withContext Result.UpVoted()
                }

                if (currentState == UPVOTED) {

                    // user already upvoted this, undo this operation.

                    docRef
                        .collection(FirestoreCollection.UPVOTE_DATA_USERS)
                        .document(user.id!!)
                        .delete().await()

                    firestore.runTransaction { transaction ->

                        val docx = if (isForAnswer) {
                            firestore.collection(FirestoreCollection.AllDoubts)
                                .document(answerData!!.doubtId!!)
                                .collection(FirestoreCollection.DoubtAnswer)
                                .document(answerData.id!!)
                        } else {
                            firestore.collection(FirestoreCollection.AllDoubts)
                                .document(doubtData!!.id!!)
                        }

                        val new_votes = if (isForAnswer) {
                            transaction.get(docx).get("net_votes") as Long - 1
                        } else {
                            transaction.get(docx).get("net_votes") as Double - 1
                        }

                        transaction.update(docx, "net_votes", new_votes)
                    }.await()

                    this@VotingUseCase.currentState = NONE
                    return@withContext Result.UndoneUpVote()
                }

                return@withContext Result.Error("unknown state")

            } catch (e: Exception) {
                return@withContext Result.Error(e.message ?: "error")
            }
        }
    }

    suspend fun downVoteDoubt(): Result = withContext(Dispatchers.IO) {

        lock.withLock {

            val currentState = getUserCurrentState()

            if (currentState == NOT_SET) {
                return@withContext Result.Error("error!")
            }

            try {

                if (currentState == NONE) {
                    // User didn't upvote this doubt, could have downvoted but i am ignoring it as of now,
                    // Ideally should save these operations locally and then send to server after a while.

                    docRef
                        .collection(FirestoreCollection.DOWNVOTE_DATA_USERS)
                        .document(user.id!!)
                        .set(user).await()

                    firestore.runTransaction { transaction ->

                        val docx = if (isForAnswer) {
                            firestore.collection(FirestoreCollection.AllDoubts)
                                .document(answerData!!.doubtId!!)
                                .collection(FirestoreCollection.DoubtAnswer)
                                .document(answerData.id!!)
                        } else {
                            firestore.collection(FirestoreCollection.AllDoubts)
                                .document(doubtData!!.id!!)
                        }

                        val new_votes = if (isForAnswer) {
                            transaction.get(docx).get("net_votes") as Long - 1
                        } else {
                            transaction.get(docx).get("net_votes") as Double - 1
                        }

                        transaction.update(docx, "net_votes", new_votes)
                    }.await()

                    this@VotingUseCase.currentState = DOWNVOTED
                    return@withContext Result.DownVoted()
                }

                if (currentState == DOWNVOTED) {
                    // user already downvoted this, undo this operation.

                    docRef
                        .collection(FirestoreCollection.DOWNVOTE_DATA_USERS)
                        .document(user.id!!)
                        .delete().await()

                    firestore.runTransaction { transaction ->

                        val docx = if (isForAnswer) {
                            firestore.collection(FirestoreCollection.AllDoubts)
                                .document(answerData!!.doubtId!!)
                                .collection(FirestoreCollection.DoubtAnswer)
                                .document(answerData.id!!)
                        } else {
                            firestore.collection(FirestoreCollection.AllDoubts)
                                .document(doubtData!!.id!!)
                        }

                        val new_votes = if (isForAnswer) {
                            transaction.get(docx).get("net_votes") as Long + 1
                        } else {
                            transaction.get(docx).get("net_votes") as Double + 1
                        }

                        transaction.update(docx, "net_votes", new_votes)
                    }.await()

                    this@VotingUseCase.currentState = NONE
                    return@withContext Result.UndoneDownVote()
                }

                return@withContext Result.Error("unknown state")

            } catch (e: Exception) {
                return@withContext Result.Error(e.message ?: "error")
            }
        }
    }

}