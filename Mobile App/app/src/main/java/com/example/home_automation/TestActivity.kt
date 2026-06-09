package com.example.home_automation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.home_automation.database.Device
import com.example.home_automation.viewmodel.DeviceViewModel

class TestActivity : AppCompatActivity() {

    private lateinit var deviceViewModel: DeviceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Simple test layout - just create a button programmatically
        val button = Button(this).apply {
            text = "Test Database Operations"
            setOnClickListener { testDatabaseOperations() }
        }
        setContentView(button)

        deviceViewModel = ViewModelProvider(this)[DeviceViewModel::class.java]

        // Test database operations immediately
        testDatabaseOperations()
    }

    private fun testDatabaseOperations() {
        println("=== TESTING DATABASE OPERATIONS ===")

        // Test 1: Insert a test device
        val testDevice = Device("test1", "Test Device", "test", false)
        deviceViewModel.insertDevice(testDevice)
        println("Test 1: Inserted test device")

        // Test 2: Update the test device
        Thread.sleep(1000) // Wait a bit for insert to complete
        deviceViewModel.updateDeviceState("test1", true)
        println("Test 2: Updated test device to ON")

        // Test 3: Observe changes
        deviceViewModel.allDevices.observe(this) { devices ->
            println("Test 3: Database contains ${devices.size} devices:")
            devices.forEach { device ->
                println("  - ${device.id}: ${device.name} (${if (device.isOn) "ON" else "OFF"})")
            }
        }
    }
}
