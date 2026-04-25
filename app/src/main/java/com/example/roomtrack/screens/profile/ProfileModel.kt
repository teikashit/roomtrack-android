package com.example.roomtrack.screens.profile

import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.model.MetadataUpdate
import com.example.roomtrack.model.ProfileResponse
import com.example.roomtrack.model.UpdateMetadataRequest
import com.example.roomtrack.model.UpdatePasswordRequest
import com.example.roomtrack.model.UpdateProfileRequest
import retrofit2.Response


class ProfileModel {

    // Fetches the user profile from the database
    suspend fun getProfile(token: String, userId: String): Response<List<ProfileResponse>> {
        return RetrofitClient.profileService.getProfile(token, "eq.$userId")
    }

    // Updates the user's profile information (name, phone, address)
    suspend fun updateProfile(token: String, request: UpdateProfileRequest): Response<Void> {
        return RetrofitClient.profileService.upsertProfile(token = token, request = request)
    }

    // Changes the user's account password via the Auth API
    suspend fun changePassword(token: String, newPassword: String): Response<Any> {
        return RetrofitClient.authService.updatePassword(token, UpdatePasswordRequest(newPassword))
    }

    // Updates the user metadata (full name) stored in the Auth service
    suspend fun updateMetadata(token: String, fullName: String): Response<Any> {
        return RetrofitClient.authService.updateUserMetadata(
            token, UpdateMetadataRequest(MetadataUpdate(fullName))
        )
    }
}
