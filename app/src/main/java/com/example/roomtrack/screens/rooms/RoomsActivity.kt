package com.example.roomtrack.screens.rooms

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.roomtrack.R
import com.example.roomtrack.model.RoomResponse
import com.example.roomtrack.screens.login.LoginActivity
import com.example.roomtrack.utils.app
import com.example.roomtrack.utils.start
import com.example.roomtrack.utils.toast
import com.example.roomtrack.utils.value

class RoomsActivity : Activity(), RoomsContract.View {

    private lateinit var presenter: RoomsPresenter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvRole: TextView
    private lateinit var tvInitials: TextView

    // Landlord views
    private lateinit var layoutLandlord: LinearLayout
    private lateinit var btnAddRoom: Button
    private lateinit var containerRooms: LinearLayout
    private lateinit var tvRoomCount: TextView

    // Tenant views
    private lateinit var layoutTenant: LinearLayout
    private lateinit var tvNoRoom: TextView
    private lateinit var layoutTenantRoom: LinearLayout
    private lateinit var tvUnitNumber: TextView
    private lateinit var tvMonthlyRate: TextView
    private lateinit var tvRoomStatus: TextView
    private lateinit var tvRoomDescription: TextView

    // Nav
    private lateinit var navHome: LinearLayout
    private lateinit var navRooms: LinearLayout
    private lateinit var navProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms)
        initViews()
        setupHeader()
        presenter = RoomsPresenter(this, RoomsModel())
        setupListeners()
        presenter.loadRooms()
    }

    private fun initViews() {
        progressBar = findViewById(R.id.progressBar)
        tvRole = findViewById(R.id.tvRole)
        tvInitials = findViewById(R.id.tvInitials)

        layoutLandlord = findViewById(R.id.layoutLandlord)
        btnAddRoom = findViewById(R.id.btnAddRoom)
        containerRooms = findViewById(R.id.containerRooms)
        tvRoomCount = findViewById(R.id.tvRoomCount)

        layoutTenant = findViewById(R.id.layoutTenant)
        tvNoRoom = findViewById(R.id.tvNoRoom)
        layoutTenantRoom = findViewById(R.id.layoutTenantRoom)
        tvUnitNumber = findViewById(R.id.tvUnitNumber)
        tvMonthlyRate = findViewById(R.id.tvMonthlyRate)
        tvRoomStatus = findViewById(R.id.tvRoomStatus)
        tvRoomDescription = findViewById(R.id.tvRoomDescription)

        navHome = findViewById(R.id.navHome)
        navRooms = findViewById(R.id.navRooms)
        navProfile = findViewById(R.id.navProfile)
    }

    private fun setupHeader() {
        val session = app().getUserSession()
        tvRole.text = session.role.uppercase()
        tvInitials.text = if (session.name.isNotEmpty()) session.name.first().uppercaseChar().toString() else "?"
    }

    private fun setupListeners() {
        btnAddRoom.setOnClickListener { presenter.onAddRoomClicked() }
        navHome.setOnClickListener { finish() }
        navProfile.setOnClickListener { toast("Profile — navigate there from Dashboard") }
        navRooms.setOnClickListener { /* already here */ }
    }

    override fun showLoading() { progressBar.visibility = View.VISIBLE }
    override fun hideLoading() { progressBar.visibility = View.GONE }
    override fun showError(message: String) { toast(message) }

    override fun showLandlordRooms(rooms: List<RoomResponse>) {
        layoutLandlord.visibility = View.VISIBLE
        layoutTenant.visibility = View.GONE
        containerRooms.removeAllViews()

        tvRoomCount.text = "${rooms.size} unit(s) total"

        if (rooms.isEmpty()) {
            val tvEmpty = TextView(this)
            tvEmpty.text = "No rooms yet. Tap '+ Add Room' to get started."
            tvEmpty.textSize = 14f
            tvEmpty.setTextColor(0xFF64748B.toInt())
            tvEmpty.setPadding(0, 32, 0, 0)
            containerRooms.addView(tvEmpty)
            return
        }

        rooms.forEach { room -> containerRooms.addView(buildRoomCard(room)) }
    }

    private fun buildRoomCard(room: RoomResponse): View {
        val card = layoutInflater.inflate(R.layout.item_room_card, containerRooms, false)

        card.findViewById<TextView>(R.id.tvCardUnit).text = "Unit ${room.unit_name}"
        card.findViewById<TextView>(R.id.tvCardRate).text = "₱${String.format("%,.0f", room.monthly_rate)}/mo"
        card.findViewById<TextView>(R.id.tvCardTenant).text = "—"

        val tvCardStatus = card.findViewById<TextView>(R.id.tvCardStatus)
        tvCardStatus.text = room.status
        tvCardStatus.setTextColor(
            if (room.status.equals("Occupied", ignoreCase = true)) 0xFF10B981.toInt()
            else 0xFFF59E0B.toInt()
        )

        return card
    }

    override fun showTenantRoom(room: RoomResponse) {
        layoutLandlord.visibility = View.GONE
        layoutTenant.visibility = View.VISIBLE
        tvNoRoom.visibility = View.GONE
        layoutTenantRoom.visibility = View.VISIBLE

        tvUnitNumber.text = "Unit ${room.unit_name}"
        tvMonthlyRate.text = "₱${String.format("%,.0f", room.monthly_rate)}"
        tvRoomStatus.text = room.status
        tvRoomDescription.text = room.description ?: "No description available."
    }

    override fun showNoRoomAssigned() {
        layoutLandlord.visibility = View.GONE
        layoutTenant.visibility = View.VISIBLE
        tvNoRoom.visibility = View.VISIBLE
        layoutTenantRoom.visibility = View.GONE
    }

    override fun showAddRoomDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_room, null)
        val etUnit = dialogView.findViewById<EditText>(R.id.etDialogUnit)
        val etRate = dialogView.findViewById<EditText>(R.id.etDialogRate)
        val etDesc = dialogView.findViewById<EditText>(R.id.etDialogDesc)

        AlertDialog.Builder(this)
            .setTitle("Add New Room")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                presenter.onAddRoomSubmitted(etUnit.value(), etRate.value(), etDesc.value())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun navigateToLogin() {
        start(LoginActivity::class.java)
        finish()
    }
}
