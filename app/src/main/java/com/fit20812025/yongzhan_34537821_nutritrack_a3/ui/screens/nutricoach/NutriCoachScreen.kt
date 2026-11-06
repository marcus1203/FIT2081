package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.nutricoach

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.NutriCoachTip
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.remote_dto.Fruit
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.FruitViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.PatientViewModel
import androidx.compose.runtime.collectAsState
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.common.GenAiUiState
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.NutriCoachTipViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NutriCoachScreen(
    patientViewModel: PatientViewModel = viewModel(),
    fruitViewModel: FruitViewModel = viewModel(),
    tipViewModel: NutriCoachTipViewModel = viewModel()
) {
    var fruitNameInput by remember { mutableStateOf("") }
    val fetchedFruitDetails by fruitViewModel.fruitDetails.observeAsState()
    val isLoadingFruit by fruitViewModel.isLoading.observeAsState(false)
    val fruitErrorMessage by fruitViewModel.errorMessage.observeAsState()

    val patient by patientViewModel.patient.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val fruitScoreOptimalThreshold = 8.0
    val currentFruitScore = patient?.let {
        if (it.sex == "Male") it.fruitHeifaScoreMale else it.fruitHeifaScoreFemale
    } ?: 0.0
    val isFruitScoreOptimal = currentFruitScore >= fruitScoreOptimalThreshold

    val genAiState by tipViewModel.genAiUiState.collectAsState()
    val savedTipsForModal by tipViewModel.savedTips.observeAsState(emptyList())
    var showAllTipsDialog by remember { mutableStateOf(false) }
    val currentPatientId = patient?.userId

    LaunchedEffect(currentPatientId) {
        if (currentPatientId != null && currentPatientId.isNotEmpty()) {
            tipViewModel.loadSavedTips(currentPatientId)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tipViewModel.resetGenAiState()
        }
    }

    val purpleColor = Color(0xFF6002E5) // Consistent purple
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp) // Consistent padding
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "NutriCoach",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 20.dp) // Spacing after title
        )

        // --- Fruits Section Wrapped in a Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Fruit Insights", // Changed title for clarity
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(bottom = 12.dp) // Spacing after section title
                )
                if (isFruitScoreOptimal) {
                    OptimalFruitScoreView()
                } else {
                    NonOptimalFruitScoreView(
                        fruitNameInput = fruitNameInput,
                        onFruitNameInputChange = { fruitNameInput = it },
                        onSearchClick = {
                            if (fruitNameInput.isNotBlank()) {
                                fruitViewModel.fetchFruitDetails(fruitNameInput)
                            }
                            keyboardController?.hide()
                        },
                        isLoading = isLoadingFruit,
                        errorMessage = fruitErrorMessage,
                        fruitDetails = fetchedFruitDetails,
                        purpleColor = purpleColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp)) // Increased spacer for section separation

        // --- GenAI Section Wrapped in a Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    // MODIFIED: Consistent typography with Fruit section title
                    text = "Daily Tip (AI)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Button(
                    onClick = {
                        currentPatientId?.let {
                            tipViewModel.generateMotivationalTip(it)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = purpleColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(2.dp, RoundedCornerShape(8.dp)), // Keep existing shadow
                    enabled = genAiState !is GenAiUiState.Loading && currentPatientId != null
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Get Motivational Message (AI)",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Motivational Tip", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(12.dp))

                // MODIFIED: AI Tip Display Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 100.dp) // Min height during loading/initial
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background) // Slightly different background
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart // Align content to start, center vertically if smaller
                ) {
                    when (val state = genAiState) {
                        is GenAiUiState.Initial -> Text("Click button above to get a fresh tip!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                        is GenAiUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        is GenAiUiState.Success -> Text(state.outputText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
                        is GenAiUiState.Error -> Text(state.errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Spacing before next button

                Button(
                    onClick = {
                        if (currentPatientId != null) {
                            tipViewModel.loadSavedTips(currentPatientId) // Ensure tips are fresh
                            showAllTipsDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = purpleColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(2.dp, RoundedCornerShape(8.dp)), // Keep existing shadow
                    enabled = currentPatientId != null
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = "Show All Tips",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Show All Saved Tips", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp)) // Bottom padding
    }

    if (showAllTipsDialog) {
        AlertDialog(
            onDismissRequest = { showAllTipsDialog = false },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(8.dp),
                    onClick = { showAllTipsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = purpleColor)
                ) {
                    Text("Done", color = Color.White) // Changed from Close to Done to match mockups
                }
            },
            title = { Text("All Saved AI Tips", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
            text = {
                Box(modifier = Modifier.heightIn(max = screenHeight * 0.6f)) {
                    if (savedTipsForModal.isEmpty()) {
                        Text("No tips saved yet. Generate some!", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(items = savedTipsForModal, key = { tip -> tip.id }) { tipItem ->
                                TipCard(tip = tipItem)
                            }
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface, // Use surface for dialog
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun TipCard(tip: NutriCoachTip) { // Styling for individual tips in the modal
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp), // Reduced horizontal padding
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), // Subtle elevation
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = tip.tip,
            modifier = Modifier.padding(12.dp), // Adjusted padding
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun OptimalFruitScoreView() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            "Your fruit intake is optimal!", // More direct message
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.primary // Use primary color for positive message
        )
        Text(
            "Keep up the great work with your fruit choices. You're doing wonderfully!",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Image(
            painter = rememberAsyncImagePainter("https://picsum.photos/seed/fruit/300/200"), // Added a seed for consistency
            contentDescription = "Random decorative image for optimal score",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp) // Slightly adjusted height
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp)) // Clip the image
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NonOptimalFruitScoreView(
    fruitNameInput: String,
    onFruitNameInputChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    fruitDetails: Fruit?,
    purpleColor: Color // Pass color for consistency
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Text(
        "Your fruit score could be improved. Let's find some fruit facts!",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 12.dp)
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = fruitNameInput,
            onValueChange = onFruitNameInputChange,
            placeholder = {Text("E.g., banana, apple", color = Color.Gray)},
            label = { Text("Enter fruit name") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp), // Consistent rounding
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchClick() })
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onSearchClick,
            enabled = !isLoading,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = purpleColor),
            modifier = Modifier.height(IntrinsicSize.Min) // Match text field height more closely
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Color.White)
            } else {
                Icon(Icons.Filled.Search, contentDescription = "Search Fruit", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Details", color = Color.White)
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp)) // Adjusted spacer

    if (errorMessage != null) {
        Text(errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
    }

    fruitDetails?.let { fruit ->
        DynamicFruitDetailsCard(fruit = fruit)
    }
}

@Composable
fun DynamicFruitDetailsCard(fruit: Fruit) { // Card for displaying fruit details
    Card(
        shape = RoundedCornerShape(12.dp), // Consistent rounding
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp), // Spacing above card
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)), // Slightly different background
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                fruit.name?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } ?: "Fruit Details", // Capitalize
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Divider(modifier = Modifier.padding(bottom = 8.dp))

            fruit.family?.let { FruitDetailRow("Family", it) }
            fruit.order?.let { FruitDetailRow("Order", it) }
            fruit.genus?.let { FruitDetailRow("Genus", it) }

            fruit.nutritions?.let { nutritions ->
                Spacer(modifier = Modifier.height(8.dp))
                Text("Nutritions (per 100g):", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                FruitDetailRow("Calories", nutritions.calories?.toString(), "kcal")
                FruitDetailRow("Fat", nutritions.fat?.toString(), "g")
                FruitDetailRow("Sugar", nutritions.sugar?.toString(), "g")
                FruitDetailRow("Carbohydrates", nutritions.carbohydrates?.toString(), "g")
                FruitDetailRow("Protein", nutritions.protein?.toString(), "g")
            } ?: Text("Nutritional information not available.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun FruitDetailRow(label: String, value: String?, unit: String = "") {
    if (value != null && value.isNotBlank()) { // Ensure value is not blank
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp), // Adjusted padding
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$label:",
                fontWeight = FontWeight.Medium, // Adjusted weight
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$value ${if (unit.isNotEmpty()) unit else ""}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

