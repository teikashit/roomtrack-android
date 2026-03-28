package com.example.roomtrack.ui.dashboard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.roomtrack.databinding.ActivityDashboardBinding
import com.example.roomtrack.ui.profile.ProfileActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("APP", MODE_PRIVATE)

        val name = prefs.getString("USER_NAME", "User") ?: "User"
        val role = prefs.getString("USER_ROLE", "landlord") ?: "landlord"

        // Set welcome text
        binding.tvWelcome.text = "Hello, ${name.split(" ")[0]}!"
        binding.tvSubtitle.text = if (role == "landlord")
            "Here's your property overview."
        else
            "Your rent status for this month."

        // Set role badge
        binding.tvRole.text = role.uppercase()
        binding.tvRole.setTextColor(
            if (role == "landlord")
                getColor(android.R.color.holo_blue_dark)
            else
                getColor(android.R.color.holo_green_dark)
        )

        // Set initials
        val initials = name.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()
        binding.tvInitials.text = initials

        // Bottom nav
        binding.navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}