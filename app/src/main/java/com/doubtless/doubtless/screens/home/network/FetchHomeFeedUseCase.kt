package com.doubtless.doubtless.screens.home.network

import android.util.Log
import androidx.annotation.WorkerThread
import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.google.firebase.firestore.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class FetchHomeFeedUseCase constructor(
    private val firestore: FirebaseFirestore
) {

    data class FetchHomeFeedRequest(
        val user: User,
        val pageSize: Int = 10,
        val fetchFromPage1: Boolean = false
    )

    sealed class Result(data: List<DoubtData>? = null, message: String? = null) {
        class Success(val data: List<DoubtData>) : Result(data, null)
        class ListEnded(): Result(null, null)
        class Error(val message: String) : Result(null, message)
    }

    private var lastDoubtData: DoubtData? = null
    private var collectionCount = -1L
    private var docFetched = 0L

    @WorkerThread
    fun fetchFeedSync(request: FetchHomeFeedRequest): Result {

        // if this is a refresh call then reset all the params,
        // though for now, we don't reset collection count.
        if (request.fetchFromPage1) {
            docFetched = 0
            lastDoubtData = null
        }

        // predetermine the count of total doubts to paginate accordingly.
        val fetchCountResult = fetchCollectionCountIfNotDoneAlready()

        if (fetchCountResult != null)
            return fetchCountResult

        // doc fetched should be less than total count in order to make a new call.
        if (collectionCount <= docFetched)
            return Result.ListEnded()

        val latch = CountDownLatch(1)
        var result: Result? = null

        val query = getHomeFeedQuery(request) ?: return Result.Error("cannot create feed firestore query!")

        query.get().addOnSuccessListener {

                // iterate on list, map to DoubtData and return result
                Thread {
                    val docs = mutableListOf<DoubtData>()

                    for (document in it.documents) {
                        try {
                            val doubt = DoubtData.parse(document) ?: continue
                            docs.add(doubt) // will be caught if some error occurs
                        } catch (e: Exception) {
                            /* no-op */
                        }
                    }

                    if (it.documents.isNotEmpty()) {
                        lastDoubtData = DoubtData.parse(it.documents.last())
                    }

                    docFetched += it.documents.size

                    result = Result.Success(docs)
                    latch.countDown()

                }.start()

            }.addOnFailureListener {
                result = Result.Error(it.message ?: "some error occurs")
                latch.countDown()
            }

        latch.await(20, TimeUnit.SECONDS)

        return result!!
    }

    private fun getHomeFeedQuery(request: FetchHomeFeedRequest): Query? {
        var query = firestore.collection(FirestoreCollection.AllDoubts)
            .orderBy(FieldPath.of("score"), Query.Direction.DESCENDING)

        if (lastDoubtData != null)
            query =
                query.startAfter(lastDoubtData!!.score) // by doc ref doesn't work

        // if total size = 33 and docFetched = 30, then request only 3 more,
        // else request page size.
        val size = if (collectionCount - docFetched < request.pageSize)
            collectionCount - docFetched
        else
            request.pageSize

        query.limit(size.toLong())

        return query
    }

    private fun fetchCollectionCountIfNotDoneAlready(): Result? {
        if (collectionCount == -1L) {
            val latch = CountDownLatch(1)

            firestore.collection(FirestoreCollection.AllDoubts).count()
                .get(AggregateSource.SERVER).addOnSuccessListener {
                    collectionCount = it.count
                    latch.countDown()
                }.addOnFailureListener {
                    latch.countDown()
                }

            latch.await(10, TimeUnit.SECONDS)

            Log.d("doubts count :", "$collectionCount")

            if (collectionCount == -1L)
                return Result.Error("some error occurred!")
        }

        return null
    }

}