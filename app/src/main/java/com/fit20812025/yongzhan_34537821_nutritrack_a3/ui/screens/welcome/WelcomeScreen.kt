package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.welcome

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit20812025.yongzhan_34537821_nutritrack_a3.R
import androidx.compose.foundation.text.ClickableText

@Composable
fun WelcomeScreen(onLoginClick: () -> Unit) {
    val context = LocalContext.current
    val url = "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition"
    val disclaimerText = "This app provides general health and nutrition information for educational purposes only. It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional before making any changes to your diet, exercise, or health regimen.\nUse this app at your own risk.\nIf you'd like to an Accredited Practicing Dietitian (APD), please visit the Monash Nutrition/Dietetics Clinic (discounted rates for students): $url"

    val annotatedDisclaimer = remember {
        buildAnnotatedString {
            val linkStart = disclaimerText.indexOf(url)
            val linkEnd = linkStart + url.length
            append(disclaimerText)
            if (linkStart >= 0) {
                addStyle(
                    style = SpanStyle(
                        color = Color(0xFF3F51B5),
                        textDecoration = TextDecoration.Underline
                    ),
                    start = linkStart,
                    end = linkEnd
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = url,
                    start = linkStart,
                    end = linkEnd
                )
            }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            // App Name and Logo
            Text(
                text = "NutriTrack",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.nutritrack_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            // Disclaimer with clickable link
            ClickableText(
                text = annotatedDisclaimer,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp
                ),
                onClick = {
                    val monashClinicIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(monashClinicIntent)
                },
                modifier = Modifier.padding(horizontal = 0.dp)
            )

            Spacer(modifier = Modifier.height(70.dp))
            // Login Button
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text(
                    text = "Login",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
            // Credit at the bottom
            Text(
                text = "Designed with \uD83D\uDC96 by Yong Zhan Siow (34537821)",
                style = MaterialTheme.typography.bodySmall.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
} 