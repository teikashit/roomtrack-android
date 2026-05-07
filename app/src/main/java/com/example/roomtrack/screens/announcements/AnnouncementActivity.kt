package com.example.roomtrack.screens.announcements

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.roomtrack.R
import com.example.roomtrack.model.AnnouncementResponse
import com.example.roomtrack.screens.dashboard.DashboardActivity
import com.example.roomtrack.screens.login.LoginActivity
import com.example.roomtrack.screens.payments.PaymentActivity
import com.example.roomtrack.screens.profile.ProfileActivity
import com.example.roomtrack.screens.rooms.RoomsActivity
import com.example.roomtrack.utils.NavHelper
import com.example.roomtrack.utils.app
import com.example.roomtrack.utils.start
import com.example.roomtrack.utils.toast
import com.example.roomtrack.utils.value

class AnnouncementActivity : Activity(), AnnouncementContract.View {

    private lateinit var presenter: AnnouncementPresenter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvRole: TextView
    private lateinit var tvInitials: TextView
    private lateinit var btnPost: Button
    private lateinit var containerAnnouncements: LinearLayout
    private lateinit var tvNoAnnouncements: TextView
    private lateinit var navHome: LinearLayout
    private lateinit var navRooms: LinearLayout
    private lateinit var navPayments: LinearLayout
    private lateinit var navAnnouncements: LinearLayout
    private lateinit var navProfile: LinearLayout

    private var isLandlord = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcement)
        initViews()
        setupHeader()
        presenter = AnnouncementPresenter(this, AnnouncementModel())
        setupListeners()
        presenter.loadAnnouncements()
    }

    private fun initViews() {
        progressBar = findViewById(R.id.progressBar)
        tvRole = findViewById(R.id.tvRole)
        tvInitials = findViewById(R.id.tvInitials)
        btnPost = findViewById(R.id.btnPost)
        containerAnnouncements = findViewById(R.id.containerAnnouncements)
        tvNoAnnouncements = findViewById(R.id.tvNoAnnouncements)
        navHome = findViewById(R.id.navHome)
        navRooms = findViewById(R.id.navRooms)
        navPayments = findViewById(R.id.navPayments)
        navAnnouncements = findViewById(R.id.navAnnouncements)
        navProfile = findViewById(R.id.navProfile)
    }

    private fun setupHeader() {
        val session = app().getUserSession()
        tvRole.text = session.role.uppercase()
        tvInitials.text = if (session.name.isNotEmpty()) session.name.first().uppercaseChar().toString() else "?"
        isLandlord = session.role.equals("landlord", ignoreCase = true)
        btnPost.visibility = if (isLandlord) View.VISIBLE else View.GONE
    }

    private fun setupListeners() {
        NavHelper.setupAvatarMenu(this, tvInitials)

        btnPost.setOnClickListener { presenter.onPostClicked() }

        navHome.setOnClickListener {
            start<DashboardActivity>(DashboardActivity::class.java)
            finish()
        }
        navRooms.setOnClickListener { start<RoomsActivity>(RoomsActivity::class.java) }
        navPayments.setOnClickListener { start<PaymentActivity>(PaymentActivity::class.java) }
        navAnnouncements.setOnClickListener { /* already here */ }
        navProfile.setOnClickListener { start<ProfileActivity>(ProfileActivity::class.java) }
    }

    override fun showLoading() { progressBar.visibility = View.VISIBLE }
    override fun hideLoading() { progressBar.visibility = View.GONE }
    override fun showError(message: String) { toast(message) }
    override fun showSuccess(message: String) { toast(message) }

    override fun showAnnouncements(announcements: List<AnnouncementResponse>) {
        tvNoAnnouncements.visibility = View.GONE
        containerAnnouncements.removeAllViews()
        announcements.forEach { containerAnnouncements.addView(buildAnnouncementCard(it)) }
    }

    override fun showNoAnnouncements() {
        tvNoAnnouncements.visibility = View.VISIBLE
        containerAnnouncements.removeAllViews()
    }

    private fun buildAnnouncementCard(announcement: AnnouncementResponse): View {
        val card = layoutInflater.inflate(R.layout.item_announcement_card, containerAnnouncements, false)
        card.findViewById<TextView>(R.id.tvCardTitle).text = announcement.title ?: "No title"
        card.findViewById<TextView>(R.id.tvCardContent).text = announcement.content ?: ""
        card.findViewById<TextView>(R.id.tvCardLandlord).text = "Posted by: ${announcement.landlord_name ?: "—"}"

        val rawDate = announcement.created_at ?: ""
        card.findViewById<TextView>(R.id.tvCardDate).text =
            if (rawDate.length >= 10) rawDate.substring(0, 10) else rawDate

        val btnDelete = card.findViewById<Button>(R.id.btnDelete)
        if (isLandlord) {
            btnDelete.visibility = View.VISIBLE
            btnDelete.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Delete Announcement")
                    .setMessage("Are you sure you want to delete this announcement?")
                    .setPositiveButton("Delete") { _, _ -> presenter.onDeleteClicked(announcement.id) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        } else {
            btnDelete.visibility = View.GONE
        }
        return card
    }

    override fun showPostDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_post_announcement, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etDialogTitle)
        val etContent = dialogView.findViewById<EditText>(R.id.etDialogContent)
        AlertDialog.Builder(this)
            .setTitle("Post Announcement")
            .setView(dialogView)
            .setPositiveButton("Post") { _, _ ->
                presenter.onPostSubmitted(etTitle.value(), etContent.value())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun navigateToLogin() {
        start(LoginActivity::class.java)
        finish()
    }
}
