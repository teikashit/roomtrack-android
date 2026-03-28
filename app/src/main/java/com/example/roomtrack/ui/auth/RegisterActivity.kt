package com.example.roomtrack.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.databinding.ActivityRegisterBinding
import com.example.roomtrack.model.RegisterMetadata
import com.example.roomtrack.model.RegisterRequest
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup role spinner
        val roles = listOf("landlord", "tenant")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter

        binding.btnRegister.setOnClickListener { handleRegister() }
        binding.tvGoToLogin.setOnClickListener { finish() }
    }

    private fun handleRegister() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val role = binding.spinnerRole.selectedItem.toString()

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.")
            return
        }
        if (!email.contains("@")) {
            showError("Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            showError("Password must be at least 6 characters.")
            return
        }
        if (password != confirmPassword) {
            showError("Passwords do not match.")
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.authService.register(
                    RegisterRequest(
                        email = email,
                        password = password,
                        data = RegisterMetadata(
                            full_name = fullName,
                            phone = phone,
                            role = role
                        )
                    )
                )

                if (response.isSuccessful) {
                    // Save to profiles table
                    val userId = response.body()?.id
                    if (userId != null) {
                        RetrofitClient.profileService.upsertProfile(
                            token = "Bearer ${RetrofitClient.getApiKey()}",
                            request = com.example.roomtrack.model.UpdateProfileRequest(
                                id = userId,
                                full_name = fullName,
                                phone = phone,
                                address = "",
                                role = role
                            )
                        )
                    }
                    showSuccess("Account created! Please sign in.")
                    kotlinx.coroutines.delay(1500)
                    finish()
                } else {
                    when (response.code()) {
                        400 -> showError("Email already registered.")
                        422 -> showError("Invalid data. Please check your inputs.")
                        500 -> showError("Server error. Please try again later.")
                        else -> showError("Registration failed. Please try again.")
                    }
                }
            } catch (e: Exception) {
                showError("No internet connection. Please try again.")
            }
            setLoading(false)
        }
    }

    private fun showError(message: String) {
        binding.tvError.text = "⚠️ $message"
        binding.tvError.setTextColor(getColor(android.R.color.holo_red_dark))
        binding.tvError.visibility = View.VISIBLE
    }

    private fun showSuccess(message: String) {
        binding.tvError.text = "✅ $message"
        binding.tvError.setTextColor(getColor(android.R.color.holo_green_dark))
        binding.tvError.visibility = View.VISIBLE
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }
}