package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_intake_questionnaire")
data class FoodIntakeQuestionnaire(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val patientId: String,
    val selectedFoodCategories: String, // comma-separated
    val persona: String,
    val biggestMealTime: String,
    val sleepTime: String,
    val wakeUpTime: String,
    val date: Long
) 