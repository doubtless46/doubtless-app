package com.doubtless.doubtless.screens.answers

import androidx.lifecycle.*
import com.doubtless.doubtless.screens.answers.usecases.FetchAnswerUseCase
import com.doubtless.doubtless.screens.answers.usecases.PublishAnswerUseCase
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.doubt.DoubtData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnswersViewModel(
    private val fetchAnswerUseCase: FetchAnswerUseCase,
    private val publishAnswerUseCase: PublishAnswerUseCase,
    private val userManager: UserManager,
    private val doubtData: DoubtData
) : ViewModel() {

    private val _answerDoubtEntities = MutableLiveData(
        listOf(
            AnswerDoubtEntity(AnswerDoubtEntity.TYPE_DOUBT, doubtData, null),
            AnswerDoubtEntity(AnswerDoubtEntity.TYPE_ANSWER_ENTER, null, null)
        )
    )

    val answerDoubtEntities: LiveData<List<AnswerDoubtEntity>?> = _answerDoubtEntities

    private val _publishAnswerStatus = MutableLiveData<PublishAnswerUseCase.Result>()
    val publishAnswerStatus: LiveData<PublishAnswerUseCase.Result> = _publishAnswerStatus

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

    fun publishAnswer(publishAnswerRequest: PublishAnswerRequest) =
        viewModelScope.launch(Dispatchers.Main) {

            PublishAnswerUseCase.Result.Loading(true)
            val result = publishAnswerUseCase.publish(publishAnswerRequest)
            // get from result instead
            _publishAnswerStatus.postValue(result)
            PublishAnswerUseCase.Result.Loading(false)
        }

    fun notifyAnswersConsumed() {
        _answerDoubtEntities.postValue(null)
    }

    companion object {
        class Factory constructor(
            private val fetchAnswerUseCase: FetchAnswerUseCase,
            private val publishAnswerUseCase: PublishAnswerUseCase,
            private val userManager: UserManager,
            private val doubtData: DoubtData
        ) : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AnswersViewModel(fetchAnswerUseCase, publishAnswerUseCase, userManager, doubtData) as T
            }
        }
    }

}