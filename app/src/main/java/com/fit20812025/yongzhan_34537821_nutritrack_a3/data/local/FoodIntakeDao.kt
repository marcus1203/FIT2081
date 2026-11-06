package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.FoodIntake

@Dao
interface FoodIntakeDao {
    @Insert
    suspend fun insert(foodIntake: FoodIntake)

    @Query("SELECT * FROM food_intake WHERE patientId = :patientId")
    suspend fun getFoodIntakesByPatientId(patientId: String): List<FoodIntake>
} 