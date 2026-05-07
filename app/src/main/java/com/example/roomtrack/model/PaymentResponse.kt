package com.example.roomtrack.model

data class PaymentResponse(
    val id: String,
    val tenant_id: String?,
    val tenant_name: String?,
    val room_id: String?,
    val amount: Double,
    val status: String,
    val due_date: String?,
    val paid_date: String?,
    val description: String?
)
