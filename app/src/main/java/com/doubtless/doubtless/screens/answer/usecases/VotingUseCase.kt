package com.doubtless.doubtless.screens.answer.usecases

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class VotingUseCase(
    private val firestore: FirebaseFirestore,
    private val postId: String
) {

    private var upVotes: Int = 0
    private var downVotes: Int = 0

    private var count: Int = upVotes-downVotes

    init {
        retrieveVoteCount()
    }

    fun upvote() {
        upVotes++
        updateVoteCount()
    }

    fun downvote() {
        if (downVotes > 0) {
            downVotes++
            updateVoteCount()
        }
    }

    fun getCount(): Int {
        return count
    }



    private fun retrieveVoteCount(){
        firestore.collection("AllDoubts")
            .document(postId)
            .get()
            .addOnSuccessListener {documentSnapshot ->
                if (documentSnapshot.exists()) {
                    upVotes = documentSnapshot.getLong("upVotes")?.toInt() ?: 0
                    downVotes = documentSnapshot.getLong("upVotes")?.toInt() ?: 0
                }
            }
            .addOnFailureListener{e ->
                Log.e("VotingUseCase","Error getting votes" , e)
            }
    }

    private fun updateVoteCount() {

        count = upVotes - downVotes
        firestore.collection("AllDoubts")
            .document(postId)
            .set(mapOf("upVotes" to upVotes, "downVotes" to downVotes))
            .addOnSuccessListener {
                Log.i("VotingUseCase", "votes updated successfully")
            }
            .addOnFailureListener { e->

                Log.e("VotingUseCase","Error updating votes" , e)

            }

    }
}