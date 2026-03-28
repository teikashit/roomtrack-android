package com.example.roomtrack.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://lajmclxicnpxcfkucwsz.supabase.co/"
    private const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imxham1jbHhpY25weGNma3Vjd3N6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI4MDc4MzIsImV4cCI6MjA4ODM4MzgzMn0.bZvSrJT7iciSxHZbn-YCiJHXzTs0r6EnSzY2GUiof2Y"

    fun getApiKey() = API_KEY

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthApiService = retrofit.create(AuthApiService::class.java)
    val profileService: ProfileApiService = retrofit.create(ProfileApiService::class.java)
}