package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fit20812025.yongzhan_34537821_nutritrack_a3.R // Ensure R is imported
// Ensure FoodIntakeQuestionnaireActivity is correctly imported if used, or use NavController
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.PatientViewModel

@Composable
fun HomeScreen(
    patientViewModel: PatientViewModel, // Changed from direct userName, foodScore
    onEditQuestionnaire: () -> Unit, // This will be navController.navigate("questionnaire")
    onNavigateToInsights: () -> Unit,
    onNavigateToNutriCoach: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val patient by patientViewModel.patient.observeAsState()

    val userName = patient?.name ?: "User" // Derived from ViewModel
    val sex = patient?.sex ?: "Male" // Derived
    val score = (if (sex == "Male") patient?.heifaTotalScoreMale else patient?.heifaTotalScoreFemale) ?: 0.0 // Derived
    val foodScore = String.format("%.2f", score) // Derived

    LaunchedEffect(userName) {
        println("DEBUG: userName = $userName")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Hello,",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        // Greeting and Edit button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "You've already filled in your Food Intake Questionnaire, but you can change details here:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Button(
                onClick = {
                    // Using the onEditQuestionnaire lambda which will be navController.navigate
                    onEditQuestionnaire()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6002E5)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit")
            }
        }
        // Food plate image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.food_plate),
                contentDescription = "Balanced food plate",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(280.dp).clip(CircleShape)
            )
        }
        // My Score section
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Score",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigateToInsights() }
                ) {
                    Text(
                        text = "See all scores",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "See all scores",
                        tint = Color.Gray
                    )
                }
            }
            // Food Quality Score
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow_up),
                            contentDescription = "Food Quality Score",
                            modifier = Modifier.size(40.dp),
                        )
                        Text(
                            text = "Your Food Quality score",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Text(
                        text = "$foodScore/100",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
        Divider(
            modifier = Modifier.padding(horizontal = 8.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        // What is the Food Quality Score section
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = "What is the Food Quality Score?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.\n\n" +
                        "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
