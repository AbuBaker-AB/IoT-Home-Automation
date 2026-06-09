package com.example.home_automation.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String, // "fan" or "light"
    val isOn: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
