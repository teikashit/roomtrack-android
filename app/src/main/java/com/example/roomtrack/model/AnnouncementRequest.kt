package com.example.roomtrack.model

data class AnnouncementRequest(
    val title: String,
    val content: String,
    val landlord_id: String,
    val landlord_name: String
)
