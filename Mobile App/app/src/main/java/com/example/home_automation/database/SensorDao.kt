package com.example.home_automation.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SensorDao {

    @Query("SELECT * FROM sensor_data ORDER BY timestamp DESC")
    fun getAllSensorData(): LiveData<List<SensorData>>

    @Query("SELECT * FROM sensor_data ORDER BY timestamp DESC LIMIT 1")
    fun getLatestSensorData(): LiveData<SensorData?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSensorData(sensorData: SensorData)

    @Query("DELETE FROM sensor_data WHERE id NOT IN (SELECT id FROM sensor_data ORDER BY timestamp DESC LIMIT 100)")
    suspend fun keepLatest100Records()

    @Query("DELETE FROM sensor_data")
    suspend fun deleteAllSensorData()

    @Query("SELECT * FROM sensor_data WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    fun getSensorDataByTimeRange(startTime: Long, endTime: Long): LiveData<List<SensorData>>
}
