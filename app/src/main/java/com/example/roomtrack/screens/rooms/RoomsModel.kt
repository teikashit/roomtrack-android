package com.example.roomtrack.screens.rooms

import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.model.AssignTenantRequest
import com.example.roomtrack.model.ProfileResponse
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

    suspend fun assignTenant(token: String, roomId: String, tenantId: String, tenantName: String): Response<Void> {
        val request = AssignTenantRequest(
            tenant_id = tenantId,
            tenant_name = tenantName,
            status = "occupied"
        )
        return RetrofitClient.roomService.assignTenant(token, id = "eq.$roomId", request = request)
    }

    suspend fun unassignTenant(token: String, roomId: String): Response<Void> {
        val request = AssignTenantRequest(
            tenant_id = null,
            tenant_name = null,
            status = "vacant"
        )
        return RetrofitClient.roomService.assignTenant(token, id = "eq.$roomId", request = request)
    }

    suspend fun getTenants(token: String): Response<List<ProfileResponse>> {
        return RetrofitClient.profileService.getTenants(token)
    }
}
