// In com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.NutriCoachTipViewModel.kt
package com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.NutriCoachTip
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository.NutriCoachTipRepository
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local.NutriTrackDatabase
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.common.GenAiUiState // Your UiState
import com.fit20812025.yongzhan_34537821_nutritrack_a3.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NutriCoachTipViewModel(application: Application) : AndroidViewModel(application) {
    private val db = NutriTrackDatabase.getInstance(application)
    private val repository = NutriCoachTipRepository(db.nutriCoachTipDao())

    // For displaying saved tips
    private val _savedTips = MutableLiveData<List<NutriCoachTip>>()
    val savedTips: LiveData<List<NutriCoachTip>> = _savedTips

    // For GenAI state
    private val _genAiUiState = MutableStateFlow<GenAiUiState>(GenAiUiState.Initial)
    val genAiUiState: StateFlow<GenAiUiState> = _genAiUiState.asStateFlow()

    private var generativeModel: GenerativeModel


    init {
        val apiKey = BuildConfig.apiKey
        if (apiKey == "MISSING_API_KEY" || apiKey.isBlank()) {
            Log.e("NutriCoachTipVM", "API Key not found or is the default. Please check local.properties and BuildConfig setup.")
        }
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
    }


    fun loadSavedTips(patientId: String) {
        viewModelScope.launch {
            try {
                _savedTips.value = repository.getTipsForPatient(patientId)
            } catch (e: Exception) {
                Log.e("NutriCoachTipVM", "Error loading saved tips: ${e.message}")
                _savedTips.value = emptyList() // Set to empty on error
            }
        }
    }

    fun generateMotivationalTip(patientId: String ) {
        _genAiUiState.value = GenAiUiState.Loading
        viewModelScope.launch {
            try {
                // Basic prompt as per spec
                // For HD++: "improve the specificity of GenAl response by sending additional information to the GenAl model such as all the values of the patient and their food intake result."
                val prompt = "Generate a short, positive, and encouraging message (around 2-3 sentences) to help someone improve their fruit intake. Make it sound friendly and supportive."

                val response = withContext(Dispatchers.IO) { // Offload network call
                    generativeModel.generateContent(prompt)
                }

                response.text?.let { tipText ->
                    _genAiUiState.value = GenAiUiState.Success(tipText)
                    // Save the generated tip to the database
                    saveTipToDatabase(patientId, tipText)
                } ?: run {
                    _genAiUiState.value = GenAiUiState.Error("Received no text from AI.")
                }
            } catch (e: Exception) {
                Log.e("NutriCoachTipVM", "Error generating tip: ${e.message}", e)
                _genAiUiState.value = GenAiUiState.Error("Failed to generate tip: ${e.message}")
            }
        }
    }

    private fun saveTipToDatabase(patientId: String, tipText: String) {
        viewModelScope.launch {
            try {
                val newTip = NutriCoachTip(patientId = patientId, tip = tipText)
                repository.insert(newTip)
                // Optionally, reload saved tips to update the "Show All Tips" modal live
                // loadSavedTips(patientId) // Or manage this differently to avoid immediate flicker
                Log.d("NutriCoachTipVM", "Tip saved: $tipText for patient $patientId")
            } catch (e: Exception) {
                Log.e("NutriCoachTipVM", "Error saving tip to DB: ${e.message}")
            }
        }
    }

    // Call this to reset the UI state if needed, e.g., when the screen is left
    fun resetGenAiState() {
        _genAiUiState.value = GenAiUiState.Initial
    }
}