package com.example.roomtrack.utils

import android.app.Activity
import android.app.AlertDialog
import android.widget.LinearLayout
import android.widget.TextView
import com.example.roomtrack.data.UserSession
import com.example.roomtrack.screens.announcements.AnnouncementActivity
import com.example.roomtrack.screens.dashboard.DashboardActivity
import com.example.roomtrack.screens.login.LoginActivity
import com.example.roomtrack.screens.payments.PaymentActivity
import com.example.roomtrack.screens.profile.ProfileActivity
import com.example.roomtrack.screens.rooms.RoomsActivity

object NavHelper {

    fun setupNav(
        activity: Activity,
        navHome: LinearLayout,
        navRooms: LinearLayout,
        navPayments: LinearLayout,
        navAnnouncements: LinearLayout,
        navProfile: LinearLayout,
        tvInitials: LinearLayout? = null
    ) {
        navHome.setOnClickListener {
            activity.start<DashboardActivity>(DashboardActivity::class.java)
            activity.finish()
        }
        navRooms.setOnClickListener {
            if (activity !is RoomsActivity) {
                activity.start<RoomsActivity>(RoomsActivity::class.java)
            }
        }
        navPayments.setOnClickListener {
            if (activity !is PaymentActivity) {
                activity.start<PaymentActivity>(PaymentActivity::class.java)
            }
        }
        navAnnouncements.setOnClickListener {
            if (activity !is AnnouncementActivity) {
                activity.start<AnnouncementActivity>(AnnouncementActivity::class.java)
            }
        }
        navProfile.setOnClickListener {
            if (activity !is ProfileActivity) {
                activity.start<ProfileActivity>(ProfileActivity::class.java)
            }
        }
    }

    fun setupAvatarMenu(activity: Activity, tvInitials: TextView) {
        tvInitials.setOnClickListener {
            val options = arrayOf("View Profile", "Sign Out")
            AlertDialog.Builder(activity)
                .setTitle("Account")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> activity.start<ProfileActivity>(ProfileActivity::class.java)
                        1 -> {
                            activity.app().setToken("")
                            activity.app().setUserSession(UserSession())
                            activity.start<LoginActivity>(LoginActivity::class.java)
                            activity.finish()
                        }
                    }
                }
                .show()
        }
    }
}
