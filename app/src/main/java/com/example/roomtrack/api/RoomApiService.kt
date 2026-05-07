package com.example.roomtrack.api

import com.example.roomtrack.model.AssignTenantRequest
import com.example.roomtrack.model.RoomRequest
import com.example.roomtrack.model.RoomResponse
import retrofit2.Response
import retrofit2.http.*

interface RoomApiService {

    @GET("rest/v1/rooms")
    suspend fun getRooms(
        @Header("Authorization") token: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "unit_name.asc"
    ): Response<List<RoomResponse>>

    @GET("rest/v1/rooms")
    suspend fun getRoomByTenantId(
        @Header("Authorization") token: String,
        @Query("tenant_id") tenantId: String,
        @Query("select") select: String = "*"
    ): Response<List<RoomResponse>>

    @POST("rest/v1/rooms")
    suspend fun createRoom(
        @Header("Authorization") token: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body request: RoomRequest
    ): Response<List<RoomResponse>>

    @PATCH("rest/v1/rooms")
    suspend fun updateRoom(
        @Header("Authorization") token: String,
        @Header("Prefer") prefer: String = "return=minimal",
        @Query("id") id: String,
        @Body request: RoomRequest
    ): Response<Void>

    @PATCH("rest/v1/rooms")
    suspend fun assignTenant(
        @Header("Authorization") token: String,
        @Header("Prefer") prefer: String = "return=minimal",
        @Query("id") id: String,
        @Body request: AssignTenantRequest
    ): Response<Void>
}
