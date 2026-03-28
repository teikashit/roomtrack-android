package com.example.roomtrack.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val data: RegisterMetadata
)

data class RegisterMetadata(
    val full_name: String,
    val phone: String,
    val role: String
)