package com.example.roomtrack.screens.register

import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.model.RegisterMetadata
import com.example.roomtrack.model.RegisterRequest
import com.example.roomtrack.model.RegisterResponse
import retrofit2.Response

class RegisterModel {
    suspend fun register(
        fullName: String, email: String,
        phone: String, role: String, password: String
    ): Response<RegisterResponse> {
        val request = RegisterRequest(
            email = email,
            password = password,
            data = RegisterMetadata(full_name = fullName, phone = phone, role = role)
        )
        return RetrofitClient.authService.register(request)
    }
}