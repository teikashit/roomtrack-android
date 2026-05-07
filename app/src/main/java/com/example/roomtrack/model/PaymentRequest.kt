package com.example.roomtrack.model

data class PaymentRequest(
    val tenant_id: String,
    val tenant_name: String,
    val room_id: String,
    val amount: Double,
    val status: String = "Pending",
    val due_date: String,
    val description: String?
)
