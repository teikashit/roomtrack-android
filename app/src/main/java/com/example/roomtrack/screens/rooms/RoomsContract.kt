package com.example.roomtrack.screens.rooms

import com.example.roomtrack.model.RoomResponse

class RoomsContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showLandlordRooms(rooms: List<RoomResponse>)
        fun showTenantRoom(room: RoomResponse)
        fun showNoRoomAssigned()
        fun showAddRoomDialog()
        fun navigateToLogin()
    }
    interface Presenter {
        fun loadRooms()
        fun onAddRoomClicked()
        fun onAddRoomSubmitted(unitNumber: String, monthlyRate: String, description: String)
        fun onLogoutClicked()
    }
}
