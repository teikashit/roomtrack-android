package com.example.roomtrack.api

import com.example.roomtrack.model.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/v1/signup")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @PUT("auth/v1/user")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body request: UpdatePasswordRequest
    ): Response<Any>

    @PUT("auth/v1/user")
    suspend fun updateUserMetadata(
        @Header("Authorization") token: String,
        @Body request: UpdateMetadataRequest
    ): Response<Any>
}