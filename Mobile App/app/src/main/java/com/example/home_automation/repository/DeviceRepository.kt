package com.example.home_automation.repository

import androidx.lifecycle.LiveData
import com.example.home_automation.database.Device
import com.example.home_automation.database.DeviceDao
import com.example.home_automation.firebase.FirebaseService

class DeviceRepository(private val deviceDao: DeviceDao) {

    val allDevices: LiveData<List<Device>> = deviceDao.getAllDevices()
    private val firebaseService = FirebaseService.getInstance()

    suspend fun insert(device: Device) {
        println("Repository: Inserting device ${device.id} with state ${if (device.isOn) "ON" else "OFF"}")
        deviceDao.insertDevice(device)
        // Sync to Firebase
        firebaseService.updateDeviceState(device.id, device.isOn)
    }

    suspend fun update(device: Device) {
        println("Repository: Updating device ${device.id}")
        deviceDao.updateDevice(device)
        // Sync to Firebase
        firebaseService.updateDeviceState(device.id, device.isOn)
    }

    suspend fun updateDeviceState(deviceId: String, isOn: Boolean, pushToFirebase: Boolean = true) {
        println("Repository: Updating device state - $deviceId to ${if (isOn) "ON" else "OFF"}")
        deviceDao.updateDeviceState(deviceId, isOn, System.currentTimeMillis())
        if (pushToFirebase) {
            // Sync state to Firebase
            firebaseService.updateDeviceState(deviceId, isOn)
            println("Repository: Device state update completed for $deviceId and synced to Firebase")
        } else {
            println("Repository: Device state update completed for $deviceId (local only, no Firebase sync)")
        }
    }

    suspend fun getDevice(deviceId: String): Device? {
        return deviceDao.getDevice(deviceId)
    }

    fun getDevicesByType(type: String): LiveData<List<Device>> {
        return deviceDao.getDevicesByType(type)
    }

    // Initialize Firebase service
    fun initializeFirebase() {
        firebaseService.initialize()
        // Removed test sensor data write to avoid mixing with real sensor updates from ESP
        // If you need to seed once for UI, do it manually in a debug-only path.
    }
}
