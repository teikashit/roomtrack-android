package com.example.roomtrack.model

data class AnnouncementResponse(
    val id: String,
    val title: String?,
    val content: String?,
    val landlord_id: String?,
    val landlord_name: String?,
    val created_at: String?
)
