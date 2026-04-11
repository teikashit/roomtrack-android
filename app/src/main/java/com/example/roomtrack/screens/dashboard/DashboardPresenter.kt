package com.example.roomtrack.screens.dashboard

import android.app.Activity
import com.example.roomtrack.utils.app
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardPresenter(
    private val view: DashboardContract.View,
    private val model: DashboardModel
) : DashboardContract.Presenter {

    override fun loadDashboard() {
        val app = (view as Activity).app()
        val session = app.getUserSession()
        view.showWelcome(session.name)

        if (session.role.lowercase() == "landlord") {
            view.showLandlordView()
        } else {
            view.showTenantView()
        }
    }

    override fun onProfileClicked() {
        view.navigateToProfile()
    }

    override fun onLogoutClicked() {
        val app = (view as Activity).app()
        app.setToken("")
        app.setUserSession(com.example.roomtrack.data.UserSession())
        view.navigateToLogin()
    }
}