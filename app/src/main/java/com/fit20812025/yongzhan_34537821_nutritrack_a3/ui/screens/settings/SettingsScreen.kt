package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.settings

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.PatientViewModel

@Composable
fun SettingsScreen(
    patientViewModel: PatientViewModel,
    onLogout: () -> Unit,
    onNavigateToClinicianLogin: () -> Unit,
    onNavigateBack: () -> Unit // Added for consistency
) {
    val patient by patientViewModel.patient.observeAsState()
    val name = patient?.name ?: "-"
    val phone = patient?.phoneNumber ?: "-"
    val userId = patient?.userId ?: "-"
    val iconColor = Color.Black
    val sectionHeaderColor = Color.Gray
    val iconSize = 30.dp
    val iconPadding = 8.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, // Original weight
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "ACCOUNT",
            color = sectionHeaderColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Individual setting rows
        SettingRowItemOriginal(icon = Icons.Default.Person, label = "User Name", value = name, iconColor = iconColor, iconSize = iconSize, iconPadding = iconPadding)
        Spacer(modifier = Modifier.height(16.dp))
        SettingRowItemOriginal(icon = Icons.Default.Phone, label = "Phone Number", value = phone, iconColor = iconColor, iconSize = iconSize, iconPadding = iconPadding)
        Spacer(modifier = Modifier.height(16.dp))
        SettingRowItemOriginal(icon = Icons.Default.AccountBox, label = "User ID", value = userId, iconColor = iconColor, iconSize = iconSize, iconPadding = iconPadding)

        Spacer(modifier = Modifier.height(16.dp))
        Divider() // Original divider
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "OTHER SETTINGS",
            color = sectionHeaderColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Logout button
        Button(
            onClick = { // Original combined logic for logout and navigation
                Log.d("SettingsScreen", "Logout button clicked. Navigating to login screen.")
                onLogout() // This should trigger MainActivity to change isAuthenticated
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Clinician Login button
        Button(
            onClick = onNavigateToClinicianLogin,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Person, contentDescription = "Clinician Login", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clinician Login", color = Color.White)
        }
    }
}

@Composable
fun SettingRowItemOriginal(icon: ImageVector, label: String, value: String, iconColor: Color, iconSize: Dp, iconPadding: Dp) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
        Icon(
            icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier
                .size(iconSize)
                .padding(end = iconPadding)
        )
        Spacer(modifier = Modifier.padding(horizontal = 16.dp))
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}
