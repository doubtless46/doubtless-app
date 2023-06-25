package com.doubtless.doubtless.screens.answers

import androidx.lifecycle.*
import com.doubtless.doubtless.DoubtlessApp
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

    sealed class Result {
        class Success(val data: List<AnswerDoubtEntity>) : Result()
        object Loading : Result()
        class Error(val message: String) : Result()
    }

    private val _answerDoubtEntities = MutableLiveData<Result>(
        Result.Success(
            listOf(
                AnswerDoubtEntity(
                    type = AnswerDoubtEntity.TYPE_DOUBT,
                    doubt = doubtData,
                    answer = null,
                    answerVotingUseCase = null,
                    doubtVotingUseCase = DoubtlessApp.getInstance().getAppCompRoot()
                        .getDoubtVotingDoubtCase(doubtData)
                ),
                AnswerDoubtEntity(AnswerDoubtEntity.TYPE_ANSWER_ENTER, null, null)
            )
        )
    )

    val answerDoubtEntities: LiveData<Result> = _answerDoubtEntities

    private val _publishAnswerStatus = MutableLiveData<PublishAnswerUseCase.Result>()
    val publishAnswerStatus: LiveData<PublishAnswerUseCase.Result> = _publishAnswerStatus


    fun fetchAnswers() = viewModelScope.launch(Dispatchers.IO) {

        _answerDoubtEntities.postValue(Result.Loading)

        val result = fetchAnswerUseCase.fetchAnswers()

        when (result) {
            is FetchAnswerUseCase.Result.Success -> {
                _answerDoubtEntities.postValue(
                    Result.Success(result.data.map {
                        AnswerData.toAnswerDoubtEntity(it)
                    })
                )
            }
            else -> {
                _answerDoubtEntities.postValue(
                    Result.Error(
                        (result as FetchAnswerUseCase.Result.Error).message
                    )
                )
            }
        }
    }

    fun publishAnswer(publishAnswerRequest: PublishAnswerRequest) =
        viewModelScope.launch(Dispatchers.Main) {
            _publishAnswerStatus.value = PublishAnswerUseCase.Result.Loading
            val result = publishAnswerUseCase.publish(publishAnswerRequest)
            // get from result instead
            _publishAnswerStatus.postValue(result)
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
                return AnswersViewModel(
                    fetchAnswerUseCase,
                    publishAnswerUseCase,
                    userManager,
                    doubtData
                ) as T
            }
        }
    }

}