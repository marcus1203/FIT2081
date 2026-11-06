package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository

import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local.FoodIntakeQuestionnaireDao
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.FoodIntakeQuestionnaire
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FoodIntakeQuestionnaireRepository(private val questionnaireDao: FoodIntakeQuestionnaireDao) {
    suspend fun insert(questionnaire: FoodIntakeQuestionnaire) = withContext(Dispatchers.IO) {
        questionnaireDao.insert(questionnaire)
    }

    suspend fun saveQuestionnaire(questionnaire: FoodIntakeQuestionnaire) = withContext(Dispatchers.IO) {
        val existingQuestionnaire = questionnaireDao.getQuestionnaireByPatientId(questionnaire.patientId)
        if (existingQuestionnaire != null) {
            // Update existing questionnaire
            questionnaireDao.updateQuestionnaire(
                patientId = questionnaire.patientId,
                categories = questionnaire.selectedFoodCategories,
                persona = questionnaire.persona,
                biggestMealTime = questionnaire.biggestMealTime,
                sleepTime = questionnaire.sleepTime,
                wakeUpTime = questionnaire.wakeUpTime,
                date = questionnaire.date
            )
        } else {
            // Insert new questionnaire
            questionnaireDao.insert(questionnaire)
        }
    }

    suspend fun getQuestionnaireByPatientId(patientId: String): FoodIntakeQuestionnaire? = withContext(Dispatchers.IO) {
        questionnaireDao.getQuestionnaireByPatientId(patientId)
    }

    suspend fun getLatestByPatientId(patientId: String): FoodIntakeQuestionnaire? = questionnaireDao.getLatestByPatientId(patientId)
} 