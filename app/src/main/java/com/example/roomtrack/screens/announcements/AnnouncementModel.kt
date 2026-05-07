package com.example.roomtrack.screens.announcements

import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.model.AnnouncementRequest
import com.example.roomtrack.model.AnnouncementResponse
import retrofit2.Response

class AnnouncementModel {

    suspend fun getAllAnnouncements(token: String): Response<List<AnnouncementResponse>> {
        return RetrofitClient.announcementService.getAllAnnouncements(token)
    }

    suspend fun createAnnouncement(
        token: String,
        title: String,
        content: String,
        landlordId: String,
        landlordName: String
    ): Response<List<AnnouncementResponse>> {
        val request = AnnouncementRequest(
            title = title,
            content = content,
            landlord_id = landlordId,
            landlord_name = landlordName
        )
        return RetrofitClient.announcementService.createAnnouncement(token, request = request)
    }

    suspend fun deleteAnnouncement(token: String, announcementId: String): Response<Void> {
        return RetrofitClient.announcementService.deleteAnnouncement(token, "eq.$announcementId")
    }
}
