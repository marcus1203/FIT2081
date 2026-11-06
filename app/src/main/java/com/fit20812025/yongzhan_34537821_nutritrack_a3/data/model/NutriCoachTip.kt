package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nutri_coach_tips")
data class NutriCoachTip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: String,
    val tip: String,
    val timestamp: Long = System.currentTimeMillis()
) 