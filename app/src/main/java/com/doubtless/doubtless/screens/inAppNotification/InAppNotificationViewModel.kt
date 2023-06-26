package com.doubtless.doubtless.screens.inAppNotification

import androidx.lifecycle.*
import com.doubtless.doubtless.screens.inAppNotification.model.InAppNotificationEntity
import com.doubtless.doubtless.screens.inAppNotification.usecases.FetchInAppNotificationUseCase
import com.doubtless.doubtless.screens.inAppNotification.usecases.FetchUnreadNotificationUseCase
import com.doubtless.doubtless.screens.inAppNotification.usecases.MarkInAppNotificationsReadUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InAppNotificationViewModel constructor(
    private val fetchInAppNotificationUseCase: FetchInAppNotificationUseCase,
    private val markInAppNotificationsReadUseCase: MarkInAppNotificationsReadUseCase
) : ViewModel() {

    sealed class Result {
        class Success(
            val notifications: List<InAppNotificationEntity>,
            val additionalError: String? = null
        ) : Result()

        class NoData(val additionalError: String? = null) : Result()
        class Error(val message: String) : Result()
        object Loading : Result()
    }

    private val _notificationStatus = MutableLiveData<Result>()
    val notificationStatus: LiveData<Result> = _notificationStatus

    fun fetchNotification() = CoroutineScope(Dispatchers.IO).launch {

        _notificationStatus.postValue(Result.Loading)

        val result = fetchInAppNotificationUseCase.getNotifications()

        when (result) {
            is FetchInAppNotificationUseCase.Result.Success -> {
                if (result.notifications.isNotEmpty())
                    _notificationStatus.postValue(
                        Result.Success(
                            notifications = result.notifications.distinctBy {
                                it.notificationId
                            }.sortedByDescending {
                                it.createdOn?.toDate()?.time
                            },
                            additionalError = result.error
                        )
                    )
                else
                    _notificationStatus.postValue(Result.NoData(result.error))
            }

            is FetchInAppNotificationUseCase.Result.Error -> {
                _notificationStatus.postValue(Result.Error(result.message))
            }
        }
    }

    fun markNotificationsAsRead() = CoroutineScope(Dispatchers.IO).launch {
        if (_notificationStatus.value is Result.Success && _notificationStatus.value != null) {

            val notifs =
                (_notificationStatus.value!! as Result.Success).notifications.toMutableList()

            markInAppNotificationsReadUseCase
                .markRead(notifs)

            _notificationStatus.postValue(Result.Success(notifs.map {
                it.copy(isRead = true)
            }))
        }
    }

    companion object {
        class Factory constructor(
            private val fetchInAppNotificationUseCase: FetchInAppNotificationUseCase,
            private val markInAppNotificationsReadUseCase: MarkInAppNotificationsReadUseCase
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return InAppNotificationViewModel(
                    fetchInAppNotificationUseCase,
                    markInAppNotificationsReadUseCase
                ) as T
            }
        }
    }

}