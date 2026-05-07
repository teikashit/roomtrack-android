package com.example.roomtrack.screens.checkout

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.roomtrack.R
import com.example.roomtrack.screens.payments.PaymentActivity
import com.example.roomtrack.screens.payments.PaymentModel
import com.example.roomtrack.utils.app
import com.example.roomtrack.utils.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckoutActivity : Activity() {

    private lateinit var tvAmount: TextView
    private lateinit var tvDescription: TextView
    private lateinit var radioGcash: RadioButton
    private lateinit var radioMaya: RadioButton
    private lateinit var radioCard: RadioButton
    private lateinit var radioCash: RadioButton
    private lateinit var layoutGcashMaya: LinearLayout
    private lateinit var layoutCard: LinearLayout
    private lateinit var layoutCash: LinearLayout
    private lateinit var etPhone: EditText
    private lateinit var etCardNumber: EditText
    private lateinit var etCardExpiry: EditText
    private lateinit var etCardCvv: EditText
    private lateinit var btnConfirmPayment: Button
    private lateinit var tvBack: TextView
    private lateinit var progressBar: ProgressBar

    private var paymentId = ""
    private var amount = 0.0
    private var description = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        initViews()
        loadIntentData()
        setupListeners()
    }

    private fun initViews() {
        tvAmount = findViewById(R.id.tvAmount)
        tvDescription = findViewById(R.id.tvDescription)
        radioGcash = findViewById(R.id.radioGcash)
        radioMaya = findViewById(R.id.radioMaya)
        radioCard = findViewById(R.id.radioCard)
        radioCash = findViewById(R.id.radioCash)
        layoutGcashMaya = findViewById(R.id.layoutGcashMaya)
        layoutCard = findViewById(R.id.layoutCard)
        layoutCash = findViewById(R.id.layoutCash)
        etPhone = findViewById(R.id.etPhone)
        etCardNumber = findViewById(R.id.etCardNumber)
        etCardExpiry = findViewById(R.id.etCardExpiry)
        etCardCvv = findViewById(R.id.etCardCvv)
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment)
        tvBack = findViewById(R.id.tvBack)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun loadIntentData() {
        paymentId = intent.getStringExtra("PAYMENT_ID") ?: ""
        amount = intent.getDoubleExtra("AMOUNT", 0.0)
        description = intent.getStringExtra("DESCRIPTION") ?: "Rent Payment"
        tvAmount.text = "₱${String.format("%,.0f", amount)}"
        tvDescription.text = description
    }

    private fun setupListeners() {
        tvBack.setOnClickListener { finish() }

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupPayment)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            layoutGcashMaya.visibility = View.GONE
            layoutCard.visibility = View.GONE
            layoutCash.visibility = View.GONE
            when (checkedId) {
                R.id.radioGcash, R.id.radioMaya -> layoutGcashMaya.visibility = View.VISIBLE
                R.id.radioCard -> layoutCard.visibility = View.VISIBLE
                R.id.radioCash -> layoutCash.visibility = View.VISIBLE
            }
        }

        btnConfirmPayment.setOnClickListener { handlePayment() }
    }

    private fun handlePayment() {
        val phone = etPhone.text.toString().trim()
        when {
            radioGcash.isChecked -> {
                if (phone.isEmpty()) { toast("Please enter your GCash number"); return }
                showConfirmDialog("GCash") { markAsPaid("GCash") }
            }
            radioMaya.isChecked -> {
                if (phone.isEmpty()) { toast("Please enter your Maya number"); return }
                showConfirmDialog("Maya") { markAsPaid("Maya") }
            }
            radioCard.isChecked -> processCardPayment()
            radioCash.isChecked -> processCashPayment()
            else -> toast("Please select a payment method")
        }
    }

    private fun processOnlinePayment(method: String) {
        val phone = etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            toast("Please enter your $method number")
            return
        }
        if (phone.length < 10) {
            toast("Please enter a valid phone number")
            return
        }
        showConfirmDialog(method) { markAsPaid(method) }
    }

    private fun processCardPayment() {
        val cardNumber = etCardNumber.text.toString().trim()
        val expiry = etCardExpiry.text.toString().trim()
        val cvv = etCardCvv.text.toString().trim()
        if (cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
            toast("Please fill in all card details")
            return
        }
        if (cardNumber.replace(" ", "").length < 16) {
            toast("Please enter a valid card number")
            return
        }
        showConfirmDialog("Credit/Debit Card") { markAsPaid("Card") }
    }

    private fun processCashPayment() {
        AlertDialog.Builder(this)
            .setTitle("Cash Payment")
            .setMessage("Your landlord will be notified of your cash payment intent.\n\nPlease prepare ₱${String.format("%,.0f", amount)} and hand it to your landlord.")
            .setPositiveButton("Confirm") { _, _ -> markAsForVerification() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showConfirmDialog(method: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Payment")
            .setMessage("Pay ₱${String.format("%,.0f", amount)} via $method?\n\n$description")
            .setPositiveButton("Pay Now") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun markAsPaid(method: String) {
        progressBar.visibility = View.VISIBLE
        btnConfirmPayment.isEnabled = false
        val token = app().getToken()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = PaymentModel().markAsPaid(token, paymentId, today)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnConfirmPayment.isEnabled = true
                    if (response.isSuccessful) {
                        showReceiptDialog(method)
                    } else {
                        toast("Payment failed. Please try again.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnConfirmPayment.isEnabled = true
                    toast("Network error: ${e.message}")
                }
            }
        }
    }

    private fun markAsForVerification() {
        progressBar.visibility = View.VISIBLE
        btnConfirmPayment.isEnabled = false
        val token = app().getToken()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = PaymentModel().updateStatus(token, paymentId, "For Verification")
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnConfirmPayment.isEnabled = true
                    if (response.isSuccessful) {
                        showCashConfirmationDialog()
                    } else {
                        toast("Failed to submit. Please try again.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnConfirmPayment.isEnabled = true
                    toast("Network error: ${e.message}")
                }
            }
        }
    }

    private fun showReceiptDialog(method: String) {
        val today = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        AlertDialog.Builder(this)
            .setTitle("✅ Payment Successful!")
            .setMessage(
                "Amount: ₱${String.format("%,.0f", amount)}\n" +
                        "Method: $method\n" +
                        "Description: $description\n" +
                        "Date: $today\n\n" +
                        "Thank you for your payment!"
            )
            .setPositiveButton("Done") { _, _ ->
                val intent = Intent(this, PaymentActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showCashConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("📋 Cash Payment Submitted")
            .setMessage(
                "Amount: ₱${String.format("%,.0f", amount)}\n" +
                        "Method: Cash\n" +
                        "Status: For Verification\n\n" +
                        "Please hand ₱${String.format("%,.0f", amount)} to your landlord.\n" +
                        "Your payment will be confirmed once received."
            )
            .setPositiveButton("Done") { _, _ ->
                val intent = Intent(this, PaymentActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }
}
