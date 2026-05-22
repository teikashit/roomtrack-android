package com.example.roomtrack.api

import com.example.roomtrack.model.AnnouncementRequest
import com.example.roomtrack.model.AnnouncementResponse
import retrofit2.Response
import retrofit2.http.*


interface AnnouncementApiService {


    @GET("announcements")
    suspend fun getAllAnnouncements(): Response<List<AnnouncementResponse>>


    @POST("announcements")
    suspend fun createAnnouncement(
        @Body request: AnnouncementRequest
    ): Response<List<AnnouncementResponse>>


    @DELETE("announcements/{id}")
    suspend fun deleteAnnouncement(
        @Path("id") id: String
    ): Response<Void>
}
