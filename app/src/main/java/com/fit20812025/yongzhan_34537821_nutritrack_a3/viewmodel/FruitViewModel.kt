package com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.remote_dto.Fruit
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository.FruitRepository
import kotlinx.coroutines.launch

class FruitViewModel : ViewModel() {
    private val repository = FruitRepository()

    private val _fruitDetails = MutableLiveData<Fruit?>()
    val fruitDetails: LiveData<Fruit?> = _fruitDetails

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchFruitDetails(fruitName: String) {
        if (fruitName.isBlank()) {
            _errorMessage.value = "Fruit name cannot be empty."
            return
        }
        _isLoading.value = true
        _errorMessage.value = null // Clear previous error
        _fruitDetails.value = null // Clear previous details

        viewModelScope.launch {
            try {
                val response = repository.getFruit(fruitName.trim())
                if (response.isSuccessful) {
                    _fruitDetails.postValue(response.body())
                } else {
                    _errorMessage.postValue("Error: ${response.code()} - Fruit not found or API error.")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Network error: Please check your connection.")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}