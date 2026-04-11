package com.example.roomtrack.screens.dashboard

import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.model.ProfileResponse
import retrofit2.Response

class DashboardModel {
    suspend fun getProfile(token: String, userId: String): Response<List<ProfileResponse>> {
        return RetrofitClient.profileService.getProfile(token, "eq.$userId")
    }
}