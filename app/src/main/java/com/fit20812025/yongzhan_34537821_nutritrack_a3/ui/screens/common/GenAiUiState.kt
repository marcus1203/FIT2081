package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.common

sealed interface GenAiUiState {
    object Initial : GenAiUiState
    object Loading : GenAiUiState
    data class Success(val outputText: String) : GenAiUiState
    data class Error(val errorMessage: String) : GenAiUiState
}