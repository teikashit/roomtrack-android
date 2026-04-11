package com.example.roomtrack.screens.dashboard

interface DashboardContract {
    interface View {
        fun showWelcome(name: String)
        fun showLandlordView()
        fun showTenantView()
        fun navigateToProfile()
        fun navigateToLogin()
    }
    interface Presenter {
        fun loadDashboard()
        fun onProfileClicked()
        fun onLogoutClicked()
    }
}