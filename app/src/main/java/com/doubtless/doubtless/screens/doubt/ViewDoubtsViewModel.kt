package com.doubtless.doubtless.screens.doubt

import androidx.lifecycle.*
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.home.usecases.FetchHomeFeedUseCase
import com.doubtless.doubtless.screens.home.usecases.FetchHomeFeedUseCase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewDoubtsViewModel constructor(
    private val fetchHomeFeedUseCase: FetchHomeFeedUseCase,
    private val analyticsTracker: AnalyticsTracker,
    private val userManager: UserManager
) : ViewModel() {

    private val _allDoubts = mutableListOf<DoubtData>()
    val allDoubts: List<DoubtData> = _allDoubts

    private var isLoading = false

    private val _fetchedDoubts = MutableLiveData<List<DoubtData>?>()
    val fetchedDoubts: LiveData<List<DoubtData>?> = _fetchedDoubts // TODO : use Result here!

    fun notifyFetchedDoubtsConsumed() {
        _fetchedDoubts.postValue(null)
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
            _fetchedDoubts.postValue(result.data)

            if (isRefreshCall) {
                analyticsTracker.trackFeedNextPage(allDoubts.size)
            }

            _allDoubts.addAll(result.data)
        } else {
            // ERROR CASE
            _fetchedDoubts.postValue(null)
        }

        isLoading = false
    }

    fun refreshList() {
        _allDoubts.clear()
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