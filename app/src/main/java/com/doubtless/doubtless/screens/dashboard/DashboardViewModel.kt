package com.doubtless.doubtless.screens.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.dashboard.usecases.FetchUserDataUseCase
import com.doubtless.doubtless.screens.home.entities.FeedEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val fetchUserDataUseCase: FetchUserDataUseCase,
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
        _fetchedHomeEntities.postValue(null)
    }

    fun fetchDoubts(forPageOne: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {

        if (isLoading) return@launch

        isLoading = true
        val result = fetchUserDataUseCase.fetchFeedSync(
            request = FetchUserDataUseCase.FetchUserFeedRequest(
                user = userManager.getCachedUserData()!!, fetchFromPage1 = forPageOne
            )
        )

        if (result is FetchUserDataUseCase.Result.ListEnded || result is FetchUserDataUseCase.Result.Error) {
            // ERROR CASE
            _fetchedHomeEntities.postValue(null)
            isLoading = false
            return@launch
        }

        result as FetchUserDataUseCase.Result.Success

        if (!forPageOne) {
            analyticsTracker.trackFeedNextPage(homeEntities.size)
        } else {
            analyticsTracker.trackFeedRefresh()
        }

        val entitiesFromServer = mutableListOf<FeedEntity>()

        result.data.forEach { doubtData ->
            // we got the data for page 2 (lets say) now check if these posts existed on page 1 and add only unique ones.
            if (!_homeEntitiesIds.contains(doubtData.id)) {
                entitiesFromServer.add(doubtData.toHomeEntity())
                _homeEntitiesIds[doubtData.id!!] = 1
            }
        }


        // for page 1 call add user profile entity
        if (_homeEntities.isEmpty()) entitiesFromServer.add(
            0,
            FeedEntity(FeedEntity.TYPE_USER_PROFILE, null)
        )

        _homeEntities.addAll(entitiesFromServer)
        _fetchedHomeEntities.postValue(entitiesFromServer)
        fetchUserDataUseCase.notifyDistinctDocsFetched(
            docsFetched = homeEntities.size
//                    - /* subtract one for search entity, ideally should have counted Type = Doubt size */ 1
        )
        isLoading = false

    }

    fun refreshList() {
        _homeEntities.clear()
        _homeEntitiesIds.clear()
        fetchDoubts(forPageOne = true)
    }

    companion object {
        class Factory constructor(
            private val fetchUserDataUseCase: FetchUserDataUseCase,
            private val analyticsTracker: AnalyticsTracker,
            private val userManager: UserManager
        ) : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(
                    fetchUserDataUseCase, analyticsTracker, userManager
                ) as T
            }
        }
    }
}