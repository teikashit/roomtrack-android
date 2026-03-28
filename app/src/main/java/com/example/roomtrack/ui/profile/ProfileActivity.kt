package com.example.roomtrack.ui.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.databinding.ActivityProfileBinding
import com.example.roomtrack.model.UpdatePasswordRequest
import com.example.roomtrack.model.UpdateProfileRequest
import com.example.roomtrack.ui.auth.LoginActivity
import android.content.Intent
import com.example.roomtrack.model.MetadataUpdate
import com.example.roomtrack.model.UpdateMetadataRequest
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("APP", MODE_PRIVATE)

        val name = prefs.getString("USER_NAME", "User") ?: "User"
        val email = prefs.getString("USER_EMAIL", "") ?: ""
        val role = prefs.getString("USER_ROLE", "landlord") ?: "landlord"

        // Set header info
        val initials = name.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()

        binding.tvAvatarInitials.text = initials
        binding.tvProfileName.text = name
        binding.tvProfileEmail.text = email
        binding.tvProfileRole.text = role.uppercase()

        // Load profile from Supabase
        loadProfile()

        binding.tvBack.setOnClickListener { finish() }
        binding.btnSaveProfile.setOnClickListener { handleSaveProfile() }
        binding.btnChangePassword.setOnClickListener { handleChangePassword() }
        binding.btnLogout.setOnClickListener { handleLogout() }
    }

    private fun loadProfile() {
        val token = "Bearer ${prefs.getString("TOKEN", "")}"
        val userId = prefs.getString("USER_ID", "") ?: ""

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.profileService.getProfile(
                    token = token,
                    id = "eq.$userId"
                )
                if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                    val profile = response.body()!![0]
                    binding.etFullName.setText(profile.full_name ?: "")
                    binding.etPhone.setText(profile.phone ?: "")
                    binding.etAddress.setText(profile.address ?: "")
                }
            } catch (e: Exception) {
                // silently fail
            }
        }
    }

    private fun handleSaveProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (fullName.isEmpty()) {
            showProfileMessage("Full name is required.", false)
            return
        }

        val token = "Bearer ${prefs.getString("TOKEN", "")}"
        val userId = prefs.getString("USER_ID", "") ?: ""
        val role = prefs.getString("USER_ROLE", "landlord") ?: "landlord"

        setProfileLoading(true)

        lifecycleScope.launch {
            try {
                // Save to profiles table
                val response = RetrofitClient.profileService.upsertProfile(
                    token = token,
                    request = UpdateProfileRequest(
                        id = userId,
                        full_name = fullName,
                        phone = phone,
                        address = address,
                        role = role
                    )
                )
                if (response.isSuccessful || response.code() == 201) {
                    // Also update auth metadata
                    RetrofitClient.authService.updateUserMetadata(
                        token = token,
                        request = UpdateMetadataRequest(
                            data = MetadataUpdate(full_name = fullName)
                        )
                    )
                    prefs.edit().putString("USER_NAME", fullName).apply()
                    binding.tvProfileName.text = fullName
                    showProfileMessage("✅ Profile updated successfully!", true)
                } else {
                    showProfileMessage("Failed: ${response.code()}", false)
                }
            } catch (e: Exception) {
                showProfileMessage("Error: ${e.message}", false)
            }
            setProfileLoading(false)
        }
    }

    private fun handleChangePassword() {
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmNewPassword.text.toString().trim()

        if (newPassword.isEmpty()) {
            showPasswordMessage("Please enter a new password.", false)
            return
        }
        if (newPassword.length < 6) {
            showPasswordMessage("Password must be at least 6 characters.", false)
            return
        }
        if (newPassword != confirmPassword) {
            showPasswordMessage("Passwords do not match.", false)
            return
        }

        val token = "Bearer ${prefs.getString("TOKEN", "")}"

        setPasswordLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.authService.updatePassword(
                    token = token,
                    request = UpdatePasswordRequest(newPassword)
                )
                if (response.isSuccessful) {
                    showPasswordMessage("✅ Password updated successfully!", true)
                    binding.etNewPassword.setText("")
                    binding.etConfirmNewPassword.setText("")
                } else {
                    when (response.code()) {
                        401 -> showPasswordMessage("Session expired. Please login again.", false)
                        else -> showPasswordMessage("Failed to update password.", false)
                    }
                }
            } catch (e: Exception) {
                showPasswordMessage("No internet connection.", false)
            }
            setPasswordLoading(false)
        }
    }

    private fun handleLogout() {
        prefs.edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    private fun showProfileMessage(message: String, success: Boolean) {
        binding.tvProfileMessage.text = message
        binding.tvProfileMessage.setTextColor(
            if (success) getColor(android.R.color.holo_green_dark)
            else getColor(android.R.color.holo_red_dark)
        )
        binding.tvProfileMessage.visibility = View.VISIBLE
    }

    private fun showPasswordMessage(message: String, success: Boolean) {
        binding.tvPasswordMessage.text = message
        binding.tvPasswordMessage.setTextColor(
            if (success) getColor(android.R.color.holo_green_dark)
            else getColor(android.R.color.holo_red_dark)
        )
        binding.tvPasswordMessage.visibility = View.VISIBLE
    }

    private fun setProfileLoading(isLoading: Boolean) {
        binding.progressProfile.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSaveProfile.isEnabled = !isLoading
    }

    private fun setPasswordLoading(isLoading: Boolean) {
        binding.progressPassword.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnChangePassword.isEnabled = !isLoading
    }
}