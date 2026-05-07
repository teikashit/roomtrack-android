package com.example.roomtrack.screens.payments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.roomtrack.R
import com.example.roomtrack.model.PaymentResponse
import com.example.roomtrack.model.ProfileResponse
import com.example.roomtrack.model.RoomResponse
import com.example.roomtrack.screens.announcements.AnnouncementActivity
import com.example.roomtrack.screens.checkout.CheckoutActivity
import com.example.roomtrack.screens.dashboard.DashboardActivity
import com.example.roomtrack.screens.login.LoginActivity
import com.example.roomtrack.screens.profile.ProfileActivity
import com.example.roomtrack.screens.rooms.RoomsActivity
import com.example.roomtrack.utils.NavHelper
import com.example.roomtrack.utils.app
import com.example.roomtrack.utils.start
import com.example.roomtrack.utils.toast
import com.example.roomtrack.utils.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentActivity : Activity(), PaymentContract.View {

    private lateinit var presenter: PaymentPresenter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvRole: TextView
    private lateinit var tvInitials: TextView
    private lateinit var tvPageTitle: TextView
    private lateinit var tvPageSubtitle: TextView
    private lateinit var btnAddPayment: Button
    private lateinit var containerPayments: LinearLayout
    private lateinit var tvNoPayments: TextView
    private lateinit var navHome: LinearLayout
    private lateinit var navRooms: LinearLayout
    private lateinit var navPayments: LinearLayout
    private lateinit var navProfile: LinearLayout
    private lateinit var navAnnouncements: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        initViews()
        setupHeader()
        presenter = PaymentPresenter(this, PaymentModel())
        setupListeners()
        presenter.loadPayments()
    }

    private fun initViews() {
        progressBar = findViewById(R.id.progressBar)
        tvRole = findViewById(R.id.tvRole)
        tvInitials = findViewById(R.id.tvInitials)
        tvPageTitle = findViewById(R.id.tvPageTitle)
        tvPageSubtitle = findViewById(R.id.tvPageSubtitle)
        btnAddPayment = findViewById(R.id.btnAddPayment)
        containerPayments = findViewById(R.id.containerPayments)
        tvNoPayments = findViewById(R.id.tvNoPayments)
        navHome = findViewById(R.id.navHome)
        navRooms = findViewById(R.id.navRooms)
        navPayments = findViewById(R.id.navPayments)
        navProfile = findViewById(R.id.navProfile)
        navAnnouncements = findViewById(R.id.navAnnouncements)
    }

    private fun setupHeader() {
        val session = app().getUserSession()
        tvRole.text = session.role.uppercase()
        tvInitials.text = if (session.name.isNotEmpty()) session.name.first().uppercaseChar().toString() else "?"
    }

    private fun setupListeners() {
        NavHelper.setupAvatarMenu(this, tvInitials)
        btnAddPayment.setOnClickListener { presenter.onAddPaymentClicked() }
        navHome.setOnClickListener { start<DashboardActivity>(DashboardActivity::class.java); finish() }
        navRooms.setOnClickListener { start<RoomsActivity>(RoomsActivity::class.java) }
        navPayments.setOnClickListener { /* already here */ }
        navAnnouncements.setOnClickListener { start<AnnouncementActivity>(AnnouncementActivity::class.java) }
        navProfile.setOnClickListener { start<ProfileActivity>(ProfileActivity::class.java) }
    }

    override fun showLoading() { progressBar.visibility = View.VISIBLE }
    override fun hideLoading() { progressBar.visibility = View.GONE }
    override fun showError(message: String) { toast(message) }
    override fun showSuccess(message: String) { toast(message) }

    override fun showLandlordPayments(payments: List<PaymentResponse>) {
        tvPageTitle.text = "Payment Records"
        tvPageSubtitle.text = "${payments.size} record(s) total"
        btnAddPayment.visibility = View.VISIBLE
        tvNoPayments.visibility = View.GONE
        renderPaymentCards(payments, isLandlord = true)
    }

    override fun showTenantPayments(payments: List<PaymentResponse>) {
        tvPageTitle.text = "My Payments"
        tvPageSubtitle.text = "Your payment history"
        btnAddPayment.visibility = View.GONE
        tvNoPayments.visibility = View.GONE
        renderPaymentCards(payments, isLandlord = false)
    }

    override fun showNoPayments() {
        val session = app().getUserSession()
        if (session.role.equals("landlord", ignoreCase = true)) {
            tvPageTitle.text = "Payment Records"
            tvPageSubtitle.text = "0 record(s) total"
            btnAddPayment.visibility = View.VISIBLE
        } else {
            tvPageTitle.text = "My Payments"
            tvPageSubtitle.text = "Your payment history"
            btnAddPayment.visibility = View.GONE
        }
        tvNoPayments.visibility = View.VISIBLE
        containerPayments.removeAllViews()
    }

    private fun renderPaymentCards(payments: List<PaymentResponse>, isLandlord: Boolean) {
        containerPayments.removeAllViews()
        payments.forEach { payment -> containerPayments.addView(buildPaymentCard(payment, isLandlord)) }
    }

    private fun buildPaymentCard(payment: PaymentResponse, isLandlord: Boolean): View {
        val card = layoutInflater.inflate(R.layout.item_payment_card, containerPayments, false)
        card.findViewById<TextView>(R.id.tvCardAmount).text = "₱${String.format("%,.0f", payment.amount)}"
        card.findViewById<TextView>(R.id.tvCardDueDate).text = "Due: ${payment.due_date ?: "—"}"
        card.findViewById<TextView>(R.id.tvCardDescription).text = payment.description ?: "No description"
        card.findViewById<TextView>(R.id.tvCardTenantName).text = payment.tenant_name ?: "—"

        val tvCardStatus = card.findViewById<TextView>(R.id.tvCardStatus)
        tvCardStatus.text = payment.status
        when {
            payment.status.equals("Paid", ignoreCase = true) ->
                tvCardStatus.setTextColor(0xFF10B981.toInt())
            payment.status.equals("For Verification", ignoreCase = true) ->
                tvCardStatus.setTextColor(0xFF6366F1.toInt())
            else ->
                tvCardStatus.setTextColor(0xFFF59E0B.toInt())
        }

        val tvPaidDate = card.findViewById<TextView>(R.id.tvPaidDate)
        if (!payment.paid_date.isNullOrEmpty()) {
            tvPaidDate.visibility = View.VISIBLE
            tvPaidDate.text = "Paid on: ${payment.paid_date}"
        } else {
            tvPaidDate.visibility = View.GONE
        }

        val btnMarkPaid = card.findViewById<Button>(R.id.btnMarkPaid)
        val btnPayNow = card.findViewById<Button>(R.id.btnPayNow)

        if (isLandlord) {
            btnPayNow.visibility = View.GONE
            when {
                payment.status.equals("Pending", ignoreCase = true) -> {
                    btnMarkPaid.visibility = View.VISIBLE
                    btnMarkPaid.text = "Mark as Paid"
                    btnMarkPaid.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF10B981.toInt())
                    btnMarkPaid.setOnClickListener { presenter.onMarkAsPaid(payment.id) }
                }
                payment.status.equals("For Verification", ignoreCase = true) -> {
                    btnMarkPaid.visibility = View.VISIBLE
                    btnMarkPaid.text = "Confirm Cash Receipt"
                    btnMarkPaid.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF6366F1.toInt())
                    btnMarkPaid.setOnClickListener { presenter.onConfirmCashReceipt(payment.id) }
                }
                else -> btnMarkPaid.visibility = View.GONE
            }
        } else {
            btnMarkPaid.visibility = View.GONE
            if (payment.status.equals("Pending", ignoreCase = true)) {
                btnPayNow.visibility = View.VISIBLE
                btnPayNow.setOnClickListener { presenter.onPayNowClicked(payment) }
            } else {
                btnPayNow.visibility = View.GONE
            }
        }

        return card
    }

    override fun showAddPaymentDialog(tenants: List<ProfileResponse>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_payment_v2, null)
        val spinnerTenant = dialogView.findViewById<Spinner>(R.id.spinnerTenant)
        val etAmount = dialogView.findViewById<EditText>(R.id.etDialogAmount)
        val etDueDate = dialogView.findViewById<EditText>(R.id.etDialogDueDate)
        val etDesc = dialogView.findViewById<EditText>(R.id.etDialogDesc)
        val tvRoomInfo = dialogView.findViewById<TextView>(R.id.tvRoomInfo)

        // Make due date open a date picker instead of keyboard
        etDueDate.isFocusable = false
        etDueDate.isClickable = true
        etDueDate.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            android.app.DatePickerDialog(
                this,
                { _, year, month, day ->
                    etDueDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            ).show()
        }

        val tenantNames = tenants.map { it.full_name ?: "Unknown" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tenantNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTenant.adapter = adapter

        var selectedTenant = tenants[0]
        var selectedRoomId = ""

        spinnerTenant.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedTenant = tenants[position]
                tvRoomInfo.text = "Fetching room..."
                fetchTenantRoom(selectedTenant.id, tvRoomInfo) { roomId ->
                    selectedRoomId = roomId
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        AlertDialog.Builder(this)
            .setTitle("Add Payment Record")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                presenter.onAddPaymentSubmitted(
                    tenantId = selectedTenant.id,
                    tenantName = selectedTenant.full_name ?: "Unknown",
                    roomId = selectedRoomId,
                    amount = etAmount.value(),
                    dueDate = etDueDate.value(),
                    description = etDesc.value()
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun fetchTenantRoom(tenantId: String, tvRoomInfo: TextView, onResult: (String) -> Unit) {
        val token = app().getToken()
        android.os.Handler(mainLooper).post {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                try {
                    val response = PaymentModel().getRoomByTenantId(token, tenantId)
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        val rooms = response.body()
                        if (!rooms.isNullOrEmpty()) {
                            val room = rooms[0]
                            tvRoomInfo.text = "Room: Unit ${room.unit_name} — ₱${String.format("%,.0f", room.monthly_rate)}/mo"
                            onResult(room.id)
                        } else {
                            tvRoomInfo.text = "No room assigned to this tenant"
                            onResult("")
                        }
                    }
                } catch (e: Exception) {
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        tvRoomInfo.text = "Could not fetch room"
                        onResult("")
                    }
                }
            }
        }
    }

    override fun navigateToCheckout(paymentId: String, amount: Double, description: String) {
        val intent = Intent(this, CheckoutActivity::class.java)
        intent.putExtra("PAYMENT_ID", paymentId)
        intent.putExtra("AMOUNT", amount)
        intent.putExtra("DESCRIPTION", description)
        startActivity(intent)
    }

    override fun navigateToLogin() {
        start(LoginActivity::class.java)
        finish()
    }
}
