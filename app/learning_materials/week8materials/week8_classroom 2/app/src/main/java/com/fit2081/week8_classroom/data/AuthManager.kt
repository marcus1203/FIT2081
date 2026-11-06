package com.fit2081.week8_classroom.data


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
  */
object AuthManager {
    val _userId: MutableState<String?> = mutableStateOf(null)


    fun login(userId: String) {
        _userId.value = userId
    }

    fun logout() {
        _userId.value = null
    }

    fun getStudentId(): String? {
        return _userId.value
    }
}

