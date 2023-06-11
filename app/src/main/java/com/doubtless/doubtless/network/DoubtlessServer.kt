package com.doubtless.doubtless.network

import com.doubtless.doubtless.BuildConfig
import com.doubtless.doubtless.screens.answers.AnswerData
import com.doubtless.doubtless.screens.answers.PublishAnswerRequest
import com.doubtless.doubtless.screens.answers.PublishAnswerResponse
import com.doubtless.doubtless.screens.doubt.DoubtData
import com.doubtless.doubtless.screens.doubt.PublishDoubtRequest
import com.doubtless.doubtless.screens.search.SearchResult
import retrofit2.Response
import retrofit2.http.*

interface DoubtlessServer {
    // search api : (query, userid, category) -> List<SearchEntities>
    // post doubt api

    // TODO :
    // 1. make interceptor

    @POST("doubts")
    suspend fun publishDoubt(
        @Body publishDoubtRequest: PublishDoubtRequest,
        @Header("is_debug") debug: Boolean = BuildConfig.DEBUG,
        @Header("app_version") appVersion: String = BuildConfig.VERSION_NAME,
    ) : Response<Unit>

    @POST("doubts/answer")
    suspend fun publishAnswer(
        @Body publishAnswerReq: PublishAnswerRequest,
        @Header("is_debug") debug: Boolean = BuildConfig.DEBUG,
        @Header("app_version") appVersion: String = BuildConfig.VERSION_NAME,
    ): PublishAnswerResponse

    @POST("search")
    suspend fun searchDoubts(
        @Body query: String,
        @Header("Content-Type") contentType: String = "text/plain"
//        @Header("is_debug") debug: Boolean = BuildConfig.DEBUG,
//        @Header("app_version") appVersion: String = BuildConfig.VERSION_NAME,
    ): List<SearchResult>
}