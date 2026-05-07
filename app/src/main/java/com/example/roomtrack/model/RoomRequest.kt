package com.example.roomtrack.model

data class RoomRequest(
    val unit_name: String,
    val monthly_rate: Double,
    val description: String?,
    val status: String = "vacant",
    val floor: String? = null,
    val size: String? = null
)