package com.example.roomtrack.model

data class AssignTenantRequest(
    val tenant_id: String?,
    val tenant_name: String?,
    val status: String
)
