package com.example.roomtrack.api

import com.example.roomtrack.model.PaymentRequest
import com.example.roomtrack.model.PaymentResponse
import retrofit2.Response
import retrofit2.http.*


interface PaymentApiService {


    @GET("payments")
    suspend fun getAllPayments(): Response<List<PaymentResponse>>

    @GET("payments/tenant/{tenantId}")
    suspend fun getPaymentsByTenant(
        @Path("tenantId") tenantId: String
    ): Response<List<PaymentResponse>>


    @POST("payments")
    suspend fun createPayment(
        @Body request: PaymentRequest
    ): Response<List<PaymentResponse>>


    @PATCH("payments/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): Response<Void>
}
