package com.example.roomtrack.screens.rooms

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.roomtrack.R
import com.example.roomtrack.model.ProfileResponse
import com.example.roomtrack.model.RoomResponse
import com.example.roomtrack.screens.announcements.AnnouncementActivity
import com.example.roomtrack.screens.dashboard.DashboardActivity
import com.example.roomtrack.screens.login.LoginActivity
import com.example.roomtrack.screens.payments.PaymentActivity
import com.example.roomtrack.screens.profile.ProfileActivity
import com.example.roomtrack.utils.NavHelper
import com.example.roomtrack.utils.app
import com.example.roomtrack.utils.start
import com.example.roomtrack.utils.toast
import com.example.roomtrack.utils.value

class RoomsActivity : Activity(), RoomsContract.View {

    private lateinit var presenter: RoomsPresenter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvRole: TextView
    private lateinit var tvInitials: TextView

    private lateinit var layoutLandlord: LinearLayout
    private lateinit var btnAddRoom: Button
    private lateinit var containerRooms: LinearLayout
    private lateinit var tvRoomCount: TextView

    private lateinit var layoutTenant: LinearLayout
    private lateinit var tvNoRoom: TextView
    private lateinit var layoutTenantRoom: LinearLayout
    private lateinit var tvUnitNumber: TextView
    private lateinit var tvMonthlyRate: TextView
    private lateinit var tvRoomStatus: TextView
    private lateinit var tvRoomDescription: TextView

    private lateinit var navHome: LinearLayout
    private lateinit var navRooms: LinearLayout
    private lateinit var navProfile: LinearLayout
    private lateinit var navPayments: LinearLayout
    private lateinit var navAnnouncements: LinearLayout

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
        navPayments = findViewById(R.id.navPayments)
        navAnnouncements = findViewById(R.id.navAnnouncements)
    }

    private fun setupHeader() {
        val session = app().getUserSession()
        tvRole.text = session.role.uppercase()
        tvInitials.text = if (session.name.isNotEmpty()) session.name.first().uppercaseChar().toString() else "?"
    }

    private fun setupListeners() {
        NavHelper.setupAvatarMenu(this, tvInitials)
        btnAddRoom.setOnClickListener { presenter.onAddRoomClicked() }
        navHome.setOnClickListener { start<DashboardActivity>(DashboardActivity::class.java); finish() }
        navRooms.setOnClickListener { /* already here */ }
        navPayments.setOnClickListener { start<PaymentActivity>(PaymentActivity::class.java) }
        navAnnouncements.setOnClickListener { start<AnnouncementActivity>(AnnouncementActivity::class.java) }
        navProfile.setOnClickListener { start<ProfileActivity>(ProfileActivity::class.java) }
    }

    override fun showLoading() { progressBar.visibility = View.VISIBLE }
    override fun hideLoading() { progressBar.visibility = View.GONE }
    override fun showError(message: String) { toast(message) }
    override fun showSuccess(message: String) { toast(message) }

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

        val tvCardStatus = card.findViewById<TextView>(R.id.tvCardStatus)
        tvCardStatus.text = room.status.replaceFirstChar { it.uppercaseChar() }
        tvCardStatus.setTextColor(
            if (room.status.equals("occupied", ignoreCase = true)) 0xFF10B981.toInt()
            else 0xFFF59E0B.toInt()
        )

        val tvCardTenant = card.findViewById<TextView>(R.id.tvCardTenant)
        if (!room.tenant_name.isNullOrEmpty()) {
            tvCardTenant.text = room.tenant_name
            tvCardTenant.setTextColor(0xFF1E40AF.toInt())
            tvCardTenant.setOnClickListener { presenter.onTenantNameClicked(room) }
        } else {
            tvCardTenant.text = "Unassigned"
            tvCardTenant.setTextColor(0xFF94A3B8.toInt())
            tvCardTenant.setOnClickListener(null)
        }

        val btnAssign = card.findViewById<Button>(R.id.btnAssign)
        if (room.status.equals("occupied", ignoreCase = true) && !room.tenant_name.isNullOrEmpty()) {
            btnAssign.text = "Unassign"
            btnAssign.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFFEF4444.toInt())
            btnAssign.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Unassign Tenant")
                    .setMessage("Remove ${room.tenant_name} from Unit ${room.unit_name}?")
                    .setPositiveButton("Unassign") { _, _ -> presenter.onUnassignTenantClicked(room.id) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        } else {
            btnAssign.text = "Assign Tenant"
            btnAssign.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF1E40AF.toInt())
            btnAssign.setOnClickListener { presenter.onAssignTenantClicked(room.id) }
        }

        return card
    }

    override fun showTenantRoom(room: RoomResponse) {
        layoutLandlord.visibility = View.GONE
        layoutTenant.visibility = View.VISIBLE
        tvNoRoom.visibility = View.GONE
        layoutTenantRoom.visibility = View.VISIBLE
        tvUnitNumber.text = "Unit ${room.unit_name}"
        tvMonthlyRate.text = "₱${String.format("%,.0f", room.monthly_rate)}"
        tvRoomStatus.text = room.status.replaceFirstChar { it.uppercaseChar() }
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

    override fun showAssignTenantDialog(roomId: String, tenants: List<ProfileResponse>) {
        // Build display names for the spinner
        val tenantNames = tenants.map { it.full_name ?: "Unknown (${it.id.take(8)})" }
        var selectedIndex = 0

        val spinner = Spinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tenantNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedIndex = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Wrap spinner in a LinearLayout with padding
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(48, 16, 48, 0)
        container.addView(spinner)

        AlertDialog.Builder(this)
            .setTitle("Assign Tenant")
            .setMessage("Select a tenant to assign to this room:")
            .setView(container)
            .setPositiveButton("Assign") { _, _ ->
                val selected = tenants[selectedIndex]
                presenter.onAssignTenantSubmitted(
                    roomId = roomId,
                    tenantId = selected.id,
                    tenantName = selected.full_name ?: "Unknown"
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun showTenantInfoDialog(tenantName: String, roomName: String, status: String, rate: Double) {
        AlertDialog.Builder(this)
            .setTitle("👤 Tenant Info")
            .setMessage(
                "Name: $tenantName\n" +
                        "Room: $roomName\n" +
                        "Status: ${status.replaceFirstChar { it.uppercaseChar() }}\n" +
                        "Monthly Rate: ₱${String.format("%,.0f", rate)}"
            )
            .setPositiveButton("OK", null)
            .show()
    }

    override fun navigateToLogin() {
        start(LoginActivity::class.java)
        finish()
    }
}
