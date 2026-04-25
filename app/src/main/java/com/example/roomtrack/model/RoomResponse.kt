package com.example.roomtrack.model

data class RoomResponse(
    val id: String,
    val unit_name: String,
    val monthly_rate: Double,
    val status: String,
    val floor: String?,
    val size: String?,
    val description: String?,
    val tenant_id: String?,
    val tenant_name: String?,
    val photo_url: String?
)