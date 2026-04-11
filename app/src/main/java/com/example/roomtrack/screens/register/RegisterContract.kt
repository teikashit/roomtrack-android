package com.example.roomtrack.screens.register

interface RegisterContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToLogin()
    }
    interface Presenter {
        fun register(fullName: String, email: String, phone: String, password: String, confirmPassword: String)
    }
}