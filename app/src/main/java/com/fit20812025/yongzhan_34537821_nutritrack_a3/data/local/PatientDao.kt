package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.Patient

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patients: List<Patient>)

    @Query("SELECT * FROM patients WHERE userId = :userId")
    suspend fun getPatientById(userId: String): Patient?

    @Query("SELECT userId FROM patients ORDER BY CAST(userId AS INTEGER)")
    suspend fun getAllUserIds(): List<String>

    @Query("SELECT * FROM patients WHERE userId = :userId")
    fun getPatientByUserIdLD(userId: String): LiveData<Patient> // Renamed to avoid conflict if any

    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int

    @Query("SELECT * FROM patients")
    suspend fun getAllPatients(): List<Patient>

    @Update
    suspend fun updatePatient(patient: Patient) // New update method
}
