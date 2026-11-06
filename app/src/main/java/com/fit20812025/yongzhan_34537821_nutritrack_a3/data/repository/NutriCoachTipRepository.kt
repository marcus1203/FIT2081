package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository

import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local.NutriCoachTipDao
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.NutriCoachTip

class NutriCoachTipRepository(private val nutriCoachTipDao: NutriCoachTipDao) {
    suspend fun insert(tip: NutriCoachTip) = nutriCoachTipDao.insert(tip)
    suspend fun getTipsForPatient(patientId: String): List<NutriCoachTip> = nutriCoachTipDao.getTipsForPatient(patientId)
} 