package com.doubtless.doubtless.screens.doubt

import android.util.Log
import androidx.lifecycle.*
import com.doubtless.doubtless.constants.FirestoreCollection
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.screens.home.network.FetchHomeFeedUseCase
import com.doubtless.doubtless.screens.home.network.FetchHomeFeedUseCase.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ViewDoubtsViewModel constructor(
    private val fetchHomeFeedUseCase: FetchHomeFeedUseCase,
    private val userManager: UserManager
) : ViewModel() {

    private val _allDoubts = MutableLiveData<List<DoubtData>>()
    val allDoubts: LiveData<List<DoubtData>> = _allDoubts

    private var isLoading = false

    fun fetchDoubts() = viewModelScope.launch(Dispatchers.IO) {

        if (isLoading) return@launch

        isLoading = true

        val result = fetchHomeFeedUseCase.fetchFeedSync(
            request = FetchHomeFeedRequest(userManager.getCachedUserData()!!)
        )

        if (result is Result.Success && result.data.isNotEmpty()) {
            _allDoubts.postValue(result.data)
        } else {
            // ERROR CASE
        }

        isLoading = false
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