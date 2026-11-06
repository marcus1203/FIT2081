package com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit20812025.yongzhan_34537821_nutritrack_a3.BuildConfig // Your app's BuildConfig
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local.NutriTrackDatabase
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.Patient
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local.PatientDao
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.common.GenAiUiState // Reusing your UiState
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AverageScores(
    val averageMaleScore: Double?,
    val averageFemaleScore: Double?
)

class ClinicianViewModel(application: Application) : AndroidViewModel(application) {
    private val patientDao: PatientDao = NutriTrackDatabase.getInstance(application).patientDao()

    private val _averageScores = MutableLiveData<AverageScores?>()
    val averageScores: LiveData<AverageScores?> = _averageScores

    private val _dataPatternsUiState = MutableStateFlow<GenAiUiState>(GenAiUiState.Initial)
    val dataPatternsUiState: StateFlow<GenAiUiState> = _dataPatternsUiState.asStateFlow()

    private var generativeModel: GenerativeModel? = null // Nullable to handle API key issues

    init {
        val apiKey = BuildConfig.apiKey // Ensure this resolves to your app's BuildConfig
        if (apiKey == "MISSING_API_KEY_IN_LOCAL_PROPERTIES" || apiKey.isBlank() || apiKey == "YOUR_DEFAULT_API_KEY_IF_NOT_FOUND") {
            Log.e("ClinicianViewModel", "API Key for Gemini is missing or invalid. GenAI features will be disabled.")
            _dataPatternsUiState.value = GenAiUiState.Error("GenAI features disabled due to missing API key.")
        } else {
            generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash", // Or your preferred model
                apiKey = apiKey,
                generationConfig = generationConfig {
                    // temperature = 0.7f // Example
                    // maxOutputTokens = 600 // Example
                }
            )
        }
        loadAverageScores() // Load scores when ViewModel is created
    }

    fun loadAverageScores() {
        viewModelScope.launch {
            _averageScores.postValue(null) // Indicate loading
            try {
                val allPatients = withContext(Dispatchers.IO) {
                    patientDao.getAllPatients()
                }

                if (allPatients.isEmpty()) {
                    _averageScores.postValue(AverageScores(0.0, 0.0)) // No patients, averages are 0
                    return@launch
                }

                val maleScores = allPatients
                    .filter { it.sex == "Male" && it.heifaTotalScoreMale != null }
                    .mapNotNull { it.heifaTotalScoreMale }

                val femaleScores = allPatients
                    .filter { it.sex == "Female" && it.heifaTotalScoreFemale != null }
                    .mapNotNull { it.heifaTotalScoreFemale }

                val avgMale = if (maleScores.isNotEmpty()) maleScores.average() else 0.0
                val avgFemale = if (femaleScores.isNotEmpty()) femaleScores.average() else 0.0

                _averageScores.postValue(AverageScores(avgMale, avgFemale))
            } catch (e: Exception) {
                Log.e("ClinicianViewModel", "Error loading average scores", e)
                _averageScores.postValue(AverageScores(null, null)) // Indicate error state with nulls
            }
        }
    }

    fun findDataPatterns() {
        if (generativeModel == null) {
            _dataPatternsUiState.value = GenAiUiState.Error("GenAI service is not available (API Key issue).")
            Log.w("ClinicianViewModel", "findDataPatterns called but generativeModel is null.")
            return
        }

        _dataPatternsUiState.value = GenAiUiState.Loading
        viewModelScope.launch {
            try {
                val allPatients = withContext(Dispatchers.IO) {
                    patientDao.getAllPatients()
                }

                if (allPatients.isEmpty()) {
                    _dataPatternsUiState.value = GenAiUiState.Error("No patient data available to analyze.")
                    return@launch
                }

                // Create a concise summary for the prompt
                val patientDataSummary = patientsToConciseTextSummary(allPatients.take(30)) // Limit for prompt size

                val prompt = """
                    Analyze the following anonymous patient nutritional data summary and identify exactly 3 distinct and interesting patterns or correlations.
                    Focus on relationships between different HEIFA scores (e.g., fruit score vs vegetable score, water intake vs total score, specific food group scores vs overall score) or differences based on sex.
                    Provide each pattern as a concise statement. Do not number them or use bullet points. Each statement should be a complete sentence.
                    Separate the 3 statements with a double newline character ('\n\n').

                    Data Summary:
                    $patientDataSummary
                """.trimIndent()

                Log.d("ClinicianViewModel", "GenAI Prompt for patterns (length: ${prompt.length})")
                // Log.d("ClinicianViewModel", "Prompt content: $prompt") // Uncomment for full prompt if needed

                val response = withContext(Dispatchers.IO) {
                    generativeModel!!.generateContent(prompt) // Non-null assertion due to check above
                }

                response.text?.let {
                    val patterns = it.trim().split("\n\n").map { pattern -> pattern.trim() }.filter { pattern -> pattern.isNotBlank() }.take(3)
                    if (patterns.isNotEmpty()) {
                        _dataPatternsUiState.value = GenAiUiState.Success(patterns.joinToString("\n\n"))
                    } else {
                        Log.w("ClinicianViewModel", "GenAI response format unexpected or too few patterns: $it")
                        _dataPatternsUiState.value = GenAiUiState.Error("AI could not identify clear patterns from the data provided. Response: $it")
                    }
                } ?: run {
                    _dataPatternsUiState.value = GenAiUiState.Error("Received no text from AI for patterns.")
                }

            } catch (e: Exception) {
                Log.e("ClinicianViewModel", "Error finding data patterns with GenAI", e)
                _dataPatternsUiState.value = GenAiUiState.Error("Failed to find data patterns: ${e.message}")
            }
        }
    }

    // Helper to create a concise text summary
    private fun patientsToConciseTextSummary(patients: List<Patient>): String {
        if (patients.isEmpty()) return "No patient data."
        val summary = StringBuilder("Patient Data (${patients.size} records):\n")
        patients.forEachIndexed { index, p ->
            summary.append("P${index + 1}: Sex=${p.sex}, TotalM=${p.heifaTotalScoreMale ?: "-"}, TotalF=${p.heifaTotalScoreFemale ?: "-"}, FruitM=${p.fruitHeifaScoreMale ?: "-"}, FruitF=${p.fruitHeifaScoreFemale ?: "-"}, VegM=${p.vegetablesHeifaScoreMale ?: "-"}, VegF=${p.vegetablesHeifaScoreFemale ?: "-"}, WaterM=${p.waterHeifaScoreMale ?: "-"}, WaterF=${p.waterHeifaScoreFemale ?: "-"}\n")
        }
        return summary.toString()
    }

    fun resetDataPatternsState() {
        _dataPatternsUiState.value = GenAiUiState.Initial
    }
}
