package com.example.roomtrack.screens.profile

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.example.roomtrack.R
import com.example.roomtrack.utils.toast
import com.example.roomtrack.utils.value

class ProfileActivity : Activity(), ProfileContract.View {

    private lateinit var presenter: ProfilePresenter
    private lateinit var tvProfileEmail: TextView
    private lateinit var tvProfileRole: TextView
    private lateinit var tvProfileName: TextView
    private lateinit var etFullName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    private lateinit var btnSaveProfile: Button
    private lateinit var btnChangePassword: Button
    private lateinit var tvBack: TextView
    private lateinit var progressProfile: ProgressBar
    private lateinit var progressPassword: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvProfileEmail = findViewById(R.id.tvProfileEmail)
        tvProfileRole = findViewById(R.id.tvProfileRole)
        tvProfileName = findViewById(R.id.tvProfileName)
        etFullName = findViewById(R.id.etFullName)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        tvBack = findViewById(R.id.tvBack)
        progressProfile = findViewById(R.id.progressProfile)
        progressPassword = findViewById(R.id.progressPassword)

        presenter = ProfilePresenter(this, ProfileModel())

        btnSaveProfile.setOnClickListener {
            presenter.updateProfile(etFullName.value(), etPhone.value(), etAddress.value())
        }

        btnChangePassword.setOnClickListener {
            presenter.changePassword(etNewPassword.value(), etConfirmNewPassword.value())
        }

        tvBack.setOnClickListener { presenter.onBackClicked() }

        presenter.loadProfile()
    }

    override fun showLoading() {
        progressProfile.visibility = View.VISIBLE
        progressPassword.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressProfile.visibility = View.GONE
        progressPassword.visibility = View.GONE
    }

    override fun showError(message: String) { toast(message) }
    override fun showSuccess(message: String) { toast(message) }

    override fun populateProfile(name: String, email: String, phone: String, address: String, role: String) {
        tvProfileName.text = name
        tvProfileEmail.text = email
        tvProfileRole.text = role
        etFullName.setText(name)
        etPhone.setText(phone)
        etAddress.setText(address)
    }

    override fun navigateBack() { finish() }
}