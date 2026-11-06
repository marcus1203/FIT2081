package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.remote_dto

data class Fruit(
    val name: String?, // Made nullable for safety
    val id: Int?,
    val family: String?,
    val order: String?,
    val genus: String?,
    val nutritions: Nutritions?
)

data class Nutritions(
    val calories: Double?,
    val fat: Double?,
    val sugar: Double?,
    val carbohydrates: Double?,
    val protein: Double?
)