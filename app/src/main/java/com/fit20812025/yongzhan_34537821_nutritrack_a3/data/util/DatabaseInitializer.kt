package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.util

import android.content.Context
import android.util.Log
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local.NutriTrackDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {
    private const val TAG = "DatabaseInitializer"
    private const val PREFS_NAME = "nutritrack_prefs"
    private const val KEY_DB_INITIALIZED = "is_database_initialized"

    suspend fun initializeDatabaseIfNeeded(context: Context, db: NutriTrackDatabase) {
        withContext(Dispatchers.IO) {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val isInitialized = prefs.getBoolean(KEY_DB_INITIALIZED, false)

                Log.d(TAG, "Checking if database needs initialization")
                val patientDao = db.patientDao()
                val patientCount = patientDao.getPatientCount()
                Log.d(TAG, "Current patient count in database: $patientCount")

                // Only import CSV data if the database is completely empty
                if (patientCount == 0) {
                    Log.d(TAG, "Database is empty, importing initial data")
                    val patients = CsvImporter.parsePatientsCSV(context)
                    Log.d(TAG, "Parsed ${patients.size} patients from CSV")
                    
                    if (patients.isNotEmpty()) {
                        patientDao.insertAll(patients)
                        Log.d(TAG, "Successfully inserted all patients into database")
                        prefs.edit().putBoolean(KEY_DB_INITIALIZED, true).apply()
                        Log.d(TAG, "Marked database as initialized in SharedPreferences")
                    } else {
                        Log.e(TAG, "No patients were parsed from CSV")
                        prefs.edit().putBoolean(KEY_DB_INITIALIZED, false).apply()
                    }
                } else {
                    Log.d(TAG, "Database already contains $patientCount patients, skipping CSV import")
                    if (!isInitialized) {
                        prefs.edit().putBoolean(KEY_DB_INITIALIZED, true).apply()
                        Log.d(TAG, "Marked database as initialized in SharedPreferences")
                    } else{}
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing database", e)
                // Only clear initialization flag if we have no data
                val patientCount = db.patientDao().getPatientCount()
                if (patientCount == 0) {
                    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(KEY_DB_INITIALIZED, false)
                        .apply()
                } else{}
            }
        }
    }

    // Add a method to clear the initialization flag
    fun clearInitializationFlag(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DB_INITIALIZED, false)
            .apply()
    }
} 