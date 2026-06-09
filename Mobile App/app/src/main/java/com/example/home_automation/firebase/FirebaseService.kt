package com.example.home_automation.firebase

import com.google.firebase.database.*
import com.example.home_automation.database.SensorData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseService private constructor() {

    private val database = FirebaseDatabase.getInstance()
    private val devicesRef = database.getReference("devices")
    private val sensorsRef = database.getReference("sensors")

    // Listeners for sensor data updates
    private var sensorDataListener: ((SensorData) -> Unit)? = null
    private var valueEventListener: ValueEventListener? = null
    private var deviceStateListener: ValueEventListener? = null


    companion object {
        @Volatile
        private var INSTANCE: FirebaseService? = null

        fun getInstance(): FirebaseService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirebaseService().also { INSTANCE = it }
            }
        }
    }

    // Initialize Firebase Database URL (call this in Application class)
    fun initialize(databaseUrl: String? = null) {
        try {
            if (databaseUrl != null) {
                // Use custom database URL if provided
                FirebaseDatabase.getInstance(databaseUrl)
            }
            // Enable offline persistence (must be called before any other database usage)
            // Note: calling this after initialization will throw; we catch to avoid crash.
            database.setPersistenceEnabled(true)
        } catch (e: Exception) {
            println("Firebase: Database already initialized or error: ${e.message}")
        }
    }


    // Update device state in Firebase
    fun updateDeviceState(deviceId: String, isOn: Boolean) {
        val deviceData = mapOf(
            "status" to if (isOn) "on" else "off",
            "timestamp" to System.currentTimeMillis()
        )

        devicesRef.child(deviceId).setValue(deviceData)
            .addOnSuccessListener {
                println("Firebase: Device $deviceId updated to ${if (isOn) "on" else "off"}")
            }
            .addOnFailureListener { exception ->
                println("Firebase Error: Failed to update $deviceId - ${exception.message}")
            }
    }

    // Update sensor data to Firebase (for testing purposes)
    fun updateSensorData(temperature: Int, humidity: Int, motionDetected: Boolean) {
        val sensorData = mapOf(
            "temperature" to temperature,
            "humidity" to humidity,
            "motion" to motionDetected,
            "timestamp" to System.currentTimeMillis()
        )

        sensorsRef.setValue(sensorData)
            .addOnSuccessListener {
                println("Firebase: Sensor data updated - Temp: ${temperature}°C, Humidity: ${humidity}%, Motion: $motionDetected")
            }
            .addOnFailureListener { exception ->
                println("Firebase Error: Failed to update sensor data - ${exception.message}")
            }
    }

    // Listen for sensor data changes from Firebase
    fun listenForSensorData(onDataReceived: (SensorData) -> Unit) {
        sensorDataListener = onDataReceived

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    // Handle different data types that ESP might send
                    val temperature = when (val tempValue = snapshot.child("temperature").value) {
                        is Number -> tempValue.toFloat()
                        is String -> tempValue.toFloatOrNull() ?: 0f
                        else -> 0f
                    }

                    val humidity = when (val humValue = snapshot.child("humidity").value) {
                        is Number -> humValue.toFloat()
                        is String -> humValue.toFloatOrNull() ?: 0f
                        else -> 0f
                    }

                    val motion = when (val motionValue = snapshot.child("motion").value) {
                        is Boolean -> motionValue
                        is String -> motionValue.toBoolean()
                        is Number -> motionValue.toInt() != 0
                        else -> false
                    }

                    val timestamp = snapshot.child("timestamp").getValue(Long::class.java)
                        ?: System.currentTimeMillis()

                    val sensorData = SensorData(
                        id = 0, // Room database will auto-generate if you persist it
                        temperature = temperature,
                        humidity = humidity,
                        motionDetected = motion,
                        timestamp = timestamp
                    )

                    // Deliver to callback
                    sensorDataListener?.invoke(sensorData)
                } catch (e: Exception) {
                    println("Firebase Error: Failed to parse sensor data - ${e.message}")
                    e.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase Error: Sensor data listener cancelled - ${error.message}")
            }
        }

        sensorsRef.addValueEventListener(valueEventListener!!)
    }

    // Stop listening for sensor data
    fun stopListeningForSensorData() {
        valueEventListener?.let { listener ->
            sensorsRef.removeEventListener(listener)
        }
        sensorDataListener = null
        valueEventListener = null
    }

    // Listen for device state changes from Firebase
    fun listenForDeviceStates(onDeviceStateChanged: (String, Boolean, Long) -> Unit) {
        deviceStateListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    for (deviceSnapshot in snapshot.children) {
                        val deviceId = deviceSnapshot.key ?: continue
                        val status = deviceSnapshot.child("status").getValue(String::class.java) ?: "off"
                        val isOn = status.lowercase() == "on"
                        val ts = deviceSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                        onDeviceStateChanged(deviceId, isOn, ts)
                    }
                } catch (e: Exception) {
                    println("Firebase Error: Failed to parse device states - ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase Error: Device states listener cancelled - ${error.message}")
            }
        }

        devicesRef.addValueEventListener(deviceStateListener!!)
    }

    // Stop listening for device state changes
    fun stopListeningForDeviceStates() {
        deviceStateListener?.let { listener ->
            devicesRef.removeEventListener(listener)
        }
        deviceStateListener = null
    }

    // Clean up all listeners
    fun cleanup() {
        stopListeningForSensorData()
        stopListeningForDeviceStates()
    }
}
