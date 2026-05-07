package com.example.roomtrack.screens.profile

import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.model.MetadataUpdate
import com.example.roomtrack.model.ProfileResponse
import com.example.roomtrack.model.UpdateMetadataRequest
import com.example.roomtrack.model.UpdatePasswordRequest
import com.example.roomtrack.model.UpdateProfileRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class ProfileModel {

    private val supabaseUrl = "https://lajmclxicnpxcfkucwsz.supabase.co"
    private val apiKey = RetrofitClient.getApiKey()

    suspend fun getProfile(token: String, userId: String): Response<List<ProfileResponse>> {
        return RetrofitClient.profileService.getProfile(token, "eq.$userId")
    }

    suspend fun updateProfile(token: String, request: UpdateProfileRequest): Response<Void> {
        return RetrofitClient.profileService.upsertProfile(token = token, request = request)
    }

    suspend fun changePassword(token: String, newPassword: String): Response<Any> {
        return RetrofitClient.authService.updatePassword(token, UpdatePasswordRequest(newPassword))
    }

    suspend fun updateMetadata(token: String, fullName: String): Response<Any> {
        return RetrofitClient.authService.updateUserMetadata(
            token, UpdateMetadataRequest(MetadataUpdate(fullName))
        )
    }

    // Upload image to Supabase Storage and return the public URL
    suspend fun uploadPhoto(token: String, userId: String, imageBytes: ByteArray, fileName: String): String? {
        return try {
            val client = OkHttpClient()
            val filePath = "$userId/$fileName"
            val uploadUrl = "$supabaseUrl/storage/v1/object/avatars/$filePath"

            val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .addHeader("Authorization", token)
                .addHeader("apikey", apiKey)
                .addHeader("Content-Type", "image/jpeg")
                .addHeader("x-upsert", "true") // overwrite if exists
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                "$supabaseUrl/storage/v1/object/public/avatars/$filePath"
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
