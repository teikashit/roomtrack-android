package com.example.roomtrack.screens.login

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.example.roomtrack.R
import com.example.roomtrack.screens.dashboard.DashboardActivity
import com.example.roomtrack.screens.register.RegisterActivity
import com.example.roomtrack.utils.start
import com.example.roomtrack.utils.toast
import com.example.roomtrack.utils.value

class LoginActivity : Activity(), LoginContract.View {

    private lateinit var presenter: LoginPresenter
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoRegister: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoRegister = findViewById(R.id.tvGoToRegister)
        progressBar = findViewById(R.id.progressBar)

        presenter = LoginPresenter(this, LoginModel())

        btnLogin.setOnClickListener {
            presenter.login(etEmail.value(), etPassword.value())
        }

        btnGoRegister.setOnClickListener {
            start(RegisterActivity::class.java)
        }
    }

    override fun showLoading() { progressBar.visibility = View.VISIBLE }
    override fun hideLoading() { progressBar.visibility = View.GONE }
    override fun showError(message: String) { toast(message) }
    override fun navigateToDashboard() {
        start(DashboardActivity::class.java)
        finish()
    }
}