package com.example.roomtrack.screens.register

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.example.roomtrack.R
import com.example.roomtrack.screens.login.LoginActivity
import com.example.roomtrack.utils.start
import com.example.roomtrack.utils.toast
import com.example.roomtrack.utils.value

class RegisterActivity : Activity(), RegisterContract.View {

    private lateinit var presenter: RegisterPresenter
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnGoLogin: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnGoLogin = findViewById(R.id.tvGoToLogin)
        progressBar = findViewById(R.id.progressBar)

        presenter = RegisterPresenter(this, RegisterModel())

        btnRegister.setOnClickListener {
            presenter.register(
                etFullName.value(), etEmail.value(), etPhone.value(),
                etPassword.value(), etConfirmPassword.value()
            )
        }

        btnGoLogin.setOnClickListener {
            start(LoginActivity::class.java)
        }
    }

    override fun showLoading() { progressBar.visibility = View.VISIBLE }
    override fun hideLoading() { progressBar.visibility = View.GONE }
    override fun showError(message: String) { toast(message) }
    override fun navigateToLogin() {
        toast("Registration successful! Please login.")
        start(LoginActivity::class.java)
        finish()
    }
}