package com.fit20812025.yongzhan_34537821

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.fit20812025.yongzhan_34537821.ui.theme.Yongzhan_34537821Theme

class FoodIntakeQuestionnaire : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Yongzhan_34537821Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { FoodIntakeTopBar() }
                ) { innerPadding ->
                    FoodIntakeElements(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodIntakeTopBar(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF6200EE),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        ),
        title = {
            Text(
                text = "Food Intake Questionnaire",
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    context.startActivity(Intent(context, LoginScreen::class.java))
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Login Screen"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodIntakeElements(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val userId = getUserId(context)
    val sharedPreferences = context.getSharedPreferences("food_prefs_$userId", Context.MODE_PRIVATE)

    var selectedPersona by remember { mutableStateOf(sharedPreferences.getString(SharedPrefKeys.SELECTED_PERSONA_KEY, "") ?: "") }
    var biggestMealTime by remember { mutableStateOf(sharedPreferences.getString(SharedPrefKeys.BIGGEST_MEAL_TIME_KEY, "12:00") ?: "12:00") }
    var sleepTime by remember { mutableStateOf(sharedPreferences.getString(SharedPrefKeys.SLEEP_TIME_KEY, "22:00") ?: "22:00") }
    var wakeUpTime by remember { mutableStateOf(sharedPreferences.getString(SharedPrefKeys.WAKE_UP_TIME_KEY, "07:00") ?: "07:00") }

    var foodCategories by remember {
        mutableStateOf(List(9) { index ->
            sharedPreferences.getBoolean("food_category_$index", false)
        })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CheckboxSection(
            foodCategories = foodCategories,
            foodLabels = SharedData.foodLabels,
            onFoodCategoryChanged = { index, checked ->
                foodCategories = foodCategories.toMutableList().also { it[index] = checked }
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        PersonaSection(selectedPersona) { selectedPersona = it }
        Spacer(modifier = Modifier.height(8.dp))

        BestPersonaDropdown(selectedPersona) { selectedPersona = it }

        Spacer(modifier = Modifier.height(16.dp))

        TimingsSection(
            biggestMealTime = biggestMealTime,
            sleepTime = sleepTime,
            wakeUpTime = wakeUpTime,
            onBiggestMealTimeChange = { biggestMealTime = it },
            onSleepTimeChange = { sleepTime = it },
            onWakeUpTimeChange = { wakeUpTime = it }
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            SaveButton(
                context = context,
                foodCategories = foodCategories.toList(),
                selectedPersona = selectedPersona,
                biggestMealTime = biggestMealTime,
                sleepTime = sleepTime,
                wakeUpTime = wakeUpTime
            )
        }
    }
}

@Composable
fun CheckboxSection(
    foodCategories: List<Boolean>,
    foodLabels: List<String>,
    onFoodCategoryChanged: (Int, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Text(
            text = "Tick all the food categories you can eat",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            foodLabels.chunked(3).forEach { chunk ->
                Column {
                    chunk.forEachIndexed { index, label ->
                        val actualIndex = foodLabels.indexOf(label)
                        CheckboxWithLabel(label, foodCategories[actualIndex]) {
                            onFoodCategoryChanged(actualIndex, it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckboxWithLabel(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(0.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF6200EE),
                uncheckedColor = Color.Gray
            ),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun PersonaSection(selectedPersona: String, onPersonaSelected: (String) -> Unit) {
    var showPersonaInfo by remember { mutableStateOf(false) }
    var selectedPersonaInfo by remember { mutableStateOf<Pair<String, String>?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            text = "Your Persona",
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "People can be broadly classified into 6 different types based on their eating preferences. Click on each button below to find out the different types, and select the type that best fits you!",
            style = MaterialTheme.typography.bodyMedium
        )

        // Use Row with three Columns for custom alignment
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // First Column (Left-Aligned)
            Column(horizontalAlignment = Alignment.Start) {
                PersonaButton(
                    label = SharedData.personas[0].first,
                    isSelected = SharedData.personas[0].first == selectedPersona,
                    onClick = {
                        selectedPersonaInfo = SharedData.personas[0]
                        showPersonaInfo = true
                    }
                )
                PersonaButton(
                    label = SharedData.personas[1].first,
                    isSelected = SharedData.personas[1].first == selectedPersona,
                    onClick = {
                        selectedPersonaInfo = SharedData.personas[1]
                        showPersonaInfo = true
                    }
                )
            }

            // Second Column (Center-Aligned)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PersonaButton(
                    label = SharedData.personas[2].first,
                    isSelected = SharedData.personas[2].first == selectedPersona,
                    onClick = {
                        selectedPersonaInfo = SharedData.personas[2]
                        showPersonaInfo = true
                    }
                )
                PersonaButton(
                    label = SharedData.personas[3].first,
                    isSelected = SharedData.personas[3].first == selectedPersona,
                    onClick = {
                        selectedPersonaInfo = SharedData.personas[3]
                        showPersonaInfo = true
                    }
                )
            }

            // Third Column (Right-Aligned)
            Column(horizontalAlignment = Alignment.End) {
                PersonaButton(
                    label = SharedData.personas[4].first,
                    isSelected = SharedData.personas[4].first == selectedPersona,
                    onClick = {
                        selectedPersonaInfo = SharedData.personas[4]
                        showPersonaInfo = true
                    }
                )
                PersonaButton(
                    label = SharedData.personas[5].first,
                    isSelected = SharedData.personas[5].first == selectedPersona,
                    onClick = {
                        selectedPersonaInfo = SharedData.personas[5]
                        showPersonaInfo = true
                    }
                )
            }
        }
    }

    if (showPersonaInfo && selectedPersonaInfo != null) {
        PersonaInfoModal(
            personaName = selectedPersonaInfo!!.first,
            personaDescription = selectedPersonaInfo!!.second,
            onDismiss = { showPersonaInfo = false }
        )
    }
}


@Composable
fun PersonaInfoModal(
    personaName: String,
    personaDescription: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val imageResId = when (personaName) {
                    "Health Devotee" -> R.drawable.persona_1
                    "Mindful Eater" -> R.drawable.persona_2
                    "Wellness Striver" -> R.drawable.persona_3
                    "Balance Seeker" -> R.drawable.persona_4
                    "Health Procrastinator" -> R.drawable.persona_5
                    "Food Carefree" -> R.drawable.persona_6
                    else -> R.drawable.persona_1
                }
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "$personaName image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = personaName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = personaDescription,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Dismiss", color = Color.White)
                }
            }
        }
    )
}

@Composable
fun PersonaButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(0.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6200EE)
        )
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White,
            maxLines = 2
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BestPersonaDropdown(selectedPersona: String, onPersonaSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Text(
        "Which persona best fits you?",
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(0.dp)
    )
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedPersona.ifEmpty { "Select a persona" },
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SharedData.personas.forEach { persona ->
                DropdownMenuItem(
                    text = { Text(persona.first) },
                    onClick = {
                        onPersonaSelected(persona.first)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimingsSection(
    biggestMealTime: String,
    sleepTime: String,
    wakeUpTime: String,
    onBiggestMealTimeChange: (String) -> Unit,
    onSleepTimeChange: (String) -> Unit,
    onWakeUpTimeChange: (String) -> Unit
) {
    val showBiggestMealTimePicker = remember { mutableStateOf(false) }
    val showSleepTimePicker = remember { mutableStateOf(false) }
    val showWakeUpTimePicker = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = "Timings", fontWeight = FontWeight.Bold)

        TimePickerDialogHandler(
            label = "What time of day approx. do you normally eat your biggest meal?",
            time = biggestMealTime,
            onTimeChange = onBiggestMealTimeChange,
            showDialogState = showBiggestMealTimePicker
        )

        TimePickerDialogHandler(
            label = "What time of day approx. do you go to sleep at night?",
            time = sleepTime,
            onTimeChange = onSleepTimeChange,
            showDialogState = showSleepTimePicker
        )

        TimePickerDialogHandler(
            label = "What time of day approx. do you wake up in the morning?",
            time = wakeUpTime,
            onTimeChange = onWakeUpTimeChange,
            showDialogState = showWakeUpTimePicker
        )
    }
}

@Composable
fun TimePickerRow(
    question: String,
    time: String,
    onTimeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        OutlinedCard(
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable { onTimeClick() },
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
                    text = time,
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onCancel: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute
    )
    TimePickerDialog(
        onDismissRequest = { onCancel() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    ) {
        androidx.compose.material3.TimePicker(
            state = timePickerState
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    dismissButton()
                    Spacer(modifier = Modifier.width(8.dp))
                    confirmButton()
                }
            }
        }
    }
}

@Composable
fun TimePickerDialogHandler(
    label: String,
    time: String,
    onTimeChange: (String) -> Unit,
    showDialogState: MutableState<Boolean>
) {
    if (showDialogState.value) {
        val (hour, minute) = time.split(":").map { it.toInt() }
        ShowTimePickerDialog(
            initialHour = hour,
            initialMinute = minute,
            onCancel = { showDialogState.value = false },
            onConfirm = { newHour, newMinute ->
                onTimeChange(String.format("%02d:%02d", newHour, newMinute))
                showDialogState.value = false
            }
        )
    }

    TimePickerRow(
        question = label,
        time = time,
        onTimeClick = { showDialogState.value = true }
    )
}

@Composable
fun SaveButton(
    context: Context,
    foodCategories: List<Boolean>,
    selectedPersona: String,
    biggestMealTime: String,
    sleepTime: String,
    wakeUpTime: String
) {
    val userId = getUserId(context)
    Button(
        onClick = {
            if (selectedPersona.isEmpty()) {
                Toast.makeText(context, "Please select a persona", Toast.LENGTH_SHORT).show()
                return@Button
            }
            val sharedPreferences = context.getSharedPreferences("food_prefs_$userId", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                foodCategories.forEachIndexed { index, checked ->
                    putBoolean("food_category_$index", checked)
                }
                putString(SharedPrefKeys.SELECTED_PERSONA_KEY, selectedPersona)
                putString(SharedPrefKeys.BIGGEST_MEAL_TIME_KEY, biggestMealTime)
                putString(SharedPrefKeys.SLEEP_TIME_KEY, sleepTime)
                putString(SharedPrefKeys.WAKE_UP_TIME_KEY, wakeUpTime)
                commit()
            }
            Toast.makeText(context, "Preferences saved", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, HomeScreen::class.java))
        },
        modifier = Modifier
            .width(150.dp)
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6200EE)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.save_),
            contentDescription = "Save icon",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Save",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Shared preference keys for meal times and persona
object SharedPrefKeys {
    const val SELECTED_PERSONA_KEY = "selected_persona"
    const val BIGGEST_MEAL_TIME_KEY = "biggest_meal_time"
    const val SLEEP_TIME_KEY = "sleep_time"
    const val WAKE_UP_TIME_KEY = "wake_up_time"
}

// Shared data for food labels and personas
object SharedData {
    val foodLabels = listOf(
        "Fruits", "Vegetables", "Grains",
        "Red Meat", "Seafood", "Poultry",
        "Fish", "Eggs", "Nuts/Seeds"
    )

    val personas = listOf(
        "Health Devotee" to "I'm passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.",
        "Balance Seeker" to "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn't have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips.",
        "Mindful Eater" to "I'm health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media.",
        "Health Procrastinator" to "I'm contemplating healthy eating but it's not a priority for me right now. I know the basics about what it means to be healthy, but it doesn't seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life.\n",
        "Wellness Striver" to "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I've tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I'll give it a go.",

        "Food Carefree" to "I'm not bothered about healthy eating. I don't really see the point and I don't think about it. I don't really notice healthy eating tips or recipes and I don't care what I eat."
    )
}

// Helper function to retrieve current user ID from preferences
fun getUserId(context: Context): String {
    val loginPrefs = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    return loginPrefs.getString("current_user_id", "") ?: ""
}