package com.example.roomtrack.api

import com.example.roomtrack.model.AssignTenantRequest
import com.example.roomtrack.model.RoomRequest
import com.example.roomtrack.model.RoomResponse
import retrofit2.Response
import retrofit2.http.*


interface RoomApiService {


    @GET("rooms")
    suspend fun getRooms(): Response<List<RoomResponse>>


    @GET("rooms/tenant/{tenantId}")
    suspend fun getRoomByTenantId(
        @Path("tenantId") tenantId: String
    ): Response<List<RoomResponse>>


    @POST("rooms")
    suspend fun createRoom(
        @Body request: RoomRequest
    ): Response<List<RoomResponse>>


    @PATCH("rooms/{id}/assign")
    suspend fun assignTenant(
        @Path("id") id: String,
        @Body request: AssignTenantRequest
    ): Response<Void>


    @PATCH("rooms/{id}/unassign")
    suspend fun unassignTenant(
        @Path("id") id: String
    ): Response<Void>
}
