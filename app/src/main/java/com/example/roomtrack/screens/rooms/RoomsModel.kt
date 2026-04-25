package com.example.roomtrack.screens.rooms

import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.model.RoomRequest
import com.example.roomtrack.model.RoomResponse
import retrofit2.Response

class RoomsModel {

    suspend fun getAllRooms(token: String): Response<List<RoomResponse>> {
        return RetrofitClient.roomService.getRooms(token)
    }

    suspend fun getRoomByTenantId(token: String, tenantId: String): Response<List<RoomResponse>> {
        return RetrofitClient.roomService.getRoomByTenantId(token, "eq.$tenantId")
    }

    suspend fun createRoom(token: String, unitNumber: String, monthlyRate: Double, description: String?): Response<List<RoomResponse>> {
        val request = RoomRequest(
            unit_name = unitNumber,
            monthly_rate = monthlyRate,
            description = description,
            status = "Vacant"
        )
        return RetrofitClient.roomService.createRoom(token, request = request)
    }
}
