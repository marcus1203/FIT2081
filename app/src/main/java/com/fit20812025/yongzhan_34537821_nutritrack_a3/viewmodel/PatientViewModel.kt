package com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local.NutriTrackDatabase
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.Patient
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository.PatientRepository
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.util.DatabaseInitializer
import kotlinx.coroutines.launch

class PatientViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "PatientViewModel"
    private val db = NutriTrackDatabase.getInstance(application)
    private val repository = PatientRepository(db.patientDao())

    private val _patient = MutableLiveData<Patient?>()
    val patient: LiveData<Patient?> = _patient

    private val _userIds = MutableLiveData<List<String>>()
    val userIds: LiveData<List<String>> = _userIds

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                DatabaseInitializer.initializeDatabaseIfNeeded(application, db)
                loadUserIds()
            } catch (e: Exception) {
                Log.e(TAG, "Error during initialization", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPatient(userId: String) {
        viewModelScope.launch {
            _patient.value = repository.getPatientById(userId)
        }
    }

    fun loadUserIds() {
        Log.d(TAG, "Loading user IDs from database")
        viewModelScope.launch {
            try {
                val ids = db.patientDao().getAllUserIds()
                Log.d(TAG, "Loaded ${ids.size} user IDs from database")
                _userIds.value = ids
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user IDs", e)
            }
        }
    }

    fun getPatientByUserId(userId: String): LiveData<Patient> {
        return db.patientDao().getPatientByUserIdLD(userId)
    }
}
