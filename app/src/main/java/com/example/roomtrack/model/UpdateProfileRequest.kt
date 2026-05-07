package com.example.roomtrack.model

data class UpdateProfileRequest(
    val id: String,
    val full_name: String,
    val phone: String,
    val address: String,
    val role: String,
    val photo_url: String? = null
)
