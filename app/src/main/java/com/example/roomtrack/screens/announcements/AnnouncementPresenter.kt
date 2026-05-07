package com.example.roomtrack.screens.announcements

import android.app.Activity
import com.example.roomtrack.data.UserSession
import com.example.roomtrack.utils.app
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnnouncementPresenter(
    private val view: AnnouncementContract.View,
    private val model: AnnouncementModel
) : AnnouncementContract.Presenter {

    override fun loadAnnouncements() {
        val app = (view as Activity).app()
        val token = app.getToken()

        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.getAllAnnouncements(token)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) {
                        val announcements = response.body() ?: emptyList()
                        if (announcements.isEmpty()) view.showNoAnnouncements()
                        else view.showAnnouncements(announcements)
                    } else {
                        view.showError("Failed to load announcements")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Network error: ${e.message}")
                }
            }
        }
    }

    override fun onPostClicked() {
        view.showPostDialog()
    }

    override fun onPostSubmitted(title: String, content: String) {
        if (title.isEmpty() || content.isEmpty()) {
            view.showError("Title and content are required")
            return
        }
        val app = (view as Activity).app()
        val session = app.getUserSession()
        val token = app.getToken()

        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.createAnnouncement(
                    token = token,
                    title = title,
                    content = content,
                    landlordId = session.id,
                    landlordName = session.name
                )
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) {
                        view.showSuccess("Announcement posted!")
                        loadAnnouncements()
                    } else {
                        view.showError("Failed to post announcement")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Network error: ${e.message}")
                }
            }
        }
    }

    override fun onDeleteClicked(announcementId: String) {
        val app = (view as Activity).app()
        val token = app.getToken()

        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.deleteAnnouncement(token, announcementId)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) {
                        view.showSuccess("Announcement deleted")
                        loadAnnouncements()
                    } else {
                        view.showError("Failed to delete announcement")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Network error: ${e.message}")
                }
            }
        }
    }

    override fun onLogoutClicked() {
        val app = (view as Activity).app()
        app.setToken("")
        app.setUserSession(UserSession())
        view.navigateToLogin()
    }
}
