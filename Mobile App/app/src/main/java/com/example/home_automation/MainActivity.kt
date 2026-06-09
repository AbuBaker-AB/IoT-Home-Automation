package com.example.home_automation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.example.home_automation.viewmodel.DeviceViewModel
import com.example.home_automation.database.SensorData
import com.example.home_automation.firebase.FirebaseService
import java.util.concurrent.ConcurrentHashMap

class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var tvTemperature: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvMotionStatus: TextView

    private lateinit var switchFan1: SwitchMaterial
    private lateinit var switchFan2: SwitchMaterial
    private lateinit var switchFan3: SwitchMaterial
    private lateinit var switchLight: SwitchMaterial

    private lateinit var btnAllOn: MaterialButton
    private lateinit var btnAllOff: MaterialButton

    // Database ViewModel
    private lateinit var deviceViewModel: DeviceViewModel

    // Firebase service for sensor data
    private val firebaseService = FirebaseService.getInstance()

    // Flag to prevent recursive updates
    private var isUpdatingFromDatabase = false

    // Track latest local update times to avoid echo from Firebase
    private val localUpdateTimestamps = ConcurrentHashMap<String, Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        initializeViewModel()
        setupClickListeners()
        observeDeviceStates()
        setupFirebaseListeners()
    }

    private fun initializeViews() {
        // Sensor displays
        tvTemperature = findViewById(R.id.tvTemperature)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvMotionStatus = findViewById(R.id.tvMotionStatus)

        // Device controls
        switchFan1 = findViewById(R.id.switchFan1)
        switchFan2 = findViewById(R.id.switchFan2)
        switchFan3 = findViewById(R.id.switchFan3)
        switchLight = findViewById(R.id.switchLight)

        // Quick action buttons
        btnAllOn = findViewById(R.id.btnAllOn)
        btnAllOff = findViewById(R.id.btnAllOff)
    }

    private fun initializeViewModel() {
        deviceViewModel = ViewModelProvider(this)[DeviceViewModel::class.java]
    }

    private fun observeDeviceStates() {
        // Observe all devices and update UI accordingly
        deviceViewModel.allDevices.observe(this, Observer { devices ->
            isUpdatingFromDatabase = true

            devices?.let { deviceList ->
                for (device in deviceList) {
                    when (device.id) {
                        "fan1" -> switchFan1.isChecked = device.isOn
                        "fan2" -> switchFan2.isChecked = device.isOn
                        "fan3" -> switchFan3.isChecked = device.isOn
                        "light1" -> switchLight.isChecked = device.isOn
                    }
                }
            }

            isUpdatingFromDatabase = false
        })
    }

    private fun setupClickListeners() {
        // Fan 1 control
        switchFan1.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingFromDatabase) {
                val now = System.currentTimeMillis()
                localUpdateTimestamps["fan1"] = now
                updateDeviceInDatabase("fan1", isChecked)
                val status = if (isChecked) "ON" else "OFF"
                showToast("\uD83C\uDF00 Living Room Fan turned $status")
            }
        }

        // Fan 2 control
        switchFan2.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingFromDatabase) {
                val now = System.currentTimeMillis()
                localUpdateTimestamps["fan2"] = now
                updateDeviceInDatabase("fan2", isChecked)
                val status = if (isChecked) "ON" else "OFF"
                showToast("\uD83C\uDF00 Bedroom Fan turned $status")
            }
        }

        // Fan 3 control
        switchFan3.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingFromDatabase) {
                val now = System.currentTimeMillis()
                localUpdateTimestamps["fan3"] = now
                updateDeviceInDatabase("fan3", isChecked)
                val status = if (isChecked) "ON" else "OFF"
                showToast("\uD83C\uDF00 Kitchen Fan turned $status")
            }
        }

        // Light control
        switchLight.setOnCheckedChangeListener { _, isChecked ->
            if (!isUpdatingFromDatabase) {
                val now = System.currentTimeMillis()
                localUpdateTimestamps["light1"] = now
                updateDeviceInDatabase("light1", isChecked)
                val status = if (isChecked) "ON" else "OFF"
                showToast("\uD83D\uDCA1 Main Light turned $status")
            }
        }

        // All ON button
        btnAllOn.setOnClickListener {
            updateAllDevices(true)
            showToast("\u2705 All devices turned ON")
        }

        // All OFF button
        btnAllOff.setOnClickListener {
            updateAllDevices(false)
            showToast("\u274C All devices turned OFF")
        }
    }

    private fun updateAllDevices(isOn: Boolean) {
        updateDeviceInDatabase("fan1", isOn)
        updateDeviceInDatabase("fan2", isOn)
        updateDeviceInDatabase("fan3", isOn)
        updateDeviceInDatabase("light1", isOn)
    }

    private fun updateDeviceInDatabase(deviceId: String, isOn: Boolean) {
        deviceViewModel.updateDeviceState(deviceId, isOn)

        // Log the database update for debugging
        val status = if (isOn) "ON" else "OFF"
        println("Database Updated: Device $deviceId is now $status at ${System.currentTimeMillis()}")
    }

    private fun setupFirebaseListeners() {
        // Listen for sensor data updates from Firebase
        firebaseService.listenForSensorData { sensorData ->
            runOnUiThread {
                updateSensorUI(sensorData)
            }
        }

        // Listen for device state changes from Firebase (for synchronization)
        firebaseService.listenForDeviceStates { deviceId, isOn, remoteTs ->
            // If we recently initiated this change locally, ignore the echo
            val localTs = localUpdateTimestamps[deviceId] ?: 0L
            val isEcho = remoteTs != 0L && (remoteTs - localTs) in -1500..1500
            if (isEcho) {
                println("MainActivity: Ignoring echo for $deviceId (remoteTs=$remoteTs, localTs=$localTs)")
                return@listenForDeviceStates
            }
            runOnUiThread {
                // Update local DB without pushing back to Firebase to avoid loops
                deviceViewModel.updateDeviceState(deviceId, isOn, pushToFirebase = false)
            }
        }
    }

    private fun updateSensorUI(sensorData: SensorData) {
        // Update UI with real sensor data from Firebase
        tvTemperature.text = "${sensorData.temperature.toInt()}\u00B0C"
        tvHumidity.text = "${sensorData.humidity.toInt()}%"
        tvMotionStatus.text = if (sensorData.motionDetected) "Detected" else "Clear"

        // Log for debugging
        println("UI Updated with Firebase sensor data: Temp=${sensorData.temperature}\u00B0C, Humidity=${sensorData.humidity}%, Motion=${sensorData.motionDetected}")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up Firebase listeners
        firebaseService.stopListeningForSensorData()
        firebaseService.stopListeningForDeviceStates()
    }
}
