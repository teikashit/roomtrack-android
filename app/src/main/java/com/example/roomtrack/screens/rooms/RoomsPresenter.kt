package com.example.roomtrack.screens.rooms

import android.app.Activity
import com.example.roomtrack.data.UserSession
import com.example.roomtrack.model.RoomResponse
import com.example.roomtrack.utils.app
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomsPresenter(
    private val view: RoomsContract.View,
    private val model: RoomsModel
) : RoomsContract.Presenter {

    override fun loadRooms() {
        val app = (view as Activity).app()
        val session = app.getUserSession()
        val token = app.getToken()

        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (session.role.equals("landlord", ignoreCase = true)) {
                    val response = model.getAllRooms(token)
                    withContext(Dispatchers.Main) {
                        view.hideLoading()
                        if (response.isSuccessful) view.showLandlordRooms(response.body() ?: emptyList())
                        else view.showError("Failed to load rooms")
                    }
                } else {
                    val response = model.getRoomByTenantId(token, session.id)
                    withContext(Dispatchers.Main) {
                        view.hideLoading()
                        if (response.isSuccessful) {
                            val rooms = response.body()
                            if (!rooms.isNullOrEmpty()) view.showTenantRoom(rooms[0])
                            else view.showNoRoomAssigned()
                        } else {
                            view.showError("Failed to load your room details")
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

    override fun onAddRoomClicked() { view.showAddRoomDialog() }

    override fun onAddRoomSubmitted(unitNumber: String, monthlyRate: String, description: String) {
        if (unitNumber.isEmpty() || monthlyRate.isEmpty()) {
            view.showError("Unit number and monthly rate are required")
            return
        }
        val rate = monthlyRate.toDoubleOrNull()
        if (rate == null || rate <= 0) {
            view.showError("Please enter a valid monthly rate")
            return
        }
        val app = (view as Activity).app()
        val token = app.getToken()
        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.createRoom(token, unitNumber, rate, description.ifEmpty { null })
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) loadRooms()
                    else view.showError("Failed to add room. Unit number may already exist.")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.showError("Network error: ${e.message}")
                }
            }
        }
    }

    override fun onAssignTenantClicked(roomId: String) {
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
                        if (tenants.isEmpty()) {
                            view.showError("No tenants found. Ask tenants to register first.")
                        } else {
                            view.showAssignTenantDialog(roomId, tenants)
                        }
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

    override fun onAssignTenantSubmitted(roomId: String, tenantId: String, tenantName: String) {
        if (tenantId.isEmpty() || tenantName.isEmpty()) {
            view.showError("Please select a tenant")
            return
        }
        val app = (view as Activity).app()
        val token = app.getToken()
        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.assignTenant(token, roomId, tenantId, tenantName)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) {
                        view.showSuccess("Tenant assigned successfully!")
                        loadRooms()
                    } else {
                        view.showError("Failed to assign tenant")
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

    override fun onUnassignTenantClicked(roomId: String) {
        val app = (view as Activity).app()
        val token = app.getToken()
        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.unassignTenant(token, roomId)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    if (response.isSuccessful) {
                        view.showSuccess("Tenant unassigned")
                        loadRooms()
                    } else {
                        view.showError("Failed to unassign tenant")
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

    override fun onTenantNameClicked(room: RoomResponse) {
        view.showTenantInfoDialog(
            tenantName = room.tenant_name ?: "Unknown",
            roomName = "Unit ${room.unit_name}",
            status = room.status,
            rate = room.monthly_rate
        )
    }

    override fun onLogoutClicked() {
        val app = (view as Activity).app()
        app.setToken("")
        app.setUserSession(UserSession())
        view.navigateToLogin()
    }
}
