package com.example.roomtrack.api

import com.example.roomtrack.model.*
import retrofit2.Response
import retrofit2.http.*


interface AuthApiService {


    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>


    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}
