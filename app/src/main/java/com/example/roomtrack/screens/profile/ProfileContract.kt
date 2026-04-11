package com.example.roomtrack.screens.profile

interface ProfileContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showSuccess(message: String)
        fun populateProfile(name: String, email: String, phone: String, address: String, role: String)
        fun navigateBack()
    }
    interface Presenter {
        fun loadProfile()
        fun updateProfile(fullName: String, phone: String, address: String)
        fun changePassword(newPassword: String, confirmPassword: String)
        fun onBackClicked()
    }
}