package com.example.roomtrack.screens.dashboard

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.roomtrack.R
import com.example.roomtrack.screens.announcements.AnnouncementActivity
import com.example.roomtrack.screens.login.LoginActivity
import com.example.roomtrack.screens.payments.PaymentActivity
import com.example.roomtrack.screens.profile.ProfileActivity
import com.example.roomtrack.screens.rooms.RoomsActivity
import com.example.roomtrack.utils.NavHelper
import com.example.roomtrack.utils.start

class DashboardActivity : Activity(), DashboardContract.View {

    private lateinit var presenter: DashboardPresenter
    private lateinit var tvWelcome: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvInitials: TextView
    private lateinit var tvStatLabel1: TextView
    private lateinit var tvStatValue1: TextView
    private lateinit var tvStatLabel2: TextView
    private lateinit var tvStatValue2: TextView
    private lateinit var tvAnnouncementTitle: TextView
    private lateinit var tvAnnouncementContent: TextView
    private lateinit var layoutAnnouncement: LinearLayout
    private lateinit var tvNoAnnouncement: TextView
    private lateinit var navHome: LinearLayout
    private lateinit var navProfile: LinearLayout
    private lateinit var navRooms: LinearLayout
    private lateinit var navPayments: LinearLayout
    private lateinit var navAnnouncements: LinearLayout

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
        tvStatLabel1 = findViewById(R.id.tvStatLabel1)
        tvStatValue1 = findViewById(R.id.tvStatValue1)
        tvStatLabel2 = findViewById(R.id.tvStatLabel2)
        tvStatValue2 = findViewById(R.id.tvStatValue2)
        tvAnnouncementTitle = findViewById(R.id.tvAnnouncementTitle)
        tvAnnouncementContent = findViewById(R.id.tvAnnouncementContent)
        layoutAnnouncement = findViewById(R.id.layoutAnnouncement)
        tvNoAnnouncement = findViewById(R.id.tvNoAnnouncement)
        navHome = findViewById(R.id.navHome)
        navProfile = findViewById(R.id.navProfile)
        navRooms = findViewById(R.id.navRooms)
        navPayments = findViewById(R.id.navPayments)
        navAnnouncements = findViewById(R.id.navAnnouncements)
    }

    private fun setupListeners() {
        NavHelper.setupAvatarMenu(this, tvInitials)
        navHome.setOnClickListener { /* already here */ }
        navRooms.setOnClickListener { start<RoomsActivity>(RoomsActivity::class.java) }
        navPayments.setOnClickListener { start<PaymentActivity>(PaymentActivity::class.java) }
        navAnnouncements.setOnClickListener { start<AnnouncementActivity>(AnnouncementActivity::class.java) }
        navProfile.setOnClickListener { start<ProfileActivity>(ProfileActivity::class.java) }
    }

    override fun showWelcome(name: String) {
        tvWelcome.text = "Welcome, $name!"
        tvInitials.text = if (name.isNotEmpty()) name.first().uppercaseChar().toString() else "?"
    }

    override fun showLandlordView() { tvRole.text = "LANDLORD" }
    override fun showTenantView() { tvRole.text = "TENANT" }

    override fun showStats(label1: String, value1: String, label2: String, value2: String) {
        tvStatLabel1.text = label1
        tvStatValue1.text = value1
        tvStatLabel2.text = label2
        tvStatValue2.text = value2
    }

    override fun showLatestAnnouncement(title: String, content: String) {
        layoutAnnouncement.visibility = View.VISIBLE
        tvNoAnnouncement.visibility = View.GONE
        tvAnnouncementTitle.text = title
        tvAnnouncementContent.text = content
    }

    override fun showNoAnnouncement() {
        layoutAnnouncement.visibility = View.GONE
        tvNoAnnouncement.visibility = View.VISIBLE
    }

    override fun navigateToProfile() { start<ProfileActivity>(ProfileActivity::class.java) }

    override fun navigateToLogin() {
        start(LoginActivity::class.java)
        finish()
    }
}
