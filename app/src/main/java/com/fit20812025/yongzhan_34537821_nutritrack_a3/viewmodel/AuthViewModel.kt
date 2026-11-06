package com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository.AuthRepository
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local.NutriTrackDatabase
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.Patient // Import Patient
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository.PatientRepository // Import PatientRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val db = NutriTrackDatabase.getInstance(application)
    private val prefs = application.getSharedPreferences("nutritrack_prefs", Context.MODE_PRIVATE)
    private val authRepository = AuthRepository(db.patientDao(), prefs)
    private val patientRepository = PatientRepository(db.patientDao()) // Instance for patient updates

    private val _loginStatus = MutableLiveData<Boolean?>()
    val loginStatus: LiveData<Boolean?> = _loginStatus

    private val _claimStatus = MutableLiveData<Boolean?>()
    val claimStatus: LiveData<Boolean?> = _claimStatus

    private val _loggedInUserId = MutableLiveData<String?>()
    val loggedInUserId: LiveData<String?> = _loggedInUserId

    private val _currentPatient = MutableLiveData<Patient?>()
    val currentPatient: LiveData<Patient?> = _currentPatient

    init {
        checkSession()
    }

    fun login(userId: String, password: String) {
        viewModelScope.launch {
            val success = authRepository.login(userId, password)
            _loginStatus.value = success
            if (success) {
                _loggedInUserId.value = userId
                loadPatientDetails(userId) // Load patient details on login
            } else {
                _currentPatient.value = null
            }
        }
    }

    fun claimAccount(userId: String, phoneNumber: String, name: String, password: String) {
        viewModelScope.launch {
            val success = authRepository.claimAccount(userId, phoneNumber, name, password)
            _claimStatus.value = success
            if (success) {
                _loggedInUserId.value = userId
                loadPatientDetails(userId) // Load patient details after claim
            } else {
                _currentPatient.value = null
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _loggedInUserId.value = null
            _currentPatient.value = null
            _loginStatus.value = null
            _claimStatus.value = null
        }
    }

    fun checkSession() {
        val userId = authRepository.getLoggedInUserId()
        _loggedInUserId.value = userId
        if (userId != null) {
            loadPatientDetails(userId)
        } else {
            _currentPatient.value = null
        }
    }

    // MODIFIED: Changed visibility from private to public (or internal)
    fun loadPatientDetails(userId: String) {
        viewModelScope.launch {
            _currentPatient.value = patientRepository.getPatientById(userId)
        }
    }

    fun markInitialQuestionnaireCompleted(userId: String) {
        viewModelScope.launch {
            val patient = patientRepository.getPatientById(userId)
            patient?.let {
                if (!it.hasCompletedInitialQuestionnaire) {
                    val updatedPatient = it.copy(hasCompletedInitialQuestionnaire = true)
                    patientRepository.updatePatient(updatedPatient)
                    _currentPatient.value = updatedPatient
                }
            }
        }
    }

    fun saveLastRoute(route: String?) {
        authRepository.saveLastVisitedRoute(route)
    }

    fun getLastRoute(): String? {
        return authRepository.getLastVisitedRoute()
    }

    fun resetAuthStatusFlags() {
        _loginStatus.value = null
        _claimStatus.value = null
    }
}
