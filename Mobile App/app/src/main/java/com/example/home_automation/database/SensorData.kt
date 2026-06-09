package com.example.home_automation.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensor_data")
data class SensorData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val temperature: Float,
    val humidity: Float,
    val motionDetected: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
