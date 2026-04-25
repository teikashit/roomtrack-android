package com.example.roomtrack.screens.dashboard

import android.app.Activity
import com.example.roomtrack.data.UserSession
import com.example.roomtrack.utils.app

class DashboardPresenter(
    private val view: DashboardContract.View,
    private val model: DashboardModel
) : DashboardContract.Presenter {

    override fun loadDashboard() {
        val app = (view as Activity).app()
        val session = app.getUserSession()
        view.showWelcome(session.name)
        if (session.role.equals("landlord", ignoreCase = true)) {
            view.showLandlordView()
        } else {
            view.showTenantView()
        }
    }

    override fun onProfileClicked() { view.navigateToProfile() }

    override fun onLogoutClicked() {
        val app = (view as Activity).app()
        app.setToken("")
        app.setUserSession(UserSession())
        view.navigateToLogin()
    }
}