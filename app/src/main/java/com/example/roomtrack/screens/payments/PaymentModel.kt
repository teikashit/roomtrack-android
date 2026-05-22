package com.example.roomtrack.screens.payments

import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.model.PaymentRequest
import com.example.roomtrack.model.PaymentResponse
import com.example.roomtrack.model.ProfileResponse
import com.example.roomtrack.model.RoomResponse
import retrofit2.Response


class PaymentModel {

    suspend fun getAllPayments(token: String): Response<List<PaymentResponse>> {
        return RetrofitClient.paymentService.getAllPayments()
    }

    suspend fun getPaymentsByTenant(token: String, tenantId: String): Response<List<PaymentResponse>> {
        return RetrofitClient.paymentService.getPaymentsByTenant(tenantId)
    }

    suspend fun getTenants(token: String): Response<List<ProfileResponse>> {
        return RetrofitClient.profileService.getTenants()
    }

    suspend fun getRoomByTenantId(token: String, tenantId: String): Response<List<RoomResponse>> {
        return RetrofitClient.roomService.getRoomByTenantId(tenantId)
    }

    suspend fun createPayment(
        token: String,
        tenantId: String,
        tenantName: String,
        roomId: String,
        amount: Double,
        dueDate: String,
        description: String?
    ): Response<List<PaymentResponse>> {
        val request = PaymentRequest(
            tenant_id = tenantId,
            tenant_name = tenantName,
            room_id = roomId,
            amount = amount,
            status = "Pending",
            due_date = dueDate,
            description = description
        )
        return RetrofitClient.paymentService.createPayment(request = request)
    }


    suspend fun markAsPaid(token: String, paymentId: String, paidDate: String): Response<Void> {
        val body = mapOf("status" to "Paid")
        return RetrofitClient.paymentService.updateStatus(id = paymentId, body = body)
    }

    suspend fun updateStatus(token: String, paymentId: String, status: String): Response<Void> {
        val body = mapOf("status" to status)
        return RetrofitClient.paymentService.updateStatus(id = paymentId, body = body)
    }
}
