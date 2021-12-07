package com.bearddr.calatour.chat.remote

import com.bearddr.calatour.BuildConfig
import com.bearddr.calatour.util.Constants.authorization
import com.bearddr.calatour.util.Constants.contentTypeJson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ChatApi {

    @Headers(contentTypeJson)
    @POST(authenticationRoute)
    fun authenticate(@Body body: Requests.Authentication): Call<Responses.Authentication>

    @Headers(contentTypeJson)
    @HTTP(method = "DELETE", path = logoutRoute, hasBody = true)
    fun globalLogout(@Body body: Requests.Authentication): Call<Void>

    @Headers(contentTypeJson)
    @DELETE(logoutRoute)
    fun logout(@Header(authorization) header: String): Call<Void>

    @Headers(contentTypeJson)
    @PUT(sendMessageRoute)
    fun sendMessage(@Header(authorization) header: String, @Body body: Requests.SendMessage): Call<Void>

    @Headers(contentTypeJson)
    @GET(readMessagesRoute)
    fun readMessages(@Header(authorization) header: String): Call<Responses.ReadMessages>

    companion object {
        private val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        private val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(logger)
        }.build()

        fun create(): ChatApi {
            val retrofitInstance = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()

            return retrofitInstance.create(ChatApi::class.java)
        }
    }
}

private const val authenticationRoute = "authenticate.php"
private const val logoutRoute = "logout.php"
private const val sendMessageRoute = "sendmessage.php"
private const val readMessagesRoute = "readmessages.php"
