package com.fit2081.week10_auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.fit2081.week10_auth.ui.theme.Week10_authTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // Initialize Credential Manager
        credentialManager = CredentialManager.create(this)
        
        // Check if already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already signed in, navigate to MainActivity
            navigateToMainActivity()
            return
        }
        
        setContent {
            Week10_authTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        onEmailPasswordLogin = { email, password -> 
                            loginWithEmailPassword(email, password)
                        },
                        onSignUp = { email, password ->
                            signUpWithEmailPassword(email, password)
                        },

                    )
                }
            }
        }
    }
    
    private fun loginWithEmailPassword(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            showToast("Email and password cannot be empty")
            return
        }
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, navigate to MainActivity
                    navigateToMainActivity()
                } else {
                    // If sign in fails, display a message to the user
                    showToast("Authentication failed: ${task.exception?.message}")
                }
            }
    }
    
    private fun signUpWithEmailPassword(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            showToast("Email and password cannot be empty")
            return
        }
        
        if (password.length < 6) {
            showToast("Password should be at least 6 characters")
            return
        }
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, sign in and navigate to MainActivity
                    showToast("Account created successfully")
                    navigateToMainActivity()
                } else {
                    // If sign up fails, display a message to the user
                    showToast("Sign up failed: ${task.exception?.message}")
                }
            }
    }
    
    private fun loginWithGoogle() {
        lifecycleScope.launch {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId("project-944555265112") // Replace with your web client ID from Google Cloud Console
                .build()
                
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
                
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                showToast("Google Sign-In failed: ${e.message}")
            }
        }
    }
    
    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Parse the credential
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        
                        // Get ID token
                        val idToken = googleIdTokenCredential.idToken
                        
                        // Send ID token to Firebase
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, navigate to MainActivity
                                    navigateToMainActivity()
                                } else {
                                    // If sign in fails, display a message to the user
                                    showToast("Firebase Authentication failed: ${task.exception?.message}")
                                }
                            }
                    } catch (e: GoogleIdTokenParsingException) {
                        showToast("Error parsing Google ID token: ${e.message}")
                    }
                } else {
                    showToast("Unsupported credential type")
                }
            }
            else -> {
                showToast("Unsupported credential type")
            }
        }
    }
    
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onEmailPasswordLogin: (email: String, password: String) -> Unit,
    onSignUp: (email: String, password: String) -> Unit,
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isSignUp) "Create Account" else "Login",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                if (isSignUp) {
                    onSignUp(email, password)
                } else {
                    onEmailPasswordLogin(email, password)
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(if (isSignUp) "Sign Up" else "Login with Email")
        }

        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isSignUp) "Already have an account? " else "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium
            )
            
            TextButton(onClick = { isSignUp = !isSignUp }) {
                Text(if (isSignUp) "Login" else "Sign Up")
            }
        }
    }
}