package com.example.roomtrack.screens.dashboard

import android.app.Activity
import com.example.roomtrack.data.UserSession
import com.example.roomtrack.utils.app
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardPresenter(
    private val view: DashboardContract.View,
    private val model: DashboardModel
) : DashboardContract.Presenter {

    override fun loadDashboard() {
        val app = (view as Activity).app()
        val session = app.getUserSession()
        val token = app.getToken()

        view.showWelcome(session.name)

        if (session.role.equals("landlord", ignoreCase = true)) {
            view.showLandlordView()
            loadLandlordStats(token)
        } else {
            view.showTenantView()
            loadTenantStats(token, session.id)
        }

        loadLatestAnnouncement(token)
    }

    private fun loadLandlordStats(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val roomsResponse = model.getAllRooms(token)
                val paymentsResponse = model.getAllPayments(token)

                withContext(Dispatchers.Main) {
                    val rooms = roomsResponse.body() ?: emptyList()
                    val payments = paymentsResponse.body() ?: emptyList()

                    val totalRooms = rooms.size
                    val vacantRooms = rooms.count { it.status.equals("vacant", ignoreCase = true) }
                    val pendingPayments = payments.count { it.status.equals("Pending", ignoreCase = true) }
                    val totalIncome = payments
                        .filter { it.status.equals("Paid", ignoreCase = true) }
                        .sumOf { it.amount }

                    view.showStats(
                        label1 = "TOTAL ROOMS",
                        value1 = "$totalRooms ($vacantRooms vacant)",
                        label2 = "PENDING PAYMENTS",
                        value2 = "$pendingPayments unpaid"
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.showStats("TOTAL ROOMS", "—", "PENDING PAYMENTS", "—")
                }
            }
        }
    }

    private fun loadTenantStats(token: String, tenantId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val roomResponse = model.getRoomByTenantId(token, tenantId)
                val paymentsResponse = model.getPaymentsByTenant(token, tenantId)

                withContext(Dispatchers.Main) {
                    val rooms = roomResponse.body()
                    val payments = paymentsResponse.body() ?: emptyList()

                    val roomLabel = if (!rooms.isNullOrEmpty()) "Unit ${rooms[0].unit_name}" else "No room assigned"
                    val pendingCount = payments.count { it.status.equals("Pending", ignoreCase = true) }

                    view.showStats(
                        label1 = "MY ROOM",
                        value1 = roomLabel,
                        label2 = "PENDING PAYMENTS",
                        value2 = if (pendingCount > 0) "$pendingCount unpaid" else "All paid ✓"
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.showStats("MY ROOM", "—", "PENDING PAYMENTS", "—")
                }
            }
        }
    }

    private fun loadLatestAnnouncement(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.getLatestAnnouncement(token)
                withContext(Dispatchers.Main) {
                    val announcements = response.body()
                    if (!announcements.isNullOrEmpty()) {
                        val latest = announcements[0]
                        view.showLatestAnnouncement(
                            title = latest.title ?: "Announcement",
                            content = latest.content ?: ""
                        )
                    } else {
                        view.showNoAnnouncement()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.showNoAnnouncement()
                }
            }
        }
    }

    override fun onProfileClicked() { view.navigateToProfile() }

    override fun onLogoutClicked() {
        val app = (view as Activity).app()
        app.setToken("")
        app.setUserSession(UserSession())
        view.navigateToLogin()
    }
}
