package com.example.roomtrack.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.roomtrack.api.RetrofitClient
import com.example.roomtrack.databinding.ActivityLoginBinding
import com.example.roomtrack.model.LoginRequest
import com.example.roomtrack.ui.dashboard.DashboardActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("APP", MODE_PRIVATE)

        // If already logged in, go to dashboard
        if (prefs.getString("TOKEN", null) != null) {
            goToDashboard()
            return
        }

        binding.btnLogin.setOnClickListener { handleLogin() }
        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.")
            return
        }
        if (!email.contains("@")) {
            showError("Please enter a valid email address.")
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.authService.login(
                    LoginRequest(email, password)
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = body.access_token
                    val user = body.user

                    // Save token and user info
                    prefs.edit()
                        .putString("TOKEN", token)
                        .putString("USER_ID", user.id)
                        .putString("USER_EMAIL", user.email)
                        .putString("USER_NAME", user.user_metadata.full_name ?: user.email)
                        .putString("USER_ROLE", user.user_metadata.role ?: "landlord")
                        .apply()

                    goToDashboard()
                } else {
                    when (response.code()) {
                        400 -> showError("Invalid email or password.")
                        401 -> showError("Unauthorized. Please check your credentials.")
                        500 -> showError("Server error. Please try again later.")
                        else -> showError("Login failed. Please try again.")
                    }
                }
            } catch (e: Exception) {
                showError("No internet connection. Please try again.")
            }
            setLoading(false)
        }
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }
}