package com.example.roomtrack.api

import com.example.roomtrack.model.PaymentRequest
import com.example.roomtrack.model.PaymentResponse
import retrofit2.Response
import retrofit2.http.*

interface PaymentApiService {

    @GET("rest/v1/payments")
    suspend fun getAllPayments(
        @Header("Authorization") token: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "due_date.desc"
    ): Response<List<PaymentResponse>>

    @GET("rest/v1/payments")
    suspend fun getPaymentsByTenant(
        @Header("Authorization") token: String,
        @Query("tenant_id") tenantId: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "due_date.desc"
    ): Response<List<PaymentResponse>>

    @POST("rest/v1/payments")
    suspend fun createPayment(
        @Header("Authorization") token: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body request: PaymentRequest
    ): Response<List<PaymentResponse>>

    @PATCH("rest/v1/payments")
    suspend fun markAsPaid(
        @Header("Authorization") token: String,
        @Header("Prefer") prefer: String = "return=minimal",
        @Query("id") id: String,
        @Body body: Map<String, String>
    ): Response<Void>

    @PATCH("rest/v1/payments")
    suspend fun updateStatus(
        @Header("Authorization") token: String,
        @Header("Prefer") prefer: String = "return=minimal",
        @Query("id") id: String,
        @Body body: Map<String, String>
    ): Response<Void>
}
