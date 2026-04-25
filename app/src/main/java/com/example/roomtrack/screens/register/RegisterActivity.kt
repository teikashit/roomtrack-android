package com.example.roomtrack.screens.register

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
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
    private lateinit var spinnerRole: Spinner
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initViews()
        setupRoleSpinner()
        presenter = RegisterPresenter(this, RegisterModel())
        setupListeners()
    }

    private fun initViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        spinnerRole = findViewById(R.id.spinnerRole)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRoleSpinner() {
        val roles = listOf("Tenant", "Landlord")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener {
            val selectedRole = spinnerRole.selectedItem.toString().lowercase()
            presenter.register(
                fullName = etFullName.value(),
                email = etEmail.value(),
                phone = etPhone.value(),
                role = selectedRole,
                password = etPassword.value(),
                confirmPassword = etConfirmPassword.value()
            )
        }
        tvGoToLogin.setOnClickListener {
            start(LoginActivity::class.java)
        }
    }

    override fun showLoading() { progressBar.visibility = View.VISIBLE }
    override fun hideLoading() { progressBar.visibility = View.GONE }
    override fun showError(message: String) { toast(message) }
    override fun navigateToLogin() {
        toast("Registration successful! Please sign in.")
        start(LoginActivity::class.java)
        finish()
    }
}