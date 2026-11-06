package com.fit2081.week10_auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fit2081.week10_auth.ui.theme.Week10_authTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // Check if user is signed in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // User is not logged in, redirect to LoginActivity
            navigateToLogin()
            return
        }
        
        setContent {
            Week10_authTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold { innerPadding ->
                        UserProfile(
                            displayName = currentUser.displayName ?: "User",
                            email = currentUser.email ?: "No email available",
                            onLogout = { logout() },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
    
    private fun logout() {
        auth.signOut()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        navigateToLogin()
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close MainActivity
    }
}

@Composable
fun UserProfile(
    displayName: String,
    email: String,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome, $displayName!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "You have successfully authenticated with Firebase!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}