package com.example.roomtrack.screens.dashboard

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.example.roomtrack.R
import com.example.roomtrack.screens.login.LoginActivity
import com.example.roomtrack.screens.profile.ProfileActivity
import com.example.roomtrack.screens.rooms.RoomsActivity
import com.example.roomtrack.utils.start
import com.example.roomtrack.utils.toast

class DashboardActivity : Activity(), DashboardContract.View {

    private lateinit var presenter: DashboardPresenter
    private lateinit var tvWelcome: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvInitials: TextView
    private lateinit var navHome: LinearLayout
    private lateinit var navProfile: LinearLayout
    private lateinit var navRooms: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initViews()
        presenter = DashboardPresenter(this, DashboardModel())
        setupListeners()
        presenter.loadDashboard()
    }

    private fun initViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        tvRole = findViewById(R.id.tvRole)
        tvInitials = findViewById(R.id.tvInitials)
        navHome = findViewById(R.id.navHome)
        navProfile = findViewById(R.id.navProfile)
        navRooms = findViewById(R.id.navRooms)
    }

    private fun setupListeners() {
        navProfile.setOnClickListener { presenter.onProfileClicked() }
        navHome.setOnClickListener { presenter.onLogoutClicked() }
        navRooms.setOnClickListener { start(RoomsActivity::class.java) }
    }

    override fun showWelcome(name: String) {
        tvWelcome.text = "Welcome, $name!"
        tvInitials.text = if (name.isNotEmpty()) name.first().uppercaseChar().toString() else "?"
    }

    override fun showLandlordView() {
        tvRole.text = "LANDLORD"
        toast("Logged in as Landlord")
    }

    override fun showTenantView() {
        tvRole.text = "TENANT"
        toast("Logged in as Tenant")
    }

    override fun navigateToProfile() { start(ProfileActivity::class.java) }

    override fun navigateToLogin() {
        start(LoginActivity::class.java)
        finish()
    }
}