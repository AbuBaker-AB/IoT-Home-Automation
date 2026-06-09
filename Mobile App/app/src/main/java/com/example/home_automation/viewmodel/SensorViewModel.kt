package com.example.home_automation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.home_automation.database.HomeAutomationDatabase
import com.example.home_automation.database.SensorData
import com.example.home_automation.database.SensorDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorDao: SensorDao
    val allSensorData: LiveData<List<SensorData>>
    val latestSensorData: LiveData<SensorData?>

    init {
        val database = HomeAutomationDatabase.getDatabase(application, CoroutineScope(SupervisorJob()))
        sensorDao = database.sensorDao()
        allSensorData = sensorDao.getAllSensorData()
        latestSensorData = sensorDao.getLatestSensorData()
    }

    fun insertSensorData(sensorData: SensorData) {
        viewModelScope.launch(Dispatchers.IO) {
            sensorDao.insertSensorData(sensorData)
            // Keep only latest 100 records to prevent database from growing too large
            sensorDao.keepLatest100Records()
        }
    }

    fun getSensorDataByTimeRange(startTime: Long, endTime: Long): LiveData<List<SensorData>> {
        return sensorDao.getSensorDataByTimeRange(startTime, endTime)
    }

    fun deleteAllSensorData() {
        viewModelScope.launch(Dispatchers.IO) {
            sensorDao.deleteAllSensorData()
        }
    }
}
