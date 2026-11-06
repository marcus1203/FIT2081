package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.FoodIntakeQuestionnaire

@Dao
interface FoodIntakeQuestionnaireDao {
    @Insert
    suspend fun insert(questionnaire: FoodIntakeQuestionnaire)

    @Query("UPDATE food_intake_questionnaire SET selectedFoodCategories = :categories, persona = :persona, biggestMealTime = :biggestMealTime, sleepTime = :sleepTime, wakeUpTime = :wakeUpTime, date = :date WHERE patientId = :patientId")
    suspend fun updateQuestionnaire(patientId: String, categories: String, persona: String, biggestMealTime: String, sleepTime: String, wakeUpTime: String, date: Long)

    @Query("SELECT * FROM food_intake_questionnaire WHERE patientId = :patientId ORDER BY date DESC LIMIT 1")
    suspend fun getQuestionnaireByPatientId(patientId: String): FoodIntakeQuestionnaire?

    @Query("SELECT * FROM food_intake_questionnaire WHERE patientId = :patientId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestByPatientId(patientId: String): FoodIntakeQuestionnaire?
} 