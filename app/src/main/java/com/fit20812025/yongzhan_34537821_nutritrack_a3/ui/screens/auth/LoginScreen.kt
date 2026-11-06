package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.auth // Changed from PlaceholderLoginScreen to LoginScreen to match call

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun LoginScreen(onLogin: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login Screen (placeholder)")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onLogin("1") }) {
            Text("Login as User 1")
        }
    }
}
