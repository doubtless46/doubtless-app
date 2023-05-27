package com.doubtless.doubtless.screens.search.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExtractKeywordsUseCase {

    suspend fun notifyNewInput(input: String): List<String> = withContext(Dispatchers.IO) {
        return@withContext input.toLowerCase().split("\\s+".toRegex())
    }

}