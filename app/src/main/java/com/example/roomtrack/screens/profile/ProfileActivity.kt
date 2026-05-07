package com.example.roomtrack.screens.profile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.roomtrack.R
import com.example.roomtrack.screens.login.LoginActivity
import com.example.roomtrack.utils.start
import com.example.roomtrack.utils.toast
import com.example.roomtrack.utils.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL

class ProfileActivity : Activity(), ProfileContract.View {

    private lateinit var presenter: ProfilePresenter
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var tvProfileRole: TextView
    private lateinit var tvAvatarInitials: TextView
    private lateinit var ivProfilePhoto: ImageView
    private lateinit var btnChangePhoto: TextView
    private lateinit var etFullName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    private lateinit var btnSaveProfile: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnLogout: Button
    private lateinit var tvBack: TextView
    private lateinit var progressProfile: ProgressBar
    private lateinit var progressPassword: ProgressBar

    companion object {
        private const val PICK_IMAGE_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews()
        presenter = ProfilePresenter(this, ProfileModel())
        setupListeners()
        presenter.loadProfile()
    }

    private fun initViews() {
        tvProfileName = findViewById(R.id.tvProfileName)
        tvProfileEmail = findViewById(R.id.tvProfileEmail)
        tvProfileRole = findViewById(R.id.tvProfileRole)
        tvAvatarInitials = findViewById(R.id.tvAvatarInitials)
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)
        etFullName = findViewById(R.id.etFullName)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        btnLogout = findViewById(R.id.btnLogout)
        tvBack = findViewById(R.id.tvBack)
        progressProfile = findViewById(R.id.progressProfile)
        progressPassword = findViewById(R.id.progressPassword)
    }

    private fun setupListeners() {
        btnSaveProfile.setOnClickListener {
            presenter.updateProfile(etFullName.value(), etPhone.value(), etAddress.value())
        }
        btnChangePassword.setOnClickListener {
            presenter.changePassword(etNewPassword.value(), etConfirmNewPassword.value())
        }
        btnLogout.setOnClickListener { presenter.onLogoutClicked() }
        tvBack.setOnClickListener { presenter.onBackClicked() }
        btnChangePhoto.setOnClickListener { openImagePicker() }
        ivProfilePhoto.setOnClickListener { openImagePicker() }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri = data.data ?: return
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
                val imageBytes = inputStream?.readBytes() ?: return
                inputStream.close()

                // Show preview immediately
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ivProfilePhoto.setImageBitmap(bitmap)
                ivProfilePhoto.visibility = View.VISIBLE
                tvAvatarInitials.visibility = View.GONE

                // Upload to Supabase
                val fileName = "avatar_${System.currentTimeMillis()}.jpg"
                presenter.uploadPhoto(imageBytes, fileName)
            } catch (e: Exception) {
                toast("Failed to load image: ${e.message}")
            }
        }
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

    override fun populateProfile(name: String, email: String, phone: String, address: String, role: String, photoUrl: String?) {
        tvProfileName.text = name
        tvProfileEmail.text = email
        tvProfileRole.text = role.replaceFirstChar { it.uppercaseChar() }
        etFullName.setText(name)
        etPhone.setText(phone)
        etAddress.setText(address)

        if (!photoUrl.isNullOrEmpty()) {
            // Load photo from URL
            ivProfilePhoto.visibility = View.VISIBLE
            tvAvatarInitials.visibility = View.GONE
            loadImageFromUrl(photoUrl)
        } else {
            // Show initials
            ivProfilePhoto.visibility = View.GONE
            tvAvatarInitials.visibility = View.VISIBLE
            tvAvatarInitials.text = if (name.isNotEmpty()) name.first().uppercaseChar().toString() else "?"
        }
    }

    private fun loadImageFromUrl(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bitmap = BitmapFactory.decodeStream(URL(url).openStream())
                withContext(Dispatchers.Main) {
                    ivProfilePhoto.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    ivProfilePhoto.visibility = View.GONE
                    tvAvatarInitials.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun navigateBack() { finish() }

    override fun navigateToLogin() {
        start(LoginActivity::class.java)
        finish()
    }
}
