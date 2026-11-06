package com.fit20812025.yongzhan_34537821

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit20812025.yongzhan_34537821.ui.theme.Yongzhan_34537821Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Yongzhan_34537821Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    WelcomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Intent to open Monash Nutrition Clinic URL
    val monashClinicIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition"))

    // Intent to navigate to Login Screen
    val loginIntent = Intent(context, LoginScreen::class.java)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // App Name and Logo Section
        AppHeader()

        // Disclaimer Section
        DisclaimerSection(onClinicClick = { context.startActivity(monashClinicIntent) })

        // Login Button
        LoginButton(onLoginClick = { context.startActivity(loginIntent) })

        // Student Name and ID
        StudentFooter()
    }
}

@Composable
fun AppHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "NutriTrack",
            style = TextStyle(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.nutritrack_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(180.dp)
        )
    }
}

@Composable
fun DisclaimerSection(onClinicClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "This app provides general health and nutrition information for educational purposes only. It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional before making any changes to your diet, exercise, or health regimen.",
            style = TextStyle(
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Clickable link to Monash Nutrition Clinic
        Text(
            text = "Visit Monash Nutrition/Dietetics Clinic",
            style = TextStyle(
                fontSize = 14.sp,
                color = Color(0xFF3F51B5),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier
                .padding(bottom = 24.dp)
                .clickable { onClinicClick() }
        )
    }
}

@Composable
fun LoginButton(onLoginClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
        onClick = { onLoginClick() }
    ) {
        Text(
            text = "Login",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StudentFooter() {
    Text(
        text = "Designed by Yong Zhan Siow (34537821)",
        style = TextStyle(
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        ),
        modifier = Modifier.padding(vertical = 24.dp)
    )
}