package com.doubtless.doubtless.screens.answers

import androidx.lifecycle.*
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.screens.answers.usecases.FetchAnswerUseCase
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.doubt.view.ViewDoubtsViewModel
import com.doubtless.doubtless.screens.home.usecases.FetchHomeFeedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnswersViewModel(
    private val fetchAnswerUseCase: FetchAnswerUseCase,
    private val userManager: UserManager,
    doubtData: DoubtData
) : ViewModel() {

    private val _answerDoubtEntities = MutableLiveData(
        listOf(
            AnswerDoubtEntity(AnswerDoubtEntity.TYPE_DOUBT, doubtData, null),
            AnswerDoubtEntity(AnswerDoubtEntity.TYPE_ANSWER_ENTER, null, null)
        )
    )

    val answerDoubtEntities: LiveData<List<AnswerDoubtEntity>> = _answerDoubtEntities

    fun fetchAnswers() = viewModelScope.launch(Dispatchers.IO) {
        val result = fetchAnswerUseCase.fetchAnswers()

        if (result is FetchAnswerUseCase.Result.Success) {
            _answerDoubtEntities.postValue(result.data.map {
                AnswerData.toAnswerDoubtEntity(it)
            })
        } else {
            /* no-op */
        }
    }

    companion object {
        class Factory constructor(
            private val fetchAnswerUseCase: FetchAnswerUseCase,
            private val userManager: UserManager,
            private val doubtData: DoubtData
        ) : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AnswersViewModel(fetchAnswerUseCase, userManager, doubtData) as T
            }
        }
    }

}