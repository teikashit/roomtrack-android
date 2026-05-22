package com.example.roomtrack.screens.dashboard

import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.model.AnnouncementResponse
import com.example.roomtrack.model.PaymentResponse
import com.example.roomtrack.model.ProfileResponse
import com.example.roomtrack.model.RoomResponse
import retrofit2.Response


class DashboardModel {

    suspend fun getProfile(token: String, userId: String): Response<ProfileResponse> {
        return RetrofitClient.profileService.getProfile(userId)
    }

    suspend fun getAllRooms(token: String): Response<List<RoomResponse>> {
        return RetrofitClient.roomService.getRooms()
    }

    suspend fun getRoomByTenantId(token: String, tenantId: String): Response<List<RoomResponse>> {
        return RetrofitClient.roomService.getRoomByTenantId(tenantId)
    }

    suspend fun getAllPayments(token: String): Response<List<PaymentResponse>> {
        return RetrofitClient.paymentService.getAllPayments()
    }

    suspend fun getPaymentsByTenant(token: String, tenantId: String): Response<List<PaymentResponse>> {
        return RetrofitClient.paymentService.getPaymentsByTenant(tenantId)
    }

    suspend fun getLatestAnnouncement(token: String): Response<List<AnnouncementResponse>> {
        return RetrofitClient.announcementService.getAllAnnouncements()
    }
}
