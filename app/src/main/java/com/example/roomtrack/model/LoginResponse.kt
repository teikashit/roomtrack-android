package com.example.roomtrack.model

data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val user: UserData
)

data class UserData(
    val id: String,
    val email: String,
    val user_metadata: UserMetadata
)

data class UserMetadata(
    val full_name: String?,
    val role: String?
)