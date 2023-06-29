package com.doubtless.doubtless.utils

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val error: Exception? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T> : Resource<T>()
    class Error<T>(message: String? = null, data: T? = null, error: Exception? = null) :
        Resource<T>(data, message, error)
}