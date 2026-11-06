package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.questionnaire

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.R
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.FoodIntakeViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import android.util.Log

// Define Purple color if not already globally available or in Theme
private val Purple = Color(0xFF6200EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodIntakeQuestionnaireScreen(
    foodIntakeViewModel: FoodIntakeViewModel = viewModel(),
    onSave: (FoodIntakeData) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedFoodCategories by remember { mutableStateOf(List(9) { false }) }
    var selectedPersona by remember { mutableStateOf("") }
    var personaDialog by remember { mutableStateOf<String?>(null) }
    var personaDropdownExpanded by remember { mutableStateOf(false) }
    var biggestMealTime by remember { mutableStateOf(LocalTime.of(12, 0)) }
    var sleepTime by remember { mutableStateOf(LocalTime.of(22, 0)) }
    var wakeUpTime by remember { mutableStateOf(LocalTime.of(7, 0)) }
    var showBiggestMealPicker by remember { mutableStateOf(false) }
    var showSleepTimePicker by remember { mutableStateOf(false) }
    var showWakeUpTimePicker by remember { mutableStateOf(false) }
    var showPersonaAlert by remember { mutableStateOf(false) }

    val foodLabels = listOf(
        "Fruits", "Vegetables", "Grains",
        "Red Meat", "Seafood", "Poultry",
        "Fish", "Eggs", "Nuts/Seeds"
    )
    val personas = listOf(
        "Health Devotee", "Mindful Eater", "Wellness Striver",
        "Balance Seeker", "Health Procrastinator", "Food Carefree"
    )
    val personaDescriptions = mapOf(
        "Health Devotee" to "I'm passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.",
        "Mindful Eater" to "I'm health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media.",
        "Wellness Striver" to "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I've tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I'll give it a go.",
        "Balance Seeker" to "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn't have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips.",
        "Health Procrastinator" to "I'm contemplating healthy eating but it's not a priority for me right now. I know the basics about what it means to be healthy, but it doesn't seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life.",
        "Food Carefree" to "I'm not bothered about healthy eating. I don't really see the point and I don't think about it. I don't really notice healthy eating tips or recipes and I don't care what I eat."
    )

    val currentUserId = remember {
        val prefs = context.getSharedPreferences("nutritrack_prefs", Context.MODE_PRIVATE)
        prefs.getString("logged_in_user_id", "") ?: ""
    }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            foodIntakeViewModel.loadQuestionnaire(currentUserId)
        }
    }

    val savedQuestionnaire by foodIntakeViewModel.questionnaire.observeAsState()

    LaunchedEffect(savedQuestionnaire) {
        savedQuestionnaire?.let { questionnaire ->
            val savedCategories = questionnaire.selectedFoodCategories.split(",").filter { it.isNotBlank() }
            selectedFoodCategories = foodLabels.map { label -> label in savedCategories }
            selectedPersona = questionnaire.persona
            try {
                biggestMealTime = LocalTime.parse(questionnaire.biggestMealTime)
                sleepTime = LocalTime.parse(questionnaire.sleepTime)
                wakeUpTime = LocalTime.parse(questionnaire.wakeUpTime)
            } catch (e: Exception) {
                Log.e("FoodIntakeQuestionnaire", "Error parsing time from saved data: ${e.message}")
                // Keep default times or handle error appropriately
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Purple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Text(
                        text = "Food Intake Questionnaire",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(
                text = "Tick all the food categories you can eat",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (col in 0 until 3) {
                    Column {
                        for (row in 0 until 3) {
                            val idx = row * 3 + col
                            if (idx < foodLabels.size) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = selectedFoodCategories[idx],
                                        onCheckedChange = { checked ->
                                            selectedFoodCategories = selectedFoodCategories.toMutableList().also { it[idx] = checked }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Purple,
                                            uncheckedColor = Color.Gray
                                        )
                                    )
                                    Text(
                                        text = foodLabels[idx],
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your Persona",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "People can be broadly classified into 6 different types based on their eating preferences. Click on each button below to find out the different types, and select the type that best fits you!",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    PersonaButton(label = personas[0], isSelected = selectedPersona == personas[0], onClick = { personaDialog = personas[0] })
                    PersonaButton(label = personas[1], isSelected = selectedPersona == personas[1], onClick = { personaDialog = personas[1] })
                    PersonaButton(label = personas[2], isSelected = selectedPersona == personas[2], onClick = { personaDialog = personas[2] })
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    PersonaButton(label = personas[3], isSelected = selectedPersona == personas[3], onClick = { personaDialog = personas[3] })
                    PersonaButton(label = personas[4], isSelected = selectedPersona == personas[4], onClick = { personaDialog = personas[4] })
                    PersonaButton(label = personas[5], isSelected = selectedPersona == personas[5], onClick = { personaDialog = personas[5] })
                }
            }


            if (personaDialog != null) {
                PersonaInfoModal(
                    personaName = personaDialog!!,
                    personaDescription = personaDescriptions[personaDialog!!] ?: "",
                    onDismiss = { personaDialog = null },
                    onSelect = {
                        selectedPersona = personaDialog!!
                        // personaDialog = null // onDismiss will handle this
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Which persona best fits you?",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            ExposedDropdownMenuBox(
                expanded = personaDropdownExpanded,
                onExpandedChange = { personaDropdownExpanded = !personaDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = if (selectedPersona.isEmpty()) "Select a persona" else selectedPersona,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = personaDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = if (selectedPersona.isEmpty()) Color.Gray else Color.Black
                    )
                )
                ExposedDropdownMenu(
                    expanded = personaDropdownExpanded,
                    onDismissRequest = { personaDropdownExpanded = false }
                ) {
                    personas.forEach { persona ->
                        DropdownMenuItem(
                            text = { Text(persona) },
                            onClick = {
                                selectedPersona = persona
                                personaDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Timings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            TimingRow(label = "What time of day approx. do you normally eat your biggest meal?", time = biggestMealTime, onClick = { showBiggestMealPicker = true })
            TimingRow(label = "What time of day approx. do you go to sleep at night?", time = sleepTime, onClick = { showSleepTimePicker = true })
            TimingRow(label = "What time of day approx. do you wake up in the morning?", time = wakeUpTime, onClick = { showWakeUpTimePicker = true })

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (selectedPersona.isEmpty()) {
                        showPersonaAlert = true
                    } else {
                        if (currentUserId.isEmpty()) {
                            Log.e("FoodIntakeQuestionnaire", "Cannot save questionnaire: No user ID available")
                            return@Button
                        }
                        val chosenFoodCategories = foodLabels.filterIndexed { i, _ -> selectedFoodCategories[i] }
                        foodIntakeViewModel.saveQuestionnaire(
                            patientId = currentUserId,
                            selectedFoodCategories = chosenFoodCategories,
                            persona = selectedPersona,
                            biggestMealTime = biggestMealTime.format(DateTimeFormatter.ISO_LOCAL_TIME),
                            sleepTime = sleepTime.format(DateTimeFormatter.ISO_LOCAL_TIME),
                            wakeUpTime = wakeUpTime.format(DateTimeFormatter.ISO_LOCAL_TIME)
                        )
                        onSave(
                            FoodIntakeData(
                                foodCategories = chosenFoodCategories,
                                persona = selectedPersona,
                                biggestMealTime = biggestMealTime,
                                sleepTime = sleepTime,
                                wakeUpTime = wakeUpTime
                            )
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(180.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.save_), contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            if (showPersonaAlert) {
                AlertDialog(
                    onDismissRequest = { showPersonaAlert = false },
                    confirmButton = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Button(onClick = { showPersonaAlert = false }, colors = ButtonDefaults.buttonColors(containerColor = Purple), shape = RoundedCornerShape(8.dp)) {
                                Text("OK", color = Color.White)
                            }
                        }
                    },
                    title = { Text("Persona Required") },
                    text = { Text("Please select a persona before saving.") }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showBiggestMealPicker) {
            TimePickerDialog(
                onDismiss = { showBiggestMealPicker = false },
                onConfirm = { hour, minute ->
                    biggestMealTime = LocalTime.of(hour, minute)
                    showBiggestMealPicker = false
                },
                initialHour = biggestMealTime.hour,
                initialMinute = biggestMealTime.minute
            )
        }
        if (showSleepTimePicker) {
            TimePickerDialog(
                onDismiss = { showSleepTimePicker = false },
                onConfirm = { hour, minute ->
                    sleepTime = LocalTime.of(hour, minute)
                    showSleepTimePicker = false
                },
                initialHour = sleepTime.hour,
                initialMinute = sleepTime.minute
            )
        }
        if (showWakeUpTimePicker) {
            TimePickerDialog(
                onDismiss = { showWakeUpTimePicker = false },
                onConfirm = { hour, minute ->
                    wakeUpTime = LocalTime.of(hour, minute)
                    showWakeUpTimePicker = false
                },
                initialHour = wakeUpTime.hour,
                initialMinute = wakeUpTime.minute
            )
        }
    }
}

@Composable
fun PersonaButton(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick, // onClick is now explicitly named
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .defaultMinSize(minWidth = 110.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Purple.copy(alpha = 0.7f) else Purple
        )
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PersonaInfoModal(
    personaName: String,
    personaDescription: String,
    onDismiss: () -> Unit,
    onSelect: () -> Unit
) {
    val personaImageRes = when (personaName) {
        "Health Devotee" -> R.drawable.persona_1
        "Mindful Eater" -> R.drawable.persona_2
        "Wellness Striver" -> R.drawable.persona_3
        "Balance Seeker" -> R.drawable.persona_4
        "Health Procrastinator" -> R.drawable.persona_5
        "Food Carefree" -> R.drawable.persona_6
        else -> R.drawable.nutritrack_logo // Fallback image
    }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = personaImageRes),
                    contentDescription = "$personaName image",
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = personaName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = personaDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    Button(
                        onClick = {
                            onSelect()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Select", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Dismiss", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun TimingRow(label: String, time: LocalTime, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        OutlinedCard(
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.access_time),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    initialHour: Int = LocalTime.now().hour,
    initialMinute: Int = LocalTime.now().minute
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = timePickerState)
            }
        }
    )
}

data class FoodIntakeData(
    val foodCategories: List<String>,
    val persona: String,
    val biggestMealTime: LocalTime,
    val sleepTime: LocalTime,
    val wakeUpTime: LocalTime
)

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val rows = mutableListOf<List<Placeable>>()
        var currentRow = mutableListOf<Placeable>()
        var currentRowWidth = 0

        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints.copy(minWidth = 0))
            if (currentRowWidth + placeable.width > constraints.maxWidth) {
                if (currentRow.isNotEmpty()) { // Ensure non-empty row before adding
                    rows.add(currentRow)
                }
                currentRow = mutableListOf()
                currentRowWidth = 0
            }
            currentRow.add(placeable)
            currentRowWidth += placeable.width
        }
        if (currentRow.isNotEmpty()) { // Add the last row if it has items
            rows.add(currentRow)
        }

        val height = rows.sumOf { row ->
            row.maxOfOrNull { it.height } ?: 0
        } + (rows.size - 1).coerceAtLeast(0) * verticalArrangement.spacing.roundToPx()

        layout(constraints.maxWidth, height) {
            var y = 0
            rows.forEach { row ->
                var x = 0
                val rowTotalWidth = row.sumOf { it.width }
                val horizontalItemSpacing = if (row.size > 1) horizontalArrangement.spacing.roundToPx() else 0
                val usedHorizontalSpace = rowTotalWidth + (row.size - 1).coerceAtLeast(0) * horizontalItemSpacing
                val remainingSpace = constraints.maxWidth - usedHorizontalSpace

                x = when (horizontalArrangement) {
                    Arrangement.End -> remainingSpace
                    Arrangement.Center -> remainingSpace / 2
                    else -> 0 // For Start, SpaceBetween, SpaceAround, SpaceEvenly
                }


                row.forEachIndexed { index, placeable ->
                    placeable.place(x, y)
                    x += placeable.width + horizontalItemSpacing
                }
                y += (row.maxOfOrNull { it.height } ?: 0) + verticalArrangement.spacing.roundToPx()
            }
        }
    }
}
