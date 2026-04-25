package com.example.roomtrack.app

import android.app.Application
import com.example.roomtrack.data.UserSession

class RoomTrackApp : Application() {

    private var token: String = ""
    private var userSession = UserSession()

    fun getToken(): String = token
    fun setToken(token: String) { this.token = token }

    fun getUserSession(): UserSession = userSession
    fun setUserSession(session: UserSession) { userSession = session }

    companion object {
        private lateinit var instance: RoomTrackApp
        fun getInstance(): RoomTrackApp = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}