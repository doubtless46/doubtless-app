package com.doubtless.doubtless.screens.dashboard.usecases

import android.util.Log
import androidx.annotation.WorkerThread
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class FetchUserDataUseCase constructor(
    private val fetchUserFeedByDateUseCase: FetchUserFeedByDateUseCase,
    private val firestore: FirebaseFirestore
) {

    private val userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()


    data class FetchUserFeedRequest(
        val user: User,
        val pageSize: Int = 10,
        val fetchFromPage1: Boolean = false
    )

    sealed class Result() {
        class Success(val data: List<DoubtData>) : Result()
        class ListEnded : Result()
        class Error(val message: String) : Result()
    }

    private var lastDoubtData: DoubtData? = null
    private val NOT_SET = -1L
    private val FEED_ENDED = 0L
    private var collectionCount = NOT_SET

    private var docFetched = 0L

    @WorkerThread
    suspend fun fetchFeedSync(request: FetchUserFeedRequest): Result = withContext(Dispatchers.IO) {

        // if this is a refresh call then reset all the params,
        // though for now, we don't reset collection count.
        if (request.fetchFromPage1) {
            docFetched = 0
            lastDoubtData = null
        }
//
//        // predetermine the count of total doubts to paginate accordingly.
        val fetchCountResult = fetchCollectionCountIfNotDoneAlready()

        if (fetchCountResult != null)
            return@withContext fetchCountResult
//
//        // doc fetched should be less than total count in order to make a new call.
        if (collectionCount <= docFetched || collectionCount == FEED_ENDED)
            return@withContext Result.ListEnded()
//
//        // if total size = 33 and docFetched = 30, then request only 3 more,
//        // else request page size.
        val size: Int = if (collectionCount - docFetched < request.pageSize)
            (collectionCount - docFetched).toInt()
        else request.pageSize

        val userFeedByDateJob = async {
            fetchUserFeedByDateUseCase.getFeedData(
                request.copy(pageSize = size),
                userManager
            ) // ratio
        }

        val resultDate = userFeedByDateJob.await()
        if (resultDate is FetchUserFeedByDateUseCase.Result.Error) {
            return@withContext Result.Error(resultDate.message)
        }
        resultDate as FetchUserFeedByDateUseCase.Result.Success

        return@withContext Result.Success(resultDate.data)
    }

    private fun fetchCollectionCountIfNotDoneAlready(): Result? {
        if (collectionCount == NOT_SET) {
            val latch = CountDownLatch(1)

            firestore.collection(FirestoreCollection.AllDoubts).whereEqualTo(
                "author_id", userManager.getCachedUserData()!!.id
            )
                .count()
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

    fun notifyDistinctDocsFetched(docsFetched: Int) {
        this.docFetched = docsFetched.toLong()
    }

    fun notifyEffectiveFeedEnded() {
        collectionCount = FEED_ENDED
    }

}
