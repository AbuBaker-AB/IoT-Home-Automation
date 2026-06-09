package com.example.home_automation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.home_automation.HomeAutomationApplication
import com.example.home_automation.database.Device
import com.example.home_automation.repository.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DeviceRepository
    val allDevices: LiveData<List<Device>>

    init {
        val app = application as HomeAutomationApplication
        repository = app.repository
        allDevices = repository.allDevices
    }

    fun updateDeviceState(deviceId: String, isOn: Boolean, pushToFirebase: Boolean = true) = viewModelScope.launch(Dispatchers.IO) {
        try {
            repository.updateDeviceState(deviceId, isOn, pushToFirebase)
            println("DeviceViewModel: Successfully updated $deviceId to ${if (isOn) "ON" else "OFF"} (pushToFirebase=$pushToFirebase)")
        } catch (e: Exception) {
            println("DeviceViewModel: Error updating $deviceId: ${e.message}")
        }
    }

    fun insertDevice(device: Device) = viewModelScope.launch(Dispatchers.IO) {
        try {
            repository.insert(device)
            println("DeviceViewModel: Successfully inserted device ${device.id}")
        } catch (e: Exception) {
            println("DeviceViewModel: Error inserting device ${device.id}: ${e.message}")
        }
    }

    fun getDevice(deviceId: String, callback: (Device?) -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        val device = repository.getDevice(deviceId)
        // Switch back to Main thread for the callback
        launch(Dispatchers.Main) {
            callback(device)
        }
    }

    fun getFans(): LiveData<List<Device>> {
        return repository.getDevicesByType("fan")
    }

    fun getLights(): LiveData<List<Device>> {
        return repository.getDevicesByType("light")
    }
}
