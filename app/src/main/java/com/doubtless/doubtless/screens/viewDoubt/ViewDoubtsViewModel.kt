package com.doubtless.doubtless.screens.viewDoubt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class ViewDoubtsViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _allDoubts = MutableLiveData<ArrayList<DoubtData>>()
    val allDoubts: MutableLiveData<ArrayList<DoubtData>>
        get() = _allDoubts

    init {
        fetchAllDoubts()
    }

    private fun fetchAllDoubts() {
        val allDoubtsReference = db.collection("AllDoubts")
        allDoubtsReference.orderBy(FieldPath.of("date"), Query.Direction.DESCENDING).get()
            .addOnSuccessListener {
                val doubtsList = ArrayList<DoubtData>()
                for (data in it.documents) {
                    val doubtData = DoubtData(
                        data.id,
                        data.get("username").toString(),
                        getDateTime(data.get("date") as Timestamp),
                        data.get("heading").toString(),
                        data.get("description").toString(),
                        data.get("answerIds"),
                        (data.get("upVotes") as List<*>).size.toLong(),
                        (data.get("downVotes") as List<*>).size.toLong()
                    )
                    doubtsList.add(doubtData)
                }
                _allDoubts.value = doubtsList
            }
    }

    private fun getDateTime(s: Timestamp): String {

        return SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.ENGLISH).format(s.toDate())
    }
}