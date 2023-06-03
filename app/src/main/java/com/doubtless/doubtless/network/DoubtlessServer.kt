package com.doubtless.doubtless.network

import com.doubtless.doubtless.BuildConfig
import com.doubtless.doubtless.screens.answers.PublishAnswerRequest
import com.doubtless.doubtless.screens.doubt.PublishDoubtRequest
import com.doubtless.doubtless.screens.search.SearchResult
import retrofit2.http.*

interface DoubtlessServer {
    // search api : (query, userid, category) -> List<SearchEntities>
    // post doubt api

    @POST("doubts")
    suspend fun publishDoubt(
        @Body publishDoubtRequest: PublishDoubtRequest,
    )

    @POST("doubts/answer")
    suspend fun publishAnswer(
        @Body publishAnswerReq: PublishAnswerRequest,
//        @Header("is_debug") debug: Boolean = BuildConfig.DEBUG,
//        @Header("app_version") appVersion: String = BuildConfig.VERSION_NAME,
    )

    @POST("search")
    suspend fun searchDoubts(
        @Body query: String
    ): SearchResult
}