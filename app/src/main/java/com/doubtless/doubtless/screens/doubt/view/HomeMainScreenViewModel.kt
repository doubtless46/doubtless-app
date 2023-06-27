package com.doubtless.doubtless.screens.doubt.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.doubtless.doubtless.screens.doubt.usecases.FetchFilterTagsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeMainScreenViewModel constructor(
    private val fetchFilterTagsUseCase: FetchFilterTagsUseCase,
) : ViewModel() {

    private val _tags = MutableLiveData<List<String>?>()
    val tags: LiveData<List<String>?> = _tags

    fun fetchTags() = viewModelScope.launch(Dispatchers.IO) {

        val result = fetchFilterTagsUseCase.fetchTagsFromFirebase()

        if (result is FetchFilterTagsUseCase.Result.Error) {
            _tags.postValue(null)
            return@launch
        }

        result as FetchFilterTagsUseCase.Result.Success

        val tags = result.data.toMutableList()
        tags.addAll(0, listOf("My College", "All"))
        _tags.postValue(tags)
    }


    companion object {
        class Factory constructor(
            private val fetchFilterTagsUseCase: FetchFilterTagsUseCase
        ) : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeMainScreenViewModel(fetchFilterTagsUseCase) as T
            }
        }
    }
}