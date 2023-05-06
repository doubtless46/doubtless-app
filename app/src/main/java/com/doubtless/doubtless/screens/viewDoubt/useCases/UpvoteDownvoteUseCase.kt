package com.doubtless.doubtless.screens.viewDoubt.useCases

import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class UpvoteDownvoteUseCase {


    fun upvote(db: FirebaseFirestore, id: String, voteCount: TextView) {
        db.collection("AllDoubts").document(id).get().addOnSuccessListener {
            val upVoters = it.get("upVotes") as MutableList<String>
            val downVoters = it.get("downVotes") as MutableList<String>
            val currUserUid = "FTGKJHIKHBV"
            if (!upVoters.contains(currUserUid)) {
                if (downVoters.contains(currUserUid)) {
                    downVoters.remove(currUserUid)
                }
                upVoters.add(currUserUid)

            } else {
                upVoters.remove(currUserUid)
            }
            db.collection("AllDoubts").document(id).update(
                mapOf(
                    "upVotes" to upVoters, "downVotes" to downVoters
                )
            ).addOnSuccessListener {
                voteCount.text = (upVoters.size - downVoters.size).toString()
            }
        }
    }

    fun downvote(db: FirebaseFirestore, id: String, voteCount: TextView) {
        db.collection("AllDoubts").document(id).get().addOnSuccessListener {
            val downVoters = it.get("downVotes") as MutableList<String>
            val upVoters = it.get("upVotes") as MutableList<String>
            val currUserUid = "FTGKJHIKHBV"
            if (!downVoters.contains(currUserUid)) {
                if (upVoters.contains(currUserUid)) {
                    upVoters.remove(currUserUid)
                }
                downVoters.add(currUserUid)
            } else {
                downVoters.remove(currUserUid)
            }
            db.collection("AllDoubts").document(id).update(
                mapOf(
                    "upVotes" to upVoters, "downVotes" to downVoters
                )
            ).addOnSuccessListener {
                voteCount.text = (upVoters.size - downVoters.size).toString()
            }
        }


    }
}
