package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.settings.clinician

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.ClinicianViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.common.GenAiUiState

@Composable
fun ClinicianDashboardScreen(
    clinicianViewModel: ClinicianViewModel,
    onDone: () -> Unit
) {
    val purple = Color(0xFF6002E5) // Consider moving to Theme.kt if used widely
    val averageScores by clinicianViewModel.averageScores.observeAsState()
    val dataPatternsUiState by clinicianViewModel.dataPatternsUiState.collectAsState()

    val avgMale = averageScores?.averageMaleScore ?: 0.0
    val avgFemale = averageScores?.averageFemaleScore ?: 0.0

    var showLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf<String?>(null) }
    var insightsList by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(dataPatternsUiState) {
        when (val state = dataPatternsUiState) {
            is GenAiUiState.Loading -> {
                showLoading = true
                showError = null
                insightsList = emptyList() // Clear previous insights
            }
            is GenAiUiState.Error -> {
                showLoading = false
                showError = state.errorMessage
                insightsList = emptyList()
            }
            is GenAiUiState.Success -> {
                showLoading = false
                showError = null
                insightsList = state.outputText.split("\n\n")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
            }
            is GenAiUiState.Initial -> {
                showLoading = false
                showError = null
                insightsList = emptyList()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Adjusted vertical padding
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Clinician Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp) // Adjusted bottom padding
        )
        Text(
            text = "HEIFA Score Averages",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
        )
        // HEIFA Score Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Average HEIFA (Male)", fontWeight = FontWeight.Medium)
                Text(
                    String.format("%.1f", avgMale),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp)) // Reduced spacer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Average HEIFA (Female)", fontWeight = FontWeight.Medium)
                Text(
                    String.format("%.1f", avgFemale),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp)) // Adjusted spacer
        Divider()
        Spacer(modifier = Modifier.height(16.dp)) // Adjusted spacer

        Button(
            onClick = { clinicianViewModel.findDataPatterns() },
            colors = ButtonDefaults.buttonColors(containerColor = purple),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !showLoading
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Find Data Patterns",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Find Data Patterns", color = Color.White, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Display Area for Insights, Loading, or Error
        Box(modifier = Modifier.weight(1f)) { // This Box will take up remaining space for scrolling
            when {
                showLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                showError != null -> {
                    Text(
                        text = showError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                insightsList.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(), // Fill the Box
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        itemsIndexed(insightsList) { index, insightText ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp), // Space between insight cards
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Insight ${index + 1}:",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall, // Using titleSmall for "Insight X:"
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = insightText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                dataPatternsUiState is GenAiUiState.Initial || (dataPatternsUiState is GenAiUiState.Success && insightsList.isEmpty()) -> {
                    // Show this if initial state or if AI successfully returned no specific patterns
                    Text(
                        text = if (dataPatternsUiState is GenAiUiState.Initial) "Click 'Find Data Patterns' to generate insights." else "No specific patterns were identified.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
            }
        }

        //Spacer(modifier = Modifier.weight(if (insightsList.isEmpty() && !showLoading && showError == null) 1f else 0.1f))
        Button(
            onClick = onDone,
            colors = ButtonDefaults.buttonColors(containerColor = purple),
            shape = RoundedCornerShape(12.dp), // Consistent rounding
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp) // Padding around done button
                .height(52.dp)
        ) {
            Icon(Icons.Default.Done, contentDescription = "Done", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Done", color = Color.White, fontSize = 16.sp)
        }
    }
}
