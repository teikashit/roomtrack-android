package com.example.roomtrack.screens.login

import android.app.Activity
import com.example.roomtrack.data.UserSession
import com.example.roomtrack.utils.app
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginPresenter(
    private val view: LoginContract.View,
    private val model: LoginModel
) : LoginContract.Presenter {

    override fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            view.showError("Please fill in all fields")
            return
        }

        view.showLoading()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.login(email, password)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) {
                        val body = response.body()!!
                        val app = (view as Activity).app()
                        app.setToken("Bearer ${body.access_token}")
                        app.setUserSession(
                            UserSession(
                                id = body.user.id,
                                email = body.user.email,
                                name = body.user.user_metadata.full_name ?: "",
                                role = body.user.user_metadata.role ?: "tenant"
                            )
                        )
                        view.navigateToDashboard()
                    } else {
                        view.showError("Invalid email or password")
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