package com.example.roomtrack.screens.login

import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.model.LoginRequest
import com.example.roomtrack.model.LoginResponse
import retrofit2.Response

class LoginModel {
    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return RetrofitClient.authService.login(LoginRequest(email, password))
    }
}