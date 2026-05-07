package com.example.roomtrack.screens.payments

import android.app.Activity
import com.example.roomtrack.data.UserSession
import com.example.roomtrack.model.PaymentResponse
import com.example.roomtrack.utils.app
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentPresenter(
    private val view: PaymentContract.View,
    private val model: PaymentModel
) : PaymentContract.Presenter {

    override fun loadPayments() {
        val app = (view as Activity).app()
        val session = app.getUserSession()
        val token = app.getToken()

        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (session.role.equals("landlord", ignoreCase = true)) {
                    val response = model.getAllPayments(token)
                    withContext(Dispatchers.Main) {
                        view.hideLoading()
                        if (response.isSuccessful) {
                            val payments = response.body() ?: emptyList()
                            if (payments.isEmpty()) view.showNoPayments()
                            else view.showLandlordPayments(payments)
                        } else {
                            view.showError("Failed to load payments")
                        }
                    }
                } else {
                    val response = model.getPaymentsByTenant(token, session.id)
                    withContext(Dispatchers.Main) {
                        view.hideLoading()
                        if (response.isSuccessful) {
                            val payments = response.body() ?: emptyList()
                            if (payments.isEmpty()) view.showNoPayments()
                            else view.showTenantPayments(payments)
                        } else {
                            view.showError("Failed to load payments")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Network error: ${e.message}")
                }
            }
        }
    }

    override fun onAddPaymentClicked() {
        val app = (view as Activity).app()
        val token = app.getToken()
        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.getTenants(token)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) {
                        val tenants = response.body() ?: emptyList()
                        if (tenants.isEmpty()) view.showError("No tenants found")
                        else view.showAddPaymentDialog(tenants)
                    } else {
                        view.showError("Failed to load tenants")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Network error: ${e.message}")
                }
            }
        }
    }

    override fun onAddPaymentSubmitted(
        tenantId: String,
        tenantName: String,
        roomId: String,
        amount: String,
        dueDate: String,
        description: String
    ) {
        if (amount.isEmpty() || dueDate.isEmpty()) {
            view.showError("Amount and due date are required")
            return
        }
        val amountDouble = amount.toDoubleOrNull()
        if (amountDouble == null || amountDouble <= 0) {
            view.showError("Please enter a valid amount")
            return
        }
        val app = (view as Activity).app()
        val token = app.getToken()
        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.createPayment(
                    token = token,
                    tenantId = tenantId,
                    tenantName = tenantName,
                    roomId = roomId,
                    amount = amountDouble,
                    dueDate = dueDate,
                    description = description.ifEmpty { null }
                )
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) {
                        view.showSuccess("Payment record added")
                        loadPayments()
                    } else {
                        view.showError("Failed to add payment")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Network error: ${e.message}")
                }
            }
        }
    }

    override fun onMarkAsPaid(paymentId: String) {
        val app = (view as Activity).app()
        val token = app.getToken()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.markAsPaid(token, paymentId, today)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) {
                        view.showSuccess("Marked as Paid")
                        loadPayments()
                    } else {
                        view.showError("Failed to update payment")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Network error: ${e.message}")
                }
            }
        }
    }

    override fun onMarkAsForVerification(paymentId: String) {
        val app = (view as Activity).app()
        val token = app.getToken()
        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.updateStatus(token, paymentId, "For Verification")
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) {
                        view.showSuccess("Cash payment submitted. Waiting for landlord confirmation.")
                        loadPayments()
                    } else {
                        view.showError("Failed to update payment")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Network error: ${e.message}")
                }
            }
        }
    }

    override fun onConfirmCashReceipt(paymentId: String) {
        val app = (view as Activity).app()
        val token = app.getToken()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.markAsPaid(token, paymentId, today)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) {
                        view.showSuccess("Cash receipt confirmed!")
                        loadPayments()
                    } else {
                        view.showError("Failed to confirm receipt")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Network error: ${e.message}")
                }
            }
        }
    }

    override fun onPayNowClicked(payment: PaymentResponse) {
        view.navigateToCheckout(
            paymentId = payment.id,
            amount = payment.amount,
            description = payment.description ?: "Rent Payment"
        )
    }

    override fun onLogoutClicked() {
        val app = (view as Activity).app()
        app.setToken("")
        app.setUserSession(UserSession())
        view.navigateToLogin()
    }
}
