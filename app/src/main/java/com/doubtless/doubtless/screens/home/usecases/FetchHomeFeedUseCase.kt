package com.doubtless.doubtless.screens.home.usecases

import android.util.Log
import androidx.annotation.WorkerThread
import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class FetchHomeFeedUseCase constructor(
    private val fetchFeedByDateUseCase: FetchFeedByDateUseCase,
    private val fetchFeedByPopularityUseCase: FetchFeedByPopularityUseCase,
    private val firestore: FirebaseFirestore
) {

    data class FetchHomeFeedRequest(
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
    private var collectionCount = -1L
    private var docFetched = 0L

    @WorkerThread
    suspend fun fetchFeedSync(request: FetchHomeFeedRequest): Result = withContext(Dispatchers.IO) {

        // if this is a refresh call then reset all the params,
        // though for now, we don't reset collection count.
        if (request.fetchFromPage1) {
            docFetched = 0
            lastDoubtData = null
        }

        // predetermine the count of total doubts to paginate accordingly.
        val fetchCountResult = fetchCollectionCountIfNotDoneAlready()

        if (fetchCountResult != null)
            return@withContext fetchCountResult

        // doc fetched should be less than total count in order to make a new call.
        if (collectionCount <= docFetched)
            return@withContext Result.ListEnded()

        // if total size = 33 and docFetched = 30, then request only 3 more,
        // else request page size.
        val size: Int = if (collectionCount - docFetched < request.pageSize)
            (collectionCount - docFetched).toInt()
        else
            request.pageSize

        val feedByDateJob = async {
            fetchFeedByDateUseCase.getFeedData(request.copy(pageSize = size / 2)) // ratio
        }
        val feedByPopularityJob = async {
            fetchFeedByPopularityUseCase.getFeedData(request.copy(pageSize = size / 2))
        }

        val resultDate = feedByDateJob.await()
        val resultPopularity = feedByPopularityJob.await()

        if (resultDate is FetchFeedByDateUseCase.Result.Error) {
            return@withContext Result.Error(resultDate.message)
        }

        resultDate as FetchFeedByDateUseCase.Result.Success

        if (resultPopularity is FetchFeedByPopularityUseCase.Result.Error) {
            return@withContext Result.Error(resultPopularity.message)
        }

        resultPopularity as FetchFeedByPopularityUseCase.Result.Success

        val mergedFeed = mergeFeeds(resultDate.data, resultPopularity.data)

        docFetched += mergedFeed.size

        return@withContext Result.Success(mergedFeed)
    }

    private fun mergeFeeds(
        byDate: List<DoubtData>,
        byPopularity: List<DoubtData>
    ): List<DoubtData> {
        val set = hashSetOf<DoubtData>()

        set.addAll(byDate)
        set.addAll(byPopularity)

        return set.shuffled()
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