package com.example.roomtrack.model

data class UpdateMetadataRequest(
    val data: MetadataUpdate
)

data class MetadataUpdate(
    val full_name: String
)