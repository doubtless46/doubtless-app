package com.doubtless.doubtless.screens.post

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ViewPostViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _allPosts = MutableLiveData<ArrayList<PostData>>()
    val allPosts: MutableLiveData<ArrayList<PostData>>
        get() = _allPosts

    init {
        fetchAllPost()
    }

    private fun fetchAllPost() {
        val allPostsReference = db.collection("AllPosts")
        allPostsReference.get().addOnSuccessListener {
            val postsList = ArrayList<PostData>()
            for (data in it.documents) {
                val postData = PostData(
                    data.get("username").toString(),
                    getDateTime(data.get("date") as Timestamp),
                    data.get("doubt").toString(),
                    data.get("heading").toString(),
                    data.get("description").toString()
                )
                postsList.add(postData)
            }
            _allPosts.value = postsList
        }
    }

    private fun getDateTime(s: Timestamp): String {
        return s.toDate().toString()
    }
}