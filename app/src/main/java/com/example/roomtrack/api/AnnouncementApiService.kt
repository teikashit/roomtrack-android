package com.example.roomtrack.api

import com.example.roomtrack.model.AnnouncementRequest
import com.example.roomtrack.model.AnnouncementResponse
import retrofit2.Response
import retrofit2.http.*

interface AnnouncementApiService {

    @GET("rest/v1/announcements")
    suspend fun getAllAnnouncements(
        @Header("Authorization") token: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): Response<List<AnnouncementResponse>>

    @POST("rest/v1/announcements")
    suspend fun createAnnouncement(
        @Header("Authorization") token: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body request: AnnouncementRequest
    ): Response<List<AnnouncementResponse>>

    @DELETE("rest/v1/announcements")
    suspend fun deleteAnnouncement(
        @Header("Authorization") token: String,
        @Query("id") id: String
    ): Response<Void>
}
