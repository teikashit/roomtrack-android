package com.example.roomtrack.screens.rooms

import com.example.roomtrack.model.ProfileResponse
import com.example.roomtrack.model.RoomResponse

class RoomsContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showSuccess(message: String)
        fun showLandlordRooms(rooms: List<RoomResponse>)
        fun showTenantRoom(room: RoomResponse)
        fun showNoRoomAssigned()
        fun showAddRoomDialog()
        fun showAssignTenantDialog(roomId: String, tenants: List<ProfileResponse>)
        fun showTenantInfoDialog(tenantName: String, roomName: String, status: String, rate: Double)
        fun navigateToLogin()
    }
    interface Presenter {
        fun loadRooms()
        fun onAddRoomClicked()
        fun onAddRoomSubmitted(unitNumber: String, monthlyRate: String, description: String)
        fun onAssignTenantClicked(roomId: String)
        fun onAssignTenantSubmitted(roomId: String, tenantId: String, tenantName: String)
        fun onUnassignTenantClicked(roomId: String)
        fun onTenantNameClicked(room: RoomResponse)
        fun onLogoutClicked()
    }
}
