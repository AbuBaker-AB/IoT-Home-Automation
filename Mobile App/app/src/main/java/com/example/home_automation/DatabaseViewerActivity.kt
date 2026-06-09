package com.example.home_automation

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.home_automation.viewmodel.DeviceViewModel

class DatabaseViewerActivity : AppCompatActivity() {

    private lateinit var recyclerViewDevices: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var tvDeviceCount: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnRefresh: ImageView

    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var deviceViewModel: DeviceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_viewer)

        initializeViews()
        setupRecyclerView()
        initializeViewModel()
        setupClickListeners()
        observeDevices()
    }

    private fun initializeViews() {
        recyclerViewDevices = findViewById(R.id.recyclerViewDevices)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        tvDeviceCount = findViewById(R.id.tvDeviceCount)
        btnBack = findViewById(R.id.btnBack)
        btnRefresh = findViewById(R.id.btnRefresh)
    }

    private fun setupRecyclerView() {
        deviceAdapter = DeviceAdapter()
        recyclerViewDevices.apply {
            layoutManager = LinearLayoutManager(this@DatabaseViewerActivity)
            adapter = deviceAdapter
            setHasFixedSize(true)
        }
    }

    private fun initializeViewModel() {
        deviceViewModel = ViewModelProvider(this)[DeviceViewModel::class.java]
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnRefresh.setOnClickListener {
            refreshDevices()
        }
    }

    private fun observeDevices() {
        deviceViewModel.allDevices.observe(this) { devices ->
            devices?.let { deviceList ->
                if (deviceList.isEmpty()) {
                    showEmptyState()
                } else {
                    showDeviceList()
                    deviceAdapter.submitList(deviceList)
                    updateDeviceCount(deviceList.size)
                }
            }
        }
    }

    private fun showEmptyState() {
        recyclerViewDevices.visibility = android.view.View.GONE
        emptyStateLayout.visibility = android.view.View.VISIBLE
        tvDeviceCount.text = "0 devices"
    }

    private fun showDeviceList() {
        recyclerViewDevices.visibility = android.view.View.VISIBLE
        emptyStateLayout.visibility = android.view.View.GONE
    }

    private fun updateDeviceCount(count: Int) {
        tvDeviceCount.text = if (count == 1) "1 device" else "$count devices"
    }

    private fun refreshDevices() {
        // Refresh data by re-observing
        // The ViewModel will automatically provide the latest data
        android.widget.Toast.makeText(this, "🔄 Refreshing devices...", android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
