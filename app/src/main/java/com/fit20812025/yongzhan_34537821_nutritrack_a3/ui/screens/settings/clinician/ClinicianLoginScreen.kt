package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.settings.clinician

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicianLoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var key by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val purple = Color(0xFF6002E5)

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.Default.KeyboardArrowLeft,
                contentDescription = "Back to Settings",
                tint = Color.Gray,
                modifier = Modifier.padding(end = 0.dp)
            )
        }
        Text(
            "Back to Settings",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(0.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Clinician Login",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        OutlinedTextField(
            value = key,
            onValueChange = { key = it; error = false },
            label = { Text("Clinician Key") },
            placeholder = { Text("Enter your clinician key") },
            isError = error,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        if (error) {
            Text(
                "Invalid key. Please try again.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                if (key == "dollar-entry-apples") {
                    error = false
                    onLoginSuccess()
                } else {
                    error = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = purple),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = "Login action",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Login")
        }
    }
}
