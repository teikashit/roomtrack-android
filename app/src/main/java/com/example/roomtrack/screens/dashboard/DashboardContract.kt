package com.example.roomtrack.screens.dashboard

class DashboardContract {
    interface View {
        fun showWelcome(name: String)
        fun showLandlordView()
        fun showTenantView()
        fun showStats(label1: String, value1: String, label2: String, value2: String)
        fun showLatestAnnouncement(title: String, content: String)
        fun showNoAnnouncement()
        fun navigateToProfile()
        fun navigateToLogin()
    }
    interface Presenter {
        fun loadDashboard()
        fun onProfileClicked()
        fun onLogoutClicked()
    }
}
