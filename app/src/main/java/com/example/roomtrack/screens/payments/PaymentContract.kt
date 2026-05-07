package com.example.roomtrack.screens.payments

import com.example.roomtrack.model.PaymentResponse
import com.example.roomtrack.model.ProfileResponse
import com.example.roomtrack.model.RoomResponse

class PaymentContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showSuccess(message: String)
        fun showLandlordPayments(payments: List<PaymentResponse>)
        fun showTenantPayments(payments: List<PaymentResponse>)
        fun showNoPayments()
        fun showAddPaymentDialog(tenants: List<ProfileResponse>)
        fun navigateToCheckout(paymentId: String, amount: Double, description: String)
        fun navigateToLogin()
    }
    interface Presenter {
        fun loadPayments()
        fun onAddPaymentClicked()
        fun onAddPaymentSubmitted(tenantId: String, tenantName: String, roomId: String, amount: String, dueDate: String, description: String)
        fun onMarkAsPaid(paymentId: String)
        fun onMarkAsForVerification(paymentId: String)
        fun onConfirmCashReceipt(paymentId: String)
        fun onPayNowClicked(payment: PaymentResponse)
        fun onLogoutClicked()
    }
}
