package com.example.roomtrack.api

import com.example.roomtrack.model.*
import retrofit2.Response
import retrofit2.http.*

interface ProfileApiService {

    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Header("Authorization") token: String,
        @Query("id") id: String,
        @Query("select") select: String = "*"
    ): Response<List<ProfileResponse>>

    @POST("rest/v1/profiles")
    suspend fun upsertProfile(
        @Header("Authorization") token: String,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates,return=minimal",
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: UpdateProfileRequest
    ): Response<Void>
}