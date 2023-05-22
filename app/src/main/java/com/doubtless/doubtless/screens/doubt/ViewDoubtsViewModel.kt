package com.doubtless.doubtless.screens.doubt

import androidx.lifecycle.*
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.home.network.FetchHomeFeedUseCase
import com.doubtless.doubtless.screens.home.network.FetchHomeFeedUseCase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewDoubtsViewModel constructor(
    private val fetchHomeFeedUseCase: FetchHomeFeedUseCase,
    private val userManager: UserManager
) : ViewModel() {

    private val _allDoubts = mutableListOf<DoubtData>()
    val allDoubts: List<DoubtData> = _allDoubts

    private var isLoading = false

    private val _fetchedDoubts = MutableLiveData<List<DoubtData>?>()
    val fetchedDoubts: LiveData<List<DoubtData>?> = _fetchedDoubts

    fun notifyFetchedDoubtsConsumed() {
        _fetchedDoubts.postValue(null)
    }

    fun fetchDoubts(refreshCall: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {

        if (isLoading) return@launch

        isLoading = true

        val result = fetchHomeFeedUseCase.fetchFeedSync(
            request = FetchHomeFeedRequest(
                user = userManager.getCachedUserData()!!,
                fetchFromPage1 = refreshCall
            )
        )

        if (result is Result.Success && result.data.isNotEmpty()) {
            _fetchedDoubts.postValue(result.data)
            _allDoubts.addAll(result.data)
        } else {
            // ERROR CASE
            _fetchedDoubts.postValue(null)
        }

        isLoading = false
    }

    fun refreshList() {
        _allDoubts.clear()
        fetchDoubts(refreshCall = true)
    }

    companion object {
        class Factory constructor(
            private val fetchHomeFeedUseCase: FetchHomeFeedUseCase,
            private val userManager: UserManager
        ) : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ViewDoubtsViewModel(fetchHomeFeedUseCase, userManager) as T
            }
        }
    }

}