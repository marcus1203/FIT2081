package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository

import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local.FoodIntakeDao
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.FoodIntake

class FoodIntakeRepository(private val foodIntakeDao: FoodIntakeDao) {
    suspend fun insertFoodIntake(foodIntake: FoodIntake) {
        foodIntakeDao.insert(foodIntake)
    }

    suspend fun getFoodIntakesByPatientId(patientId: String): List<FoodIntake> {
        return foodIntakeDao.getFoodIntakesByPatientId(patientId)
    }
} 