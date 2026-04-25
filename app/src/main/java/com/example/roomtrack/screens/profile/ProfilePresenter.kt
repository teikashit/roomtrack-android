package com.example.roomtrack.screens.profile

import android.app.Activity
import com.example.roomtrack.data.UserSession
import com.example.roomtrack.model.UpdateProfileRequest
import com.example.roomtrack.utils.app
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ProfilePresenter handles the business logic for the Profile screen.
 * Acts as a bridge between ProfileActivity (View) and ProfileModel (Data).
 */
class ProfilePresenter(
    private val view: ProfileContract.View,
    private val model: ProfileModel
) : ProfileContract.Presenter {

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

                    if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                        // Populate the profile form with existing data from the database
                        val profile = response.body()!![0]
                        view.populateProfile(
                            name = profile.full_name ?: session.name,
                            email = session.email,
                            phone = profile.phone ?: "",
                            address = profile.address ?: "",
                            role = profile.role ?: session.role
                        )
                    } else {
                        // Fall back to session data if no profile record found
                        view.populateProfile(
                            name = session.name,
                            email = session.email,
                            phone = "",
                            address = "",
                            role = session.role
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
        // Validate required fields before making API calls
        if (fullName.isEmpty() || phone.isEmpty()) {
            view.showError("Name and phone are required")
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
                    role = session.role
                )

                // Update both profile table and auth metadata
                val profileResponse = model.updateProfile(token, request)
                val metaResponse = model.updateMetadata(token, fullName)

                withContext(Dispatchers.Main) {
                    view.hideLoading()

                    if (profileResponse.isSuccessful && metaResponse.isSuccessful) {
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

    override fun changePassword(newPassword: String, confirmPassword: String) {
        // Validate password fields before making an API call
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
                val response = model.changePassword(token, newPassword)

                withContext(Dispatchers.Main) {
                    view.hideLoading()

                    if (response.isSuccessful) {
                        view.showSuccess("Password changed successfully")
                    } else {
                        view.showError("Failed to change password")
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

    override fun onBackClicked() {
        view.navigateBack()
    }

    override fun onLogoutClicked() {
        val app = (view as Activity).app()

        // Clear the session token and user data on logout
        app.setToken("")
        app.setUserSession(UserSession())

        view.navigateToLogin()
    }
}
