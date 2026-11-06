package com.fit20812025.yongzhan_34537821

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fit20812025.yongzhan_34537821.ui.theme.Yongzhan_34537821Theme

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val userId = getUserId(this)
        val sharedPreferences = getSharedPreferences("user_prefs_$userId", Context.MODE_PRIVATE)
        val sex = sharedPreferences.getString("sex", "Male") ?: "Male"
        val foodScore = if (sex == "Male") {
            sharedPreferences.getFloat("foodScoreMale", 0f)
        } else {
            sharedPreferences.getFloat("foodScoreFemale", 0f)
        }
        setContent {
            Yongzhan_34537821Theme {
                HomeScreenWithBottomBar(userName = userId, foodScore = foodScore)
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    userName: String,
    foodScore: Float
) {
    val context = LocalContext.current

    // Top section with greeting and edit button
    Column(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top
    ){
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

        // This Row places the text and button side by side
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
                    context.startActivity(Intent(context, FoodIntakeQuestionnaire::class.java))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
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
                    modifier = Modifier.clickable {
                        // Navigate to Insights Screen (Screen 6)
                        context.startActivity(Intent(context, InsightsScreen::class.java))
                    }
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
                // Card content remains the same
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
                            modifier = Modifier.size(40.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

        //Add a horizontal line to separate the sections
        HorizontalDivider(
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

@Composable
fun BottomBarItem(
    icon: Any,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onClick)
    ) {
        IconButton(onClick = onClick) {
            when (icon) {
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) Color(0xFF6200EE) else Color.Gray
                )
                is Painter -> Icon(
                    painter = icon,
                    contentDescription = label,
                    tint = if (isSelected) Color(0xFF6200EE) else Color.Gray
                )
            }
        }
        Text(
            text = label,
            color = if (isSelected) Color(0xFF6200EE) else Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun CommonBottomBar(currentScreen: String) {
    val context = LocalContext.current

    BottomAppBar(
        modifier = Modifier.height(60.dp),
        containerColor = Color.White,
        contentColor = Color(0xFF6200EE)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomBarItem(
                icon = Icons.Filled.Home,
                label = "Home",
                isSelected = currentScreen == "Home",
                onClick = {
                    if (currentScreen != "Home") {
                        context.startActivity(Intent(context, HomeScreen::class.java))
                    }
                }
            )

            BottomBarItem(
                icon = painterResource(id = R.drawable.insights),
                label = "Insights",
                isSelected = currentScreen == "Insights",
                onClick = {
                    if (currentScreen != "Insights") {
                        context.startActivity(Intent(context, InsightsScreen::class.java))
                    }
                }
            )

            BottomBarItem(
                icon = Icons.Filled.Person,
                label = "NutriCoach",
                isSelected = false,
                onClick = { /* Not implemented yet */ }
            )

            BottomBarItem(
                icon = Icons.Filled.Settings,
                label = "Settings",
                isSelected = false,
                onClick = { /* Not implemented yet */ }
            )
        }
    }
}


@Composable
fun HomeScreenWithBottomBar(userName: String, foodScore: Float) {
    Scaffold(
        bottomBar = {
            CommonBottomBar(currentScreen = "Home")
        }
    ) { innerPadding ->
        HomeScreenContent(
            modifier = Modifier.padding(innerPadding),
            userName = userName,
            foodScore = foodScore
        )
    }
}
