package com.doubtless.doubtless.screens.doubt.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.doubtless.doubtless.screens.doubt.PublishDoubtRequest
import com.doubtless.doubtless.screens.doubt.usecases.PostDoubtUseCase
import kotlinx.coroutines.launch

class CreateDoubtViewModel constructor(
    private val postDoubtUseCase: PostDoubtUseCase
) : ViewModel() {

    sealed class Result {
        object Success : Result()
        class Error(val message: String): Result()
    }

    private val _postDoubtStatus = MutableLiveData<Result>()
    val postDoubtStatus: LiveData<Result> = _postDoubtStatus

    fun postDoubt(publishDoubtRequest: PublishDoubtRequest) = viewModelScope.launch {
         val result = postDoubtUseCase.post(publishDoubtRequest)

        if (result is PostDoubtUseCase.Result.Success) {
            _postDoubtStatus.postValue(Result.Success)
        } else {
            _postDoubtStatus.postValue(Result.Error((result as PostDoubtUseCase.Result.Error).message))
        }
    }

    companion object {
        class Factory constructor(
            private val postDoubtUseCase: PostDoubtUseCase
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreateDoubtViewModel(postDoubtUseCase) as T
            }
        }
    }

}