package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.NutriCoachTip

@Dao
interface NutriCoachTipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tip: NutriCoachTip)

    @Query("SELECT * FROM nutri_coach_tips WHERE patientId = :patientId ORDER BY timestamp DESC")
    suspend fun getTipsForPatient(patientId: String): List<NutriCoachTip>
} 