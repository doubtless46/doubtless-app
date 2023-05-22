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

    data class FetchHomeFeedRequest(val user: User, val pageSize: Int = 10)

    sealed class Result(data: List<DoubtData>? = null, message: String? = null) {
        class Success(val data: List<DoubtData>) : Result(data, null)
        class Error(val message: String) : Result(null, message)
    }

    private var lastDocumentSnapshot: DocumentSnapshot? = null
    private var collectionCount = -1L
    private var docFetched = 0L

    @WorkerThread
    fun fetchFeedSync(request: FetchHomeFeedRequest): Result {

        // predetermine the count of total doubts to paginate accordingly.
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

        // doc fetched should be less than total count in order to make a new call.
        if (collectionCount <= docFetched) {
            return Result.Error("List Ended!")
        }

        val latch = CountDownLatch(1)
        var result: Result? = null

        var query = firestore.collection(FirestoreCollection.AllDoubts)
            .orderBy(FieldPath.of("score"), Query.Direction.DESCENDING)

        if (lastDocumentSnapshot != null)
            query =
                query.startAfter(lastDocumentSnapshot!!.toObject(DoubtData::class.java)!!.score) // by doc ref doesn't work

        // if total size = 33 and docFetched = 30, then request only 3 more,
        // else request page size.
        val size = if (collectionCount - docFetched < request.pageSize)
            collectionCount - docFetched
        else
            request.pageSize

        Log.d(
            "last doubt",
            lastDocumentSnapshot?.toObject(DoubtData::class.java)?.heading.toString() + " "
                    + collectionCount + " " + docFetched + " " + size
        )

        query.limit(size.toLong())
            .get().addOnSuccessListener {

                // iterate on list, map to DoubtData and return result
                Thread {
                    val docs = mutableListOf<DoubtData>()

                    for (document in it.documents) {
                        try {
                            docs.add(document.toObject(DoubtData::class.java)!!) // will be caught if some error occurs
                        } catch (e: Exception) {
                            /* no-op */
                        }
                    }

                    if (it.documents.isNotEmpty()) {
                        lastDocumentSnapshot = it.documents.last()
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

}