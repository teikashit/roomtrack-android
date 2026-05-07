package com.example.roomtrack.screens.announcements

import com.example.roomtrack.model.AnnouncementResponse

class AnnouncementContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showSuccess(message: String)
        fun showAnnouncements(announcements: List<AnnouncementResponse>)
        fun showNoAnnouncements()
        fun showPostDialog()
        fun navigateToLogin()
    }
    interface Presenter {
        fun loadAnnouncements()
        fun onPostClicked()
        fun onPostSubmitted(title: String, content: String)
        fun onDeleteClicked(announcementId: String)
        fun onLogoutClicked()
    }
}
