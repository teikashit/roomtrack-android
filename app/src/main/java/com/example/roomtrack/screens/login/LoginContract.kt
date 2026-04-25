package com.example.roomtrack.screens.login

class LoginContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToDashboard()
    }
    interface Presenter {
        fun login(email: String, password: String)
    }
}