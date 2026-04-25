package com.example.roomtrack.screens.register

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterPresenter(
    private val view: RegisterContract.View,
    private val model: RegisterModel
) : RegisterContract.Presenter {

    override fun register(
        fullName: String,
        email: String,
        phone: String,
        role: String,
        password: String,
        confirmPassword: String
    ) {
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            view.showError("Please fill in all fields")
            return
        }
        if (password != confirmPassword) {
            view.showError("Passwords do not match")
            return
        }
        if (password.length < 6) {
            view.showError("Password must be at least 6 characters")
            return
        }
        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.register(fullName, email, phone, role, password)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) {
                        view.navigateToLogin()
                    } else {
                        view.showError("Registration failed. Email may already be in use.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Network error: ${e.message}")
                }
            }
        }
    }
}