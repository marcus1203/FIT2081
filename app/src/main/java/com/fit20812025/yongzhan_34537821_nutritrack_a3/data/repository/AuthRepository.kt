package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository

import android.content.SharedPreferences
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local.PatientDao

class AuthRepository(private val patientDao: PatientDao, private val prefs: SharedPreferences) {
    companion object {
        private const val KEY_LOGGED_IN_USER_ID = "logged_in_user_id"
        private const val KEY_LAST_VISITED_ROUTE = "last_visited_route" // New key
    }

    suspend fun login(userId: String, password: String): Boolean {
        val patient = patientDao.getPatientById(userId)
        return if (patient != null && patient.password == password) {
            prefs.edit().putString(KEY_LOGGED_IN_USER_ID, userId).apply()
            // Do not clear last visited route on login
            true
        } else {
            false
        }
    }

    suspend fun claimAccount(userId: String, phoneNumber: String, name: String, password: String): Boolean {
        val patient = patientDao.getPatientById(userId)
        // In a real scenario, ensure patient.password is null or handle re-claiming logic
        return if (patient != null && patient.phoneNumber == phoneNumber /* && patient.password == null */) {
            val updatedPatient = patient.copy(name = name, password = password)
            // Assuming insertAll handles updates if @Insert(onConflict = OnConflictStrategy.REPLACE)
            // Or you might need an explicit update method in PatientDao
            patientDao.insertAll(listOf(updatedPatient))
            prefs.edit().putString(KEY_LOGGED_IN_USER_ID, userId).apply()
            clearLastVisitedRoute() // Fresh session after claim, start at Home
            true
        } else {
            false
        }
    }

    fun logout() {
        prefs.edit()
            .remove(KEY_LOGGED_IN_USER_ID)
            .remove(KEY_LAST_VISITED_ROUTE) // Clear last route on logout
            .apply()
    }

    fun getLoggedInUserId(): String? = prefs.getString(KEY_LOGGED_IN_USER_ID, null)

    // --- Methods for Last Visited Route ---
    fun saveLastVisitedRoute(route: String?) { // Renamed to match ViewModel call
        prefs.edit().putString(KEY_LAST_VISITED_ROUTE, route).apply()
    }

    fun getLastVisitedRoute(): String? { // Renamed to match ViewModel call
        return prefs.getString(KEY_LAST_VISITED_ROUTE, null)
    }

    private fun clearLastVisitedRoute() {
        prefs.edit().remove(KEY_LAST_VISITED_ROUTE).apply()
    }
}
