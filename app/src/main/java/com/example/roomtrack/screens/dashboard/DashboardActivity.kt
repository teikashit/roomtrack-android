package com.example.roomtrack.screens.dashboard

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.roomtrack.R
import com.example.roomtrack.screens.login.LoginActivity
import com.example.roomtrack.screens.profile.ProfileActivity
import com.example.roomtrack.utils.start
import com.example.roomtrack.utils.toast

class DashboardActivity : Activity(), DashboardContract.View {

    private lateinit var presenter: DashboardPresenter
    private lateinit var tvWelcome: TextView
    private lateinit var navProfile: LinearLayout
    private lateinit var navHome: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        tvWelcome = findViewById(R.id.tvWelcome)
        navProfile = findViewById(R.id.navProfile)
        navHome = findViewById(R.id.navHome)

        presenter = DashboardPresenter(this, DashboardModel())

        navProfile.setOnClickListener { presenter.onProfileClicked() }

        navHome.setOnClickListener { presenter.onLogoutClicked() }

        presenter.loadDashboard()
    }

    override fun showWelcome(name: String) {
        tvWelcome.text = "Welcome, $name!"
    }

    override fun showLandlordView() {
        toast("Landlord Dashboard")
    }

    override fun showTenantView() {
        toast("Tenant Dashboard")
    }

    override fun navigateToProfile() {
        start(ProfileActivity::class.java)
    }

    override fun navigateToLogin() {
        start(LoginActivity::class.java)
        finish()
    }
}