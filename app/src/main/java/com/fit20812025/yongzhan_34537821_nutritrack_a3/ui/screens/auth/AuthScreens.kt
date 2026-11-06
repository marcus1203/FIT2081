package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.PatientViewModel
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    patientViewModel: PatientViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToClaim: (String) -> Unit
) {
    var selectedUserId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val loginStatus by authViewModel.loginStatus.observeAsState()
    val userIds by patientViewModel.userIds.observeAsState(initial = emptyList())
    val isLoadingUserIds by patientViewModel.isLoading.observeAsState(initial = true)
    val errorMessage = remember { mutableStateOf("") }
    val currentPatient by authViewModel.currentPatient.observeAsState()

    LaunchedEffect(selectedUserId) {
        if (selectedUserId.isNotEmpty()) {
            authViewModel.loadPatientDetails(selectedUserId)
        }
    }

    LaunchedEffect(loginStatus) {
        if (loginStatus == true) {
            onLoginSuccess()
            authViewModel.resetAuthStatusFlags()
        } else if (loginStatus == false) {
            errorMessage.value = "Invalid User ID or password."
            authViewModel.resetAuthStatusFlags()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Log in",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (isLoadingUserIds) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            Text(
                text = "Loading user data...",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedUserId,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("My ID (Provided by your Clinician)", fontSize = 18.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    userIds.forEach { userId ->
                        DropdownMenuItem(
                            text = { Text(userId) },
                            onClick = {
                                selectedUserId = userId
                                expanded = false
                                errorMessage.value = ""
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage.value = "" },
                label = { Text("Password", fontSize = 18.sp) },
                placeholder = { Text("Enter your password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp)
            )

            if (errorMessage.value.isNotEmpty()) {
                Text(errorMessage.value, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            }

            Text(
                text = "This app is only for pre-registered users. Please enter your ID and password or Register to claim your account on your first visit.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Start,
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            val isAccountClaimed = currentPatient?.password != null && selectedUserId == currentPatient?.userId
            val purpleColor = Color(0xFF6200EE) // Define purple color for consistency

            Button(
                onClick = {
                    if (selectedUserId.isBlank()) {
                        errorMessage.value = "Please select your User ID."
                    } else if (password.isBlank()){
                        errorMessage.value = "Please enter your password."
                    }
                    else {
                        authViewModel.login(selectedUserId, password)
                    }
                },
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedUserId.isEmpty()) Color.Gray else purpleColor
                ),
                enabled = selectedUserId.isNotEmpty()
            ) {
                Text(
                    text = "Continue",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = {
                    if (selectedUserId.isNotBlank()) {
                        onNavigateToClaim(selectedUserId)
                    } else {
                        errorMessage.value = "Please select a User ID to register."
                    }
                },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    // MODIFIED: Use purpleColor when enabled
                    containerColor = if (selectedUserId.isEmpty() || (selectedUserId == currentPatient?.userId && isAccountClaimed)) Color.Gray else purpleColor
                ),
                enabled = selectedUserId.isNotEmpty() && !(selectedUserId == currentPatient?.userId && isAccountClaimed)
            ) {
                Text(
                    // MODIFIED: Text color to White for consistency
                    text = if (selectedUserId == currentPatient?.userId && isAccountClaimed) "Account Claimed" else "Register",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimAccountScreen(
    selectedUserId: String,
    authViewModel: AuthViewModel = viewModel(),
    onClaimSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val claimStatus by authViewModel.claimStatus.observeAsState()
    val errorMessage = remember { mutableStateOf("") }

    LaunchedEffect(claimStatus) {
        if (claimStatus == true) {
            onClaimSuccess()
            authViewModel.resetAuthStatusFlags()
        } else if (claimStatus == false) {
            errorMessage.value = "Claim failed. Ensure User ID and Phone Number match records, or account may already be claimed."
            authViewModel.resetAuthStatusFlags()
        }
    }

    val passwordMinLength = 6
    val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{${passwordMinLength},}\$".toRegex()
    val namePattern = "^[A-Z][a-zA-Z]*(?:\\s[A-Z][a-zA-Z]*)*\$".toRegex()
    val purpleColor = Color(0xFF6200EE) // Define purple color

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register Account",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = selectedUserId,
            onValueChange = {},
            readOnly = true,
            label = { Text("My ID (Provided by your Clinician)", fontSize = 18.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = false
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; errorMessage.value = "" },
            label = { Text("Full Name", fontSize = 18.sp) },
            placeholder = { Text("E.g., John Doe") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it; errorMessage.value = "" },
            label = { Text("Phone Number (Registered)", fontSize = 18.sp) },
            placeholder = { Text("Enter your registered phone number") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage.value = "" },
            label = { Text("Password", fontSize = 18.sp) },
            placeholder = { Text("Enter complex password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; errorMessage.value = "" },
            label = { Text("Confirm Password", fontSize = 18.sp) },
            placeholder = { Text("Enter your password again") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp)
        )

        if (errorMessage.value.isNotEmpty()) {
            Text(
                text = errorMessage.value,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = "Password must be at least $passwordMinLength characters, including uppercase, lowercase, a digit, and a special character.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            modifier = Modifier.padding(bottom = 16.dp, start = 4.dp, end = 4.dp)
        )


        Button(
            onClick = {
                errorMessage.value = ""
                if (name.isBlank()) {
                    errorMessage.value = "Full name cannot be empty."
                } else if (!name.matches(namePattern)) {
                    errorMessage.value = "Name must start with a capital letter, followed by letters. Each word in the name should follow this."
                } else if (phoneNumber.isBlank()){
                    errorMessage.value = "Phone number cannot be empty."
                } else if (password.isBlank()) {
                    errorMessage.value = "Password cannot be empty."
                } else if (password.length < passwordMinLength) {
                    errorMessage.value = "Password must be at least $passwordMinLength characters long."
                } else if (!password.matches(passwordPattern)) {
                    errorMessage.value = "Password not strong enough. Must include uppercase, lowercase, digit, and special character."
                }
                else if (password != confirmPassword) {
                    errorMessage.value = "Passwords do not match."
                } else {
                    authViewModel.claimAccount(selectedUserId, phoneNumber, name, password)
                }
            },
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = purpleColor) // Use purpleColor
        ) {
            Text(
                text = "Register",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Button(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = purpleColor) // Kept Teal for "Back to Login" for differentiation
        ) {
            Text(
                text = "Back to Login",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
