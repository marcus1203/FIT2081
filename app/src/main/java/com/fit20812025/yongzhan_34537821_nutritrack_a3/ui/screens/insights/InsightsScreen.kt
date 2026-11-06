package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.insights

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.PatientViewModel

@Composable
fun InsightsScreen(
    patientViewModel: PatientViewModel,
    onNavigateToNutriCoach: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val patient by patientViewModel.patient.observeAsState()
    val context = LocalContext.current
    val purple = Color(0xFF6200EE)

    val sex = patient?.sex ?: "Male"

    val categories = listOf(
        "Vegetables" to (if (sex == "Male") patient?.vegetablesHeifaScoreMale else patient?.vegetablesHeifaScoreFemale),
        "Fruits" to (if (sex == "Male") patient?.fruitHeifaScoreMale else patient?.fruitHeifaScoreFemale),
        "Grains & Cereals" to (if (sex == "Male") patient?.grainsAndCerealsHeifaScoreMale else patient?.grainsAndCerealsHeifaScoreFemale),
        "Whole Grains" to (if (sex == "Male") patient?.wholeGrainsHeifaScoreMale else patient?.wholeGrainsHeifaScoreFemale),
        "Meat & Alternatives" to (if (sex == "Male") patient?.meatAndAlternativesHeifaScoreMale else patient?.meatAndAlternativesHeifaScoreFemale),
        "Dairy" to (if (sex == "Male") patient?.dairyAndAlternativesHeifaScoreMale else patient?.dairyAndAlternativesHeifaScoreFemale),
        "Water" to (if (sex == "Male") patient?.waterHeifaScoreMale else patient?.waterHeifaScoreFemale),
        "Unsaturated Fats" to (if (sex == "Male") patient?.unsaturatedFatHeifaScoreMale else patient?.unsaturatedFatHeifaScoreFemale),
        "Saturated Fats" to (if (sex == "Male") patient?.saturatedFatHeifaScoreMale else patient?.saturatedFatHeifaScoreFemale),
        "Sodium" to (if (sex == "Male") patient?.sodiumHeifaScoreMale else patient?.sodiumHeifaScoreFemale),
        "Sugar" to (if (sex == "Male") patient?.sugarHeifaScoreMale else patient?.sugarHeifaScoreFemale),
        "Alcohol" to (if (sex == "Male") patient?.alcoholHeifaScoreMale else patient?.alcoholHeifaScoreFemale),
        "Discretionary Foods" to (if (sex == "Male") patient?.discretionaryHeifaScoreMale else patient?.discretionaryHeifaScoreFemale)
    )

    val maxScores = listOf(
        10, 10, 5, 5, 10, 10, 5, 5, 5, 10, 10, 5, 10
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Insights: Food Score",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        itemsIndexed(categories) { index, (label, scoreValue) ->
            val currentScore = scoreValue ?: 0.0
            val maxScore = maxScores.getOrElse(index) { 1 }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(150.dp)
                )
                LinearProgressIndicator(
                    progress = { (currentScore / maxScore).toFloat().coerceIn(0f, 1f) },
                    color = purple,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .padding(horizontal = 10.dp),
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = "${String.format("%.2f", currentScore)}/$maxScore",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(65.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            // MODIFIED: Column alignment changed to CenterHorizontally for the buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally // Center the buttons
            ) {
                Text(
                    text = "Total Food Quality Score",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start) // Keep this text aligned to start
                        .padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth() // Keep Row full width for progress bar
                ) {
                    val totalScoreValue = (if (sex == "Male") patient?.heifaTotalScoreMale else patient?.heifaTotalScoreFemale) ?: 0.0
                    val totalMaxScore = 100.0

                    LinearProgressIndicator(
                        progress = { (totalScoreValue / totalMaxScore).toFloat().coerceIn(0f, 1f) },
                        color = purple,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .padding(end = 10.dp),
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "${String.format("%.2f", totalScoreValue)}/${totalMaxScore.toInt()}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        val totalScoreValue = (if (sex == "Male") patient?.heifaTotalScoreMale else patient?.heifaTotalScoreFemale) ?: 0.0
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Hi, my total food quality score is ${String.format("%.2f", totalScoreValue)}/100"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share your Food Quality Score via"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = purple),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .widthIn(min = 200.dp), // Optional: set a minimum width for better touch target
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Share with someone",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onNavigateToNutriCoach() },
                    colors = ButtonDefaults.buttonColors(containerColor = purple),
                    modifier = Modifier
                        // REMOVED: .fillMaxWidth()
                        // REMOVED: .wrapContentSize()
                        .padding(vertical = 4.dp)
                        .widthIn(min = 200.dp), // Optional: set a minimum width
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Improve my Diet",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Improve my diet!",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
