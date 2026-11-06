package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels // Required for by viewModels()
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.questionnaire.FoodIntakeQuestionnaireScreen
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.theme.Yongzhan_34537821_nutritrack_A3Theme
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.AuthViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.FoodIntakeViewModel

class FoodIntakeQuestionnaireActivity : ComponentActivity() {
    // Obtain ViewModel instance
    private val authViewModel: AuthViewModel by viewModels()
    private val foodIntakeViewModel: FoodIntakeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Yongzhan_34537821_nutritrack_A3Theme {
                FoodIntakeQuestionnaireScreen(
                    foodIntakeViewModel = foodIntakeViewModel, // Pass the instance
                    onSave = {
                        setResult(RESULT_OK) // Optionally set a result if HomeScreen needs to know it was saved
                        finish()
                    },
                    onBack = {
                        authViewModel.logout()
                        val intent = Intent(this@FoodIntakeQuestionnaireActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finishAffinity()
                    }
                )
            }
        }
    }
}
