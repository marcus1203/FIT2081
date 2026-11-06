package com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.FoodIntake
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository.FoodIntakeRepository
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local.NutriTrackDatabase
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.FoodIntakeQuestionnaire
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository.FoodIntakeQuestionnaireRepository
import kotlinx.coroutines.launch

class FoodIntakeViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "FoodIntakeViewModel"
    private val db = NutriTrackDatabase.getInstance(application)
    private val repository = FoodIntakeRepository(db.foodIntakeDao())
    private val questionnaireRepository = FoodIntakeQuestionnaireRepository(db.foodIntakeQuestionnaireDao())

    private val _foodIntake = MutableLiveData<List<FoodIntake>>()
    val foodIntake: LiveData<List<FoodIntake>> = _foodIntake

    private val _questionnaire = MutableLiveData<FoodIntakeQuestionnaire>()
    val questionnaire: LiveData<FoodIntakeQuestionnaire> = _questionnaire

    fun getFoodIntakesByPatientId(patientId: String) {
        viewModelScope.launch {
            _foodIntake.value = repository.getFoodIntakesByPatientId(patientId)
        }
    }

    fun insertFoodIntake(foodIntake: FoodIntake) {
        viewModelScope.launch {
            repository.insertFoodIntake(foodIntake)
        }
    }

    fun insertFoodIntakes(
        patientId: String,
        selectedFoodCategories: List<String>,
        quantity: Double = 1.0
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            selectedFoodCategories.forEach { foodName ->
                val foodIntake = FoodIntake(
                    patientId = patientId,
                    foodName = foodName,
                    quantity = quantity,
                    date = now
                )
                repository.insertFoodIntake(foodIntake)
            }
        }
    }

    fun saveQuestionnaire(
        patientId: String,
        selectedFoodCategories: List<String>,
        persona: String,
        biggestMealTime: String,
        sleepTime: String,
        wakeUpTime: String
    ) {
        Log.d(TAG, "Saving questionnaire for patient: $patientId")
        Log.d(TAG, "Selected food categories: ${selectedFoodCategories.joinToString()}")
        Log.d(TAG, "Persona: $persona")
        Log.d(TAG, "Biggest meal time: $biggestMealTime")
        Log.d(TAG, "Sleep time: $sleepTime")
        Log.d(TAG, "Wake up time: $wakeUpTime")

        viewModelScope.launch {
            try {
                val questionnaire = FoodIntakeQuestionnaire(
                    patientId = patientId,
                    selectedFoodCategories = selectedFoodCategories.joinToString(","),
                    persona = persona,
                    biggestMealTime = biggestMealTime,
                    sleepTime = sleepTime,
                    wakeUpTime = wakeUpTime,
                    date = System.currentTimeMillis()
                )
                questionnaireRepository.saveQuestionnaire(questionnaire)
                Log.d(TAG, "Successfully saved questionnaire to database")
                
                // Verify the save by immediately loading it back
                val savedQuestionnaire = questionnaireRepository.getQuestionnaireByPatientId(patientId)
                Log.d(TAG, "Verification - Loaded back questionnaire: $savedQuestionnaire")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving questionnaire", e)
            }
        }
    }

    fun loadQuestionnaire(patientId: String) {
        Log.d(TAG, "Attempting to load questionnaire for patient: $patientId")
        viewModelScope.launch {
            try {
                val questionnaire = questionnaireRepository.getQuestionnaireByPatientId(patientId)
                Log.d(TAG, "Retrieved questionnaire from database: $questionnaire")
                _questionnaire.value = questionnaire
                if (questionnaire == null) {
                    Log.d(TAG, "No questionnaire found for patient: $patientId")
                } else {
                    Log.d(TAG, "Successfully loaded questionnaire with data:")
                    Log.d(TAG, "- Selected categories: ${questionnaire.selectedFoodCategories}")
                    Log.d(TAG, "- Persona: ${questionnaire.persona}")
                    Log.d(TAG, "- Biggest meal time: ${questionnaire.biggestMealTime}")
                    Log.d(TAG, "- Sleep time: ${questionnaire.sleepTime}")
                    Log.d(TAG, "- Wake up time: ${questionnaire.wakeUpTime}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading questionnaire", e)
            }
        }
    }
} 