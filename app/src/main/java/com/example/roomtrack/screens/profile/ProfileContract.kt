package com.example.roomtrack.screens.profile

class ProfileContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showSuccess(message: String)
        fun populateProfile(name: String, email: String, phone: String, address: String, role: String, photoUrl: String?)
        fun navigateBack()
        fun navigateToLogin()
    }

    interface Presenter {
        fun loadProfile()
        fun updateProfile(fullName: String, phone: String, address: String)
        fun changePassword(newPassword: String, confirmPassword: String)
        fun uploadPhoto(imageBytes: ByteArray, fileName: String)
        fun onBackClicked()
        fun onLogoutClicked()
    }
}
