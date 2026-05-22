package com.example.roomtrack.api

import com.example.roomtrack.app.RoomTrackApp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL = "https://roomtrack-backend.onrender.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")

            // Attach Bearer token from RoomTrackApp if user is logged in
            val token = RoomTrackApp.getInstance().getToken()
            if (token.isNotEmpty()) {
                requestBuilder.addHeader("Authorization", token)
            }

            chain.proceed(requestBuilder.build())
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthApiService = retrofit.create(AuthApiService::class.java)
    val profileService: ProfileApiService = retrofit.create(ProfileApiService::class.java)
    val roomService: RoomApiService = retrofit.create(RoomApiService::class.java)
    val paymentService: PaymentApiService = retrofit.create(PaymentApiService::class.java)
    val announcementService: AnnouncementApiService = retrofit.create(AnnouncementApiService::class.java)
}
