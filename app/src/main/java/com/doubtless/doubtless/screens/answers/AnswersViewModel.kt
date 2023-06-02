package com.doubtless.doubtless.screens.answers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.screens.answers.usecases.FetchAnswerUseCase
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.doubt.view.ViewDoubtsViewModel
import com.doubtless.doubtless.screens.home.FeedEntity
import com.doubtless.doubtless.screens.home.usecases.FetchHomeFeedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnswersViewModel(
    private val fetchAnswerUseCase: FetchAnswerUseCase,
    private val analyticsTracker: AnalyticsTracker,
    private val userManager: UserManager,
) : ViewModel() {
    private val _answerDoubtEntities = mutableListOf<AnswerDoubtEntity>()
    val answerDoubtEntities: List<AnswerDoubtEntity> = _answerDoubtEntities

    private val _answerDoubtEntitiesIds: MutableMap<String, Int> = mutableMapOf()

    private var isLoading = false

    private val _fetchedAnswerDoubtEntities = MutableLiveData<List<AnswerDoubtEntity>?>()
    val fetchedAnswerDoubtEntities: LiveData<List<AnswerDoubtEntity>?> =
        _fetchedAnswerDoubtEntities // TODO : use Result here!

    fun notifyFetchedDoubtsConsumed() {
        _fetchedAnswerDoubtEntities.value = null
    }

    fun fetchAnswers(forPageOne: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {

        if (isLoading) return@launch

        isLoading = true

        val result = FetchAnswerUseCase.fetchFeedSync(
            request = FetchAnswerUseCase.FetchAnswerFeedRequest(
                user = userManager.getCachedUserData()!!,
                fetchFromPage1 = forPageOne
            )
        )

        if (result is FetchAnswerUseCase.Result.ListEnded || result is FetchAnswerUseCase.Result.Error) {
            // ERROR CASE
            _fetchedAnswerDoubtEntities.postValue(null)
            isLoading = false
            return@launch
        }

        result as FetchAnswerUseCase.Result.Success

        if (!forPageOne) {
            analyticsTracker.trackFeedNextPage(answerDoubtEntities.size)
        } else {
            analyticsTracker.trackFeedRefresh()
        }

        val entitiesFromServer = mutableListOf<AnswerDoubtEntity>()

        result.data.forEach { answerData ->

            // we got the data for page 2 (lets say) now check if these posts existed on page 1 and add only unique ones.
            if (_answerDoubtEntitiesIds.contains(answerData.id) == false) {
                entitiesFromServer.add(answerData.toHomeEntity())
                _answerDoubtEntitiesIds[answerData.id!!] = 1
            }
        }

        // for page 1 call add search entity
        if (_answerDoubtEntities.isEmpty())
            entitiesFromServer.add(0, AnswerDoubtEntity.getSearchEntity())

        _answerDoubtEntities.addAll(entitiesFromServer)
        _fetchedAnswerDoubtEntities.postValue(entitiesFromServer)
        fetchAnswerUseCase.notifyDistinctDocsFetched(
            docsFetched = answerDoubtEntities.size
                    - /* subtract one for search entity, ideally should have counted Type = Doubt size */ 1
        )
        isLoading = false
    }

    fun refreshList() {
        _answerDoubtEntities.clear()
        _answerDoubtEntitiesIds.clear()
        fetchAnswers(forPageOne = true)
    }


}