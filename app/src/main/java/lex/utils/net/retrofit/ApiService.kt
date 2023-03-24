package lex.utils.net.retrofit

import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("v1/chat/completions")
    suspend fun chatCompletions(
        @Body body: RequestBody,
        @Header("Authorization") appKey: String
    ): Response

    @POST("v1/images/generations")
    suspend fun imagesGenerations(
        @Body body: RequestBody,
        @Header("Authorization") appKey: String
    ): Response
}