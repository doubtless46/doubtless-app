package com.doubtless.doubtless.screens.doubt.view

import androidx.lifecycle.*
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.home.FeedEntity
import com.doubtless.doubtless.screens.home.usecases.FetchHomeFeedUseCase
import com.doubtless.doubtless.screens.home.usecases.FetchHomeFeedUseCase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewDoubtsViewModel constructor(
    private val fetchHomeFeedUseCase: FetchHomeFeedUseCase,
    private val analyticsTracker: AnalyticsTracker,
    private val userManager: UserManager
) : ViewModel() {

    private val _homeEntities = mutableListOf<FeedEntity>()
    val homeEntities: List<FeedEntity> = _homeEntities

    private var isLoading = false

    private val _fetchedHomeEntities = MutableLiveData<List<FeedEntity>?>()
    val fetchedHomeEntities: LiveData<List<FeedEntity>?> =
        _fetchedHomeEntities // TODO : use Result here!

    fun notifyFetchedDoubtsConsumed() {
        _fetchedHomeEntities.postValue(null)
    }

    fun fetchDoubts(isRefreshCall: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {

        if (isLoading) return@launch

        isLoading = true

        val result = fetchHomeFeedUseCase.fetchFeedSync(
            request = FetchHomeFeedRequest(
                user = userManager.getCachedUserData()!!,
                fetchFromPage1 = isRefreshCall
            )
        )

        if (result is Result.Success && result.data.isNotEmpty()) {

            if (!isRefreshCall) {
                analyticsTracker.trackFeedNextPage(homeEntities.size)
            } else {
                analyticsTracker.trackFeedRefresh()
            }

            val homeEntitiesFromServer = mutableListOf<FeedEntity>()

            result.data.forEach {
                homeEntitiesFromServer.add(it.toHomeEntity())
            }

            // for page 1 call add search entity
            if (_homeEntities.isEmpty())
                _homeEntities.add(FeedEntity.getSearchEntity())

            _homeEntities.addAll(homeEntitiesFromServer)
            _fetchedHomeEntities.postValue(_homeEntities)

        } else {
            // ERROR CASE
            _fetchedHomeEntities.postValue(null)
        }

        isLoading = false
    }

    fun refreshList() {
        _homeEntities.clear()
        fetchDoubts(isRefreshCall = true)
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