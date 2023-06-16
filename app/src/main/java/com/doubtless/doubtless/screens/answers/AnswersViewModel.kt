package com.doubtless.doubtless.screens.answers

import androidx.lifecycle.*
import com.doubtless.doubtless.R
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

    private val _publishAnswerLoading = MutableLiveData<PublishAnswerUseCase.Resource<Boolean>>()
    val publishAnswerLoading: LiveData<PublishAnswerUseCase.Resource<Boolean>> = _publishAnswerLoading

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
            if (_publishAnswerLoading.value is PublishAnswerUseCase.Resource.Loading<Boolean>) {
                // Loading state is already true, return immediately
                return@launch
            }

            _publishAnswerLoading.value = PublishAnswerUseCase.Resource.Loading()
            val result = publishAnswerUseCase.publish(publishAnswerRequest, object :
                PublishAnswerUseCase.PublishAnswerListener {
                override fun onPublishAnswerLoading(isLoading: Boolean) {
                    _publishAnswerLoading.postValue(PublishAnswerUseCase.Resource.Loading(isLoading))
                }
            })

            _publishAnswerStatus.postValue(result)
            _publishAnswerLoading.postValue(PublishAnswerUseCase.Resource.Success(false))
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