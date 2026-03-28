package com.example.roomtrack.model

data class ProfileResponse(
    val id: String,
    val full_name: String?,
    val phone: String?,
    val address: String?,
    val role: String?,
    val photo_url: String?
)