package com.doubtless.doubtless.screens.dashboard.usecases

import android.util.Log
import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class FetchUserFeedByDateUseCase constructor(
    private val firestore: FirebaseFirestore
) {

    sealed class Result {
        class Success(val data: List<DoubtData>) : Result()
        class Error(val message: String) : Result()
    }

    private var lastDoubtData: DoubtData? = null

    suspend fun getFeedData(
        request: FetchUserProfileFeedUseCase.FetchUserFeedRequest, user: User
    ): Result = withContext(Dispatchers.IO) {

        try {
            var query = firestore.collection(FirestoreCollection.AllDoubts).whereEqualTo(
                "author_id", user.id
            ).orderBy("created_on", Query.Direction.DESCENDING)

            if (lastDoubtData != null && !request.fetchFromPage1) {
                query = query.startAfter(lastDoubtData!!.date)
            }

            // if total size = 33 and docFetched = 30, then request only 3 more,
            // else request page size.
            query = query.limit(request.pageSize.toLong())

            val result = query.get().await()
            val doubtDataList = getDoubtDataList(result)
            Log.i("DoubtDataList", doubtDataList.toTypedArray().contentToString())
            if (doubtDataList.isNotEmpty())
                lastDoubtData = doubtDataList.last()

            return@withContext Result.Success(doubtDataList)

        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "some error occurred!")
        }
    }

    @kotlin.jvm.Throws(Exception::class)
    private fun getDoubtDataList(result: QuerySnapshot?): List<DoubtData> {

        val doubtDataList = mutableListOf<DoubtData>()

        result!!.documents.forEach {
            val doubtData = DoubtData.parse(it) ?: return@forEach
            doubtDataList.add(doubtData)
        }

        return doubtDataList
    }


}