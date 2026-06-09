package com.example.home_automation

import android.app.Application
import com.example.home_automation.database.HomeAutomationDatabase
import com.example.home_automation.repository.DeviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class HomeAutomationApplication : Application() {
    // Using SupervisorJob to prevent child coroutine failures from cancelling the parent
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Lazy initialization of database
    val database by lazy {
        HomeAutomationDatabase.getDatabase(this, applicationScope)
    }

    // Lazy initialization of repository
    val repository by lazy {
        DeviceRepository(database.deviceDao())
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        repository.initializeFirebase()
    }
}

