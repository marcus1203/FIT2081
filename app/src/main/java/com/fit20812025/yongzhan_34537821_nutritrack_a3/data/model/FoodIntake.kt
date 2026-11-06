package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_intake")
data class FoodIntake(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val patientId: String,
    val foodName: String,
    val quantity: Double,
    val date: Long
) 