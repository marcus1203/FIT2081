package com.fit20812025.yongzhan_34537821

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class InsightsScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(
                bottomBar = {
                    CommonBottomBar(currentScreen = "Insights")
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                InsightsContent(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

fun getUserDataFromCsv(context: Context, userId: String): Map<String, Float>? {
    val inputStream = context.assets.open("user_data.csv")
    val lines = inputStream.bufferedReader().readLines()
    val headers = lines[0].split(",")
    val userIndex = headers.indexOf("User_ID")
    val sexIndex = headers.indexOf("Sex")

    // Find the row corresponding to the user
    val userData = lines.drop(1).find { it.split(",")[userIndex] == userId }
    if (userData != null) {
        val values = userData.split(",")
        val sex = values[sexIndex]

        return mapOf(
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
    }
    return null
}

fun safeGetScore(headers: List<String>, values: List<String>, keyPrefix: String, sex: String): Float {
    val columnName = "$keyPrefix${if (sex == "Male") "Male" else "Female"}"
    val index = headers.indexOf(columnName)
    if (index == -1) {
        Log.e("CSVParser", "Column not found: $columnName")
        return 0f // Return 0f for missing columns
    }
    val value = values[index].toFloatOrNull() ?: 0f
    return value
}

@Composable
fun InsightsContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val userId = getUserId(context)
    val userData = remember { getUserDataFromCsv(context, userId) }

    if (userData == null) {
        Text(
            text = "No data found for user: $userId",
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    val categoryScores = remember {
        listOf(
            "Vegetables" to (userData["Vegetables"]?.toFloat() ?: 0f),
            "Fruits" to (userData["Fruits"]?.toFloat() ?: 0f),
            "Grains & Cereals" to (userData["Grains & Cereals"]?.toFloat() ?: 0f),
            "Whole Grains" to (userData["Whole Grains"]?.toFloat() ?: 0f),
            "Meat & Alternatives" to (userData["Meat & Alternatives"]?.toFloat() ?: 0f),
            "Dairy" to (userData["Dairy"]?.toFloat() ?: 0f),
            "Water" to (userData["Water"]?.toFloat() ?: 0f),
            "Unsaturated Fats" to (userData["Unsaturated Fats"]?.toFloat() ?: 0f),
            "Saturated Fats" to (userData["Saturated Fats"]?.toFloat() ?: 0f),
            "Sodium" to (userData["Sodium"]?.toFloat() ?: 0f),
            "Sugar" to (userData["Sugar"]?.toFloat() ?: 0f),
            "Alcohol" to (userData["Alcohol"]?.toFloat() ?: 0f),
            "Discretionary Foods" to (userData["Discretionary Foods"]?.toFloat() ?: 0f)
        )
    }

    // Cap scores at their respective maximums (5 or 10)
    val cappedCategoryScores = categoryScores.map { (category, score) ->
        val maxScore = when (category) {
            "Grains & Cereals", "Whole Grains", "Water", "Alcohol", "Saturated Fats", "Unsaturated Fats" -> 5f
            else -> 10f
        }
        category to score.coerceAtMost(maxScore)
    }

    // Adjust total score to a maximum of 100 if necessary
    val totalScore = cappedCategoryScores.sumOf { it.second.toDouble() }.toFloat()
    val adjustedCategoryScores = if (totalScore > 100) {
        val adjustmentFactor = 100 / totalScore
        cappedCategoryScores.map { (category, score) ->
            category to (score * adjustmentFactor) // Keep as Float
        }
    } else {
        cappedCategoryScores.map { (category, score) ->
            category to score.toFloat() // Convert to Float
        }
    }

    // Format total score to two decimal places
    val formattedTotalScore = "%.2f".format(adjustedCategoryScores.sumOf { it.second.toDouble() })

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Insights: Food Score",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Display progress bars for each category dynamically.
        adjustedCategoryScores.forEach { (category, score) ->
            val maxScore = when (category) {
                "Grains & Cereals", "Whole Grains", "Water", "Alcohol", "Unsaturated Fats", "Saturated Fats" -> 5
                else -> 10
            }
            CategoryProgressBar(category, score, maxScore) // Pass score as Float
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            // Total Food Quality Score section.
            Text(
                text = "Total Food Quality Score",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = (adjustedCategoryScores.sumOf { it.second.toDouble() } / 100f).toFloat(),
                    color = Color(0xFF6200EE),
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .padding(horizontal = 10.dp),
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = "$formattedTotalScore/100",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(65.dp)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Buttons: Share and Improve Diet.
            Button(
                onClick = {
                    // Create the share intent
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Hi, my total food quality score is ${"%.2f".format(adjustedCategoryScores.sumOf { it.second.toDouble() })}/100"
                        )
                    }
                    // Launch the chooser dialog
                    context.startActivity(Intent.createChooser(shareIntent, "Share your Food Quality Score via"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                modifier = Modifier.fillMaxWidth().wrapContentSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ios_share),
                    contentDescription = "Share",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Share with someone",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(4.dp, 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        "Improve my diet feature coming soon!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                modifier = Modifier.fillMaxWidth().wrapContentSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rocket_launch),
                    contentDescription = "Improve my Diet",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Improve my diet!",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(4.dp, 4.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryProgressBar(category: String, score: Float, maxScore: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = category,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(150.dp)
        )
        LinearProgressIndicator(
            progress = score / maxScore,
            color = Color(0xFF6200EE),
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .padding(horizontal = 10.dp),
            strokeCap = StrokeCap.Round
        )
        Text(
            text = "${"%.2f".format(score)}/$maxScore",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(55.dp)
        )
    }
}
