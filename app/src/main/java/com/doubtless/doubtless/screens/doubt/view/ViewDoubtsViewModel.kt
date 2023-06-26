package com.doubtless.doubtless.screens.doubt.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.home.entities.FeedEntity
import com.doubtless.doubtless.screens.home.usecases.FetchHomeFeedUseCase
import com.doubtless.doubtless.screens.home.usecases.FetchHomeFeedUseCase.FetchHomeFeedRequest
import com.doubtless.doubtless.screens.home.usecases.FetchHomeFeedUseCase.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.set

class ViewDoubtsViewModel constructor(
    private val fetchHomeFeedUseCase: FetchHomeFeedUseCase,
    private val analyticsTracker: AnalyticsTracker,
    private val userManager: UserManager
) : ViewModel() {

    private val _homeEntities = mutableListOf<FeedEntity>()
    val homeEntities: List<FeedEntity> = _homeEntities

    private val _homeEntitiesIds: MutableMap<String, Int> = mutableMapOf()

    private var isLoading = false

    private val _fetchedHomeEntities = MutableLiveData<List<FeedEntity>?>()
    val fetchedHomeEntities: LiveData<List<FeedEntity>?> =
        _fetchedHomeEntities // TODO : use Result here!

    fun notifyFetchedDoubtsConsumed() {
        _fetchedHomeEntities.value = null
    }

    fun fetchDoubts(forPageOne: Boolean = false, feedTag: String) =
        viewModelScope.launch(Dispatchers.IO) {

            if (isLoading) return@launch

            isLoading = true

            val result = fetchHomeFeedUseCase.fetchFeedSync(
                request = FetchHomeFeedRequest(
                    user = userManager.getCachedUserData()!!,
                    fetchFromPage1 = forPageOne,
                    tag = feedTag
                )
            )

            if (result is Result.ListEnded || result is Result.Error) {
                // ERROR CASE
                _fetchedHomeEntities.postValue(null)
                isLoading = false
                return@launch
            }

            result as Result.Success

            if (!forPageOne) {
                analyticsTracker.trackFeedNextPage(homeEntities.size)
            } else {
                analyticsTracker.trackFeedRefresh()
            }

            val entitiesFromServer = mutableListOf<FeedEntity>()

            result.data.forEach { doubtData ->

                // we got the data for page 2 (lets say) now check if these posts existed on page 1 and add only unique ones.
                if (_homeEntitiesIds.contains(doubtData.id) == false) {
                    entitiesFromServer.add(doubtData.toHomeEntity())
                    _homeEntitiesIds[doubtData.id!!] = 1
                }
            }

            // for page 1 call add search entity
//        if (_homeEntities.isEmpty())
//            entitiesFromServer.add(0, FeedEntity.getSearchEntity())

            _homeEntities.addAll(entitiesFromServer)
            _fetchedHomeEntities.postValue(entitiesFromServer)
            fetchHomeFeedUseCase.notifyDistinctDocsFetched(
                docsFetched = homeEntities.size
                        - /* subtract one for search entity, ideally should have counted Type = Doubt size */ 1
            )
            isLoading = false
        }

    fun refreshList(tag: String?) {
        _homeEntities.clear()
        _homeEntitiesIds.clear()
        Log.i("tags", tag!!)
        fetchDoubts(forPageOne = true, feedTag = tag!!)
    }

    companion object {
        class Factory constructor(
            private val fetchHomeFeedUseCase: FetchHomeFeedUseCase,
            private val analyticsTracker: AnalyticsTracker,
            private val userManager: UserManager
        ) : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ViewDoubtsViewModel(fetchHomeFeedUseCase, analyticsTracker, userManager) as T
            }
        }
    }

}