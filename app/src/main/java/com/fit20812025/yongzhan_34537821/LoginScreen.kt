package com.fit20812025.yongzhan_34537821

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.fit20812025.yongzhan_34537821.ui.theme.Yongzhan_34537821Theme
import java.io.BufferedReader
import java.io.InputStreamReader

// Add this import for ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3Api

// Data class to store user information from CSV
data class UserData(
    val phoneNumber: String,
    val userId: String,
    val sex: String,
    val foodScoreMale: Float,
    val foodScoreFemale: Float
)

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saveCsvDataToSharedPreferences(this) //load csv data to shared preferences
        setContent {
            Yongzhan_34537821Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreenPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

}

@Composable
fun LoginScreenPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_data_prefs", Context.MODE_PRIVATE)

    // State for user data and validation
    var userDataList by remember { mutableStateOf<List<UserData>>(emptyList()) }
    var userIds by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // Load user data from SharedPreferences on initial composition
    val userCount = sharedPreferences.getInt("user_count", 0)
    Log.d("CSVParser", "Retrieved user count from SharedPreferences: $userCount")

    if (userDataList.isEmpty() && userCount > 0) {
        // Parse stored user data from SharedPreferences
        val users = mutableListOf<UserData>()
        for (i in 0 until userCount) {
            val line = sharedPreferences.getString("user_$i", null)
            Log.d("CSVParser", "Retrieved user_$i: $line")

            line?.let {
                val values = it.split(",")
                if (values.size >= 5) {
                    users.add(
                        UserData(
                            phoneNumber = values[0],
                            userId = values[1],
                            sex = values[2],
                            foodScoreMale = values[3].toFloatOrNull() ?: 0f,
                            foodScoreFemale = values[4].toFloatOrNull() ?: 0f
                        )
                    )
                    Log.d("CSVParser", "Added user: ID=${values[1]}, Sex=${values[2]}")
                }
            }
        }

        // update state with parsed user data
        userDataList = users
        userIds = users.map { it.userId }.distinct().sortedBy { it.toIntOrNull() ?: Int.MAX_VALUE }
        if (userIds.isNotEmpty()) {
            selectedId = "User ID"
        }
    }

    // Main login screen content
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Login Header
        Text(
            text = "Log In",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // User ID Dropdown
        UserIDDropdown(
            userIds = userIds,
            selectedId = selectedId,
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            onItemSelected = { selectedId = it },
            isError = isError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone Number Input
        PhoneNumberInput(
            phoneNumber = phoneNumber,
            onPhoneNumberChange = { phoneNumber = it },
            isError = isError
        )

        // Error Message
        if (isError) {
            ErrorMessage(message = errorMessage)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instruction Text
        InstructionText()

        Spacer(modifier = Modifier.height(16.dp))

        // Continue Button
        ContinueButton(
            userDataList = userDataList,
            selectedId = selectedId,
            phoneNumber = phoneNumber,
            onError = { message ->
                errorMessage = message
                isError = true
            },
            onSuccess = { matchingUser ->
                val loginPrefs =
                    context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                loginPrefs.edit {
                    putString("current_user_id", selectedId)
                    putString("current_user_phone", phoneNumber)
                }

                val userPrefs =
                    context.getSharedPreferences("user_prefs_$selectedId", Context.MODE_PRIVATE)
                userPrefs.edit {
                    putFloat("foodScoreMale", matchingUser.foodScoreMale)
                    putFloat("foodScoreFemale", matchingUser.foodScoreFemale)
                    putString("sex", matchingUser.sex)
                }
                context.startActivity(Intent(context, FoodIntakeQuestionnaire::class.java))
            }
        )
    }
}

fun saveCsvDataToSharedPreferences(context: Context) {
    try {
        val inputStream = context.assets.open("user_data.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val sharedPreferences = context.getSharedPreferences("user_data_prefs", Context.MODE_PRIVATE)

        // Read header line to get column indices
        val headers = reader.readLine().split(",")
        Log.d("CSVParser", "CSV Headers: ${headers.joinToString()}")

        val userIdIndex = headers.indexOf("User_ID")
        val phoneIndex = headers.indexOf("Phone")
        val sexIndex = headers.indexOf("Sex")

        val editor = sharedPreferences.edit()
        var line: String?

        // Parse CSV data and save to SharedPreferences
        var index = 0
        while (reader.readLine().also { line = it } != null) {
            line?.let {
                val values = it.split(",")
                if (values.size >= 5) {
                    Log.d("CSVParser", "Parsing user data: $it")
                    editor.putString("user_$index", it)

                    // Extract user ID and sex for category scores
                    val userId = values[userIdIndex]
                    val sex = values[sexIndex]

                    Log.d("CSVParser", "User ID: $userId, Phone: ${values[phoneIndex]}, Sex: $sex")

                    // Save all category scores for this user
                    val categoryScores = mapOf(
                        "Vegetables" to safeGetScore(headers, values, "VegetablesHEIFAscore", sex),
                        "Fruits" to safeGetScore(headers, values, "FruitHEIFAscore", sex),
                        "Grains & Cereals" to safeGetScore(headers, values, "GrainsandcerealsHEIFAscore", sex),
                        "Whole Grains" to safeGetScore(headers, values, "WholegrainsHEIFAscore", sex),
                        "Meat & Alternatives" to safeGetScore(headers, values, "MeatandalternativesHEIFAscore", sex),
                        "Dairy" to safeGetScore(headers, values, "DairyandalternativesHEIFAscore", sex),
                        "Water" to safeGetScore(headers, values, "WaterHEIFAscore", sex),
                        "Unsaturated Fats" to safeGetScore(headers, values, "UnsaturatedFatHEIFAscore", sex),
                        "Saturated Fats" to safeGetScore(headers, values, "SaturatedFatHEIFAscore", sex),
                        "Sodium" to safeGetScore(headers, values, "SodiumHEIFAscore", sex),
                        "Sugar" to safeGetScore(headers, values, "SugarHEIFAscore", sex),
                        "Alcohol" to safeGetScore(headers, values, "AlcoholHEIFAscore", sex),
                        "Discretionary Foods" to safeGetScore(headers, values, "DiscretionaryHEIFAscore", sex)
                    )

                    // Save each category score with user-specific key
                    categoryScores.forEach { (category, score) ->
                        editor.putFloat("${userId}_category_${category}", score)
                        Log.d("CSVParser", "Saved score for $userId - $category: $score")
                    }

                    index++
                }
            }
        }

        editor.putInt("user_count", index)
        Log.d("CSVParser", "Total users parsed: $index")

        editor.apply()
        reader.close()

        Log.d("CSVParser", "CSV data successfully loaded into SharedPreferences")
    } catch (e: Exception) {
        Log.e("CSVParser", "Error loading user data: ${e.message}")
        e.printStackTrace()
    }
}

// Helper function to safely get score from CSV values
fun safeGetScore(headers: List<String>, values: List<String>, keyPrefix: String, sex: String): Float {
    val columnName = "$keyPrefix${if (sex == "Male") "Male" else "Female"}"
    val index = headers.indexOf(columnName)
    if (index == -1) {
        Log.e("CSVParser", "Column not found: $columnName")
        return 0f
    }
    return values[index].toFloatOrNull() ?: 0f
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserIDDropdown(
    userIds: List<String>,
    selectedId: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onItemSelected: (String) -> Unit,
    isError: Boolean
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange, // Pass the lambda directly
    ) {
        OutlinedTextField(
            value = selectedId,
            onValueChange = {},
            label = { Text("My ID (Provided by your Clinician)") },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(8.dp),
            isError = isError
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) } // Explicitly pass `false` to close the menu
        ) {
            userIds.forEach { id ->
                DropdownMenuItem(
                    text = { Text(id) },
                    onClick = {
                        onItemSelected(id)
                        onExpandedChange(false) // Close the dropdown after selection
                    }
                )
            }
        }
    }
}

@Composable
fun PhoneNumberInput(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    isError: Boolean
) {
    OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChange,
        label = { Text("Phone number") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        isError = isError
    )
}

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = message,
        color = Color.Red,
        fontSize = 14.sp,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
fun InstructionText() {
    Text(
        text = "This app is only for pre-registered users. Please have your ID and phone number handy before continuing.",
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun ContinueButton(
    userDataList: List<UserData>,
    selectedId: String,
    phoneNumber: String,
    onError: (String) -> Unit,
    onSuccess: (UserData) -> Unit
) {
    val context = LocalContext.current

    Button(
        onClick = {
            val matchingUser = userDataList.find {
                it.userId == selectedId && it.phoneNumber == phoneNumber
            }
            if (matchingUser != null) {
                // Get all category scores for this user from shared preferences
                val sharedPreferences = context.getSharedPreferences("user_data_prefs", Context.MODE_PRIVATE)
                val userPrefs = context.getSharedPreferences("user_prefs_$selectedId", Context.MODE_PRIVATE)

                // Save basic user info
                userPrefs.edit {
                    putFloat("foodScoreMale", matchingUser.foodScoreMale)
                    putFloat("foodScoreFemale", matchingUser.foodScoreFemale)
                    putString("sex", matchingUser.sex)

                    // Copy all category scores to user-specific preferences
                    val categories = listOf(
                        "Vegetables", "Fruits", "Grains & Cereals", "Whole Grains",
                        "Meat & Alternatives", "Dairy", "Water", "Unsaturated Fats",
                        "Saturated Fats", "Sodium", "Sugar", "Alcohol", "Discretionary Foods"
                    )

                    categories.forEach { category ->
                        val score = sharedPreferences.getFloat("${selectedId}_category_${category}", 0f)
                        putFloat("category_${category}", score)
                    }

                }

                onSuccess(matchingUser)
            } else {
                onError("Invalid ID or phone number. Please try again.")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
    ) {
        Text(
            text = "Continue",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
