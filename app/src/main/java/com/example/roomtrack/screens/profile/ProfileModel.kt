package com.example.roomtrack.screens.profile

import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.model.ProfileResponse
import com.example.roomtrack.model.UpdatePasswordRequest
import com.example.roomtrack.model.UpdateProfileRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response


class ProfileModel {

    // Supabase Storage is kept for photo uploads only
    private val supabaseUrl = "https://lajmclxicnpxcfkucwsz.supabase.co"
    private val supabaseAnonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imxham1jbHhpY25weGNma3Vjd3N6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI4MDc4MzIsImV4cCI6MjA4ODM4MzgzMn0.bZvSrJT7iciSxHZbn-YCiJHXzTs0r6EnSzY2GUiof2Y"


    suspend fun getProfile(token: String, userId: String): Response<ProfileResponse> {
        return RetrofitClient.profileService.getProfile(userId)
    }


    suspend fun updateProfile(token: String, request: UpdateProfileRequest): Response<Void> {
        return RetrofitClient.profileService.upsertProfile(request = request)
    }


    suspend fun changePassword(token: String, newPassword: String): Response<Void> {
        return RetrofitClient.profileService.updatePassword(UpdatePasswordRequest(newPassword))
    }

    /**
     * Upload a profile photo directly to Supabase Storage (kept from original implementation).
     * After uploading, call updateProfile() to save the returned URL to Spring Boot.
     *
     * @param token       the user's JWT token (Spring Boot token — used as-is for Supabase upsert)
     * @param userId      the user's UUID
     * @param imageBytes  the raw JPEG bytes of the photo
     * @param fileName    the file name (e.g. "avatar.jpg")
     * @return the public URL of the uploaded photo, or null on failure
     */
    suspend fun uploadPhoto(token: String, userId: String, imageBytes: ByteArray, fileName: String): String? {
        return try {
            val client = OkHttpClient()
            val filePath = "$userId/$fileName"
            val uploadUrl = "$supabaseUrl/storage/v1/object/avatars/$filePath"

            val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .addHeader("Authorization", token) // Spring Boot Bearer token
                .addHeader("apikey", supabaseAnonKey) // Supabase still needs its anon key for Storage
                .addHeader("Content-Type", "image/jpeg")
                .addHeader("x-upsert", "true") // overwrite if file exists
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
