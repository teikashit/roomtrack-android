package com.example.roomtrack.app

import android.app.Application
import android.content.Context
import com.example.roomtrack.data.UserSession

class RoomTrackApp : Application() {

    private var token: String = ""
    private var userSession = UserSession()

    fun getToken() = token
    fun setToken(token: String) { this.token = token }
    fun getUserSession() = userSession
    fun setUserSession(session: UserSession) { userSession = session }

    companion object {
        private lateinit var instance: RoomTrackApp
        fun getInstance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}