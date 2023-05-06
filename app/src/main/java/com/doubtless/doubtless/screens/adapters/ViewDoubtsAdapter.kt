package com.doubtless.doubtless.screens.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ViewDoubtsAdapter(private val allDoubts: ArrayList<DoubtData>) :
    RecyclerView.Adapter<ViewDoubtsAdapter.ViewHolder>() {
    private val db: FirebaseFirestore = Firebase.firestore


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView
        val time: TextView
        val heading: TextView
        val description: TextView
        val voteCount: TextView
        val upvote: ImageButton
        val downvote: ImageButton


        init {
            userName = view.findViewById(R.id.user_name)
            time = view.findViewById(R.id.user_doubt_time)
            heading = view.findViewById(R.id.user_doubt_heading)
            description = view.findViewById(R.id.user_doubt_description)
            voteCount = view.findViewById(R.id.vote_count)
            upvote = view.findViewById(R.id.upvote_btn)
            downvote = view.findViewById(R.id.downvote_btn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doubt_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allDoubts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = allDoubts[position].userName
        holder.time.text = allDoubts[position].date
        holder.heading.text = allDoubts[position].heading
        holder.description.text = allDoubts[position].description
        holder.voteCount.text =
            (allDoubts[position].upVotes - allDoubts[position].downVotes).toString()

        val doubt = allDoubts[position]
        holder.upvote.setOnClickListener {
            db.collection("AllDoubts").document(doubt.id).get().addOnSuccessListener {
                val upVoters = it.get("upVotes") as MutableList<String>
                val downVoters = it.get("downVotes") as MutableList<String>
                val currUserUid = "FLKSJD3298UJFS"
                if (!upVoters.contains(currUserUid)) {
                    if (downVoters.contains(currUserUid)) {
                        downVoters.remove(currUserUid)
                    }
                    upVoters.add(currUserUid)

                } else {
                    upVoters.remove(currUserUid)
                }
                db.collection("AllDoubts").document(doubt.id).update(
                    mapOf(
                        "upVotes" to upVoters, "downVotes" to downVoters
                    )
                ).addOnSuccessListener {
                    holder.voteCount.text = (upVoters.size - downVoters.size).toString()
                }
            }
        }
        holder.downvote.setOnClickListener {
            db.collection("AllDoubts").document(doubt.id).get().addOnSuccessListener {
                val downVoters = it.get("downVotes") as MutableList<String>
                val upVoters = it.get("upVotes") as MutableList<String>
                val currUserUid = "FLKSJD3298UJFS"
                if (!downVoters.contains(currUserUid)) {
                    if (upVoters.contains(currUserUid)) {
                        upVoters.remove(currUserUid)
                    }
                    downVoters.add(currUserUid)
                } else {
                    downVoters.remove(currUserUid)
                }
                db.collection("AllDoubts").document(doubt.id).update(
                    mapOf(
                        "upVotes" to upVoters, "downVotes" to downVoters
                    )
                ).addOnSuccessListener {
                    holder.voteCount.text = (upVoters.size - downVoters.size).toString()
                }
            }
        }


    }
}