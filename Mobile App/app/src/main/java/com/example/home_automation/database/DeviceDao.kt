package com.example.home_automation.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices ORDER BY name ASC")
    fun getAllDevices(): LiveData<List<Device>>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    suspend fun getDevice(deviceId: String): Device?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: Device)

    @Update
    suspend fun updateDevice(device: Device)

    @Query("UPDATE devices SET isOn = :isOn, timestamp = :timestamp WHERE id = :deviceId")
    suspend fun updateDeviceState(deviceId: String, isOn: Boolean, timestamp: Long)

    @Delete
    suspend fun deleteDevice(device: Device)

    @Query("DELETE FROM devices")
    suspend fun deleteAllDevices()

    @Query("SELECT * FROM devices WHERE type = :type")
    fun getDevicesByType(type: String): LiveData<List<Device>>
}
