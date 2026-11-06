package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository

import androidx.lifecycle.LiveData // Required for LiveData return type
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local.PatientDao
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.Patient

class PatientRepository(private val patientDao: PatientDao) {
    suspend fun insertAll(patients: List<Patient>) = patientDao.insertAll(patients)
    suspend fun getPatientById(userId: String): Patient? = patientDao.getPatientById(userId)
    suspend fun getAllUserIds(): List<String> = patientDao.getAllUserIds()
    suspend fun updatePatient(patient: Patient) = patientDao.updatePatient(patient) // New method

    // If PatientViewModel needs LiveData for a single patient
    fun getPatientByUserIdLD(userId: String): LiveData<Patient> = patientDao.getPatientByUserIdLD(userId)
}
