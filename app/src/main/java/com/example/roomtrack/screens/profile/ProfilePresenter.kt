package com.example.roomtrack.screens.profile

import android.app.Activity
import com.example.roomtrack.data.UserSession
import com.example.roomtrack.model.UpdateProfileRequest
import com.example.roomtrack.utils.app
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfilePresenter(
    private val view: ProfileContract.View,
    private val model: ProfileModel
) : ProfileContract.Presenter {

    private var currentPhotoUrl: String? = null

    override fun loadProfile() {
        val app = (view as Activity).app()
        val session = app.getUserSession()
        val token = app.getToken()

        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.getProfile(token, session.id)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful && response.body() != null) {
                        // Spring Boot returns a single object, not a list
                        val profile = response.body()!!
                        currentPhotoUrl = profile.photo_url
                        view.populateProfile(
                            name = profile.full_name ?: session.name,
                            email = session.email,
                            phone = profile.phone ?: "",
                            address = profile.address ?: "",
                            role = profile.role ?: session.role,
                            photoUrl = profile.photo_url
                        )
                    } else {
                        // Profile not yet created — show session defaults
                        view.populateProfile(
                            name = session.name,
                            email = session.email,
                            phone = "",
                            address = "",
                            role = session.role,
                            photoUrl = null
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Failed to load profile: ${e.message}")
                }
            }
        }
    }

    override fun updateProfile(fullName: String, phone: String, address: String) {
        if (fullName.isEmpty()) {
            view.showError("Name is required")
            return
        }
        val app = (view as Activity).app()
        val session = app.getUserSession()
        val token = app.getToken()

        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = UpdateProfileRequest(
                    id = session.id,
                    full_name = fullName,
                    phone = phone,
                    address = address,
                    role = session.role,
                    photo_url = currentPhotoUrl
                )
                // Spring Boot only needs one call — no separate metadata update needed
                val profileResponse = model.updateProfile(token, request)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (profileResponse.isSuccessful) {
                        view.showSuccess("Profile updated successfully")
                    } else {
                        view.showError("Failed to update profile")
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

    override fun uploadPhoto(imageBytes: ByteArray, fileName: String) {
        val app = (view as Activity).app()
        val session = app.getUserSession()
        val token = app.getToken()

        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Step 1: Upload photo to Supabase Storage (kept as-is)
                val photoUrl = model.uploadPhoto(token, session.id, imageBytes, fileName)
                if (photoUrl != null) {
                    currentPhotoUrl = photoUrl

                    // Step 2: Save the new photo URL to Spring Boot
                    val request = UpdateProfileRequest(
                        id = session.id,
                        full_name = session.name,
                        phone = "",
                        address = "",
                        role = session.role,
                        photo_url = photoUrl
                    )
                    model.updateProfile(token, request)

                    withContext(Dispatchers.Main) {
                        view.hideLoading()
                        view.showSuccess("Photo updated!")
                        view.populateProfile(
                            name = session.name,
                            email = session.email,
                            phone = "",
                            address = "",
                            role = session.role,
                            photoUrl = photoUrl
                        )
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        view.hideLoading()
                        view.showError("Failed to upload photo")
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

    override fun changePassword(newPassword: String, confirmPassword: String) {
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            view.showError("Please fill in both password fields")
            return
        }
        if (newPassword != confirmPassword) {
            view.showError("Passwords do not match")
            return
        }
        if (newPassword.length < 6) {
            view.showError("Password must be at least 6 characters")
            return
        }
        val app = (view as Activity).app()
        val token = app.getToken()

        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Spring Boot identifies the user from the JWT Bearer token
                val response = model.changePassword(token, newPassword)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) view.showSuccess("Password changed successfully")
                    else view.showError("Failed to change password")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Network error: ${e.message}")
                }
            }
        }
    }

    override fun onBackClicked() { view.navigateBack() }

    override fun onLogoutClicked() {
        val app = (view as Activity).app()
        app.setToken("")
        app.setUserSession(UserSession())
        view.navigateToLogin()
    }
}
