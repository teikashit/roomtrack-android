package com.example.roomtrack.utils

import android.app.Activity
import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import com.example.roomtrack.app.RoomTrackApp

fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.app(): RoomTrackApp {
    return application as RoomTrackApp
}

fun <T> Activity.start(activityClass: Class<T>) {
    startActivity(Intent(this, activityClass))
}

fun EditText.value(): String {
    return text.toString().trim()
}