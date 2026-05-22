package com.example.roomtrack.api

import com.example.roomtrack.model.*
import retrofit2.Response
import retrofit2.http.*


interface ProfileApiService {


    @GET("profiles/{id}")
    suspend fun getProfile(
        @Path("id") id: String
    ): Response<ProfileResponse>


    @GET("profiles/tenants")
    suspend fun getTenants(): Response<List<ProfileResponse>>


    @POST("profiles")
    suspend fun upsertProfile(
        @Body request: UpdateProfileRequest
    ): Response<Void>


    @PUT("profiles/password")
    suspend fun updatePassword(
        @Body request: UpdatePasswordRequest
    ): Response<Void>
}
