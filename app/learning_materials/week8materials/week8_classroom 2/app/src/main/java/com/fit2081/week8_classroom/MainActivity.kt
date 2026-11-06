package com.fit2081.week8_classroom

import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.week8_classroom.data.AuthManager
import com.fit2081.week8_classroom.data.students.Student
import com.fit2081.week8_classroom.data.students.StudentsViewModel


import com.fit2081.week8_classroom.ui.theme.Week8_classroomTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking


sealed class MainActivityScreen(val route: String) {
    object StaffLogin : MainActivityScreen("staff_login")
    object StudentLogin : MainActivityScreen("student_login")
}


class MainActivity : ComponentActivity() {
    val _context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Initialize the StudentsViewModel using ViewModelProvider with a factory pattern
            // This allows the ViewModel to survive configuration changes and maintain state
            val studentViewModel: StudentsViewModel = ViewModelProvider(
                this, StudentsViewModel.StudentsViewModelFactory(this@MainActivity)
            )[StudentsViewModel::class.java]
            
            // Apply the app's theme to ensure consistent styling across the UI
            Week8_classroomTheme {
                // Scaffold provides the basic material design visual layout structure
                // It fills the entire screen and handles the app's main content area
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Display the MainScreen composable, passing the necessary padding from the Scaffold
                    // and the studentViewModel for data access
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        studentViewModel = studentViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, studentViewModel: StudentsViewModel) {
    // Create a navigation controller to handle navigation between different screens
    val navController = rememberNavController()
    
    // Main container that organizes the UI vertically and fills the screen
    Column(modifier = modifier.fillMaxSize()) {
        // Top section containing the login option buttons
        // Takes up half the screen space (weight = 1f)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,  // Center content horizontally
            verticalArrangement = Arrangement.Top               // Arrange content from top to bottom
        ) {

            Spacer(modifier = Modifier.height(20.dp))  // Add vertical space for visual separation


            // Staff login button - navigates to the staff login screen when clicked
            Button(
                onClick = {
                    navController.navigate(MainActivityScreen.StaffLogin.route)
                },
                modifier = Modifier.fillMaxWidth(0.8f)  // Button takes up 80% of screen width
            ) {
                Text(
                    text = "Staff Login",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(20.dp))  // Add vertical space for visual separation

            // Student login button - navigates to the student login screen when clicked
            Button(
                onClick = {
                    navController.navigate(MainActivityScreen.StudentLogin.route)
                },
                modifier = Modifier.fillMaxWidth(0.8f)  // Button takes up 80% of screen width
            ) {
                Text(
                    text = "Student Login",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

        }
        // Bottom section containing the navigation host
        // Takes up the remaining screen space (weight = 1f)
        NavHostSection(
            navController = navController,
            studentViewModel = studentViewModel,
            modifier = Modifier.weight(1f)
        )
    }

}


@Composable
fun NavHostSection(
    navController: NavHostController,  // Navigation controller to handle screen transitions
    studentViewModel: StudentsViewModel,  // ViewModel to access student data 
    modifier: Modifier = Modifier  // Optional modifier for customizing layout
) {
    // Set up navigation host to manage different login screens
    NavHost(
        navController = navController,  // Controller that handles navigation events
        startDestination = MainActivityScreen.StaffLogin.route,  // Initial screen is staff login
        modifier = modifier  // Apply any modifiers passed to this composable
    ) {
        // Define the Staff Login screen destination
        composable(MainActivityScreen.StaffLogin.route) {
            StaffLoginScreen()  // Display the staff login interface
        }
        // Define the Student Login screen destination
        composable(MainActivityScreen.StudentLogin.route) {
            StudentLoginScreen(studentsViewModel = studentViewModel)  // Display student login with required ViewModel
        }
    }
}

@Composable
fun StudentLoginScreen(studentsViewModel: StudentsViewModel = viewModel()) {
    // State variables to track and remember user input across recompositions
    val studentId = androidx.compose.runtime.remember { mutableStateOf("") }  // Stores student ID input
    val password = androidx.compose.runtime.remember { mutableStateOf("") }  // Stores password input securely
    var isLoggedIn = remember { mutableStateOf(false) }  // Tracks authentication status
    val _context = LocalContext.current  // Access to the current Android context for Toast and Intent

    // Main container for login form with centered alignment
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Login screen title with appropriate typography and spacing
        Text(
            text = "Student Login",
            modifier = Modifier.padding(bottom = 24.dp),
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )

        // Input field for student ID with label
        TextField(
            value = studentId.value,
            onValueChange = { studentId.value = it },  // Update state when text changes
            label = { Text("Student ID") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Password input field with secure visual transformation (shows dots instead of characters)
        TextField(
            value = password.value,
            onValueChange = { password.value = it },  // Update state when password changes
            label = { Text("Password") }, 
            visualTransformation = PasswordVisualTransformation(),  // Hide password characters
            modifier = Modifier.fillMaxWidth()
        )

        val context = LocalContext.current
        val rememberusername = remember { studentId.value }
        val rememberpassword = remember { password.value }
        
        // Login button that triggers authentication
        Button(onClick = {
            // Verify credentials against database
            isLoggedIn.value = isAuthorized(studentId.value, password.value, studentsViewModel)
            
            if (isLoggedIn.value) {
                // If login successful, store student ID in auth manager for session tracking
                AuthManager.login(studentId.value)

                // Show success message with student ID
                Toast.makeText(context, "Login Successful ${studentId.value}", Toast.LENGTH_SHORT)
                    .show()
                    
                // Create and start the Student Dashboard activity
                val intent = Intent(context, StudentsDashboard::class.java)
                context.startActivity(intent)

            } else {
                // Show error message for failed authentication
                Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }

        }, modifier = Modifier.padding(top = 24.dp)) {
            Text("Login")  // Button label
        }
    }
}

/**
 * Validates student credentials against the database
 * @param studentId The student ID to authenticate
 * @param password The password to validate
 * @param studentsViewModel ViewModel to access student data
 * @return Boolean indicating whether authentication was successful
 */
fun isAuthorized(
    studentId: String,
    password: String,
    studentsViewModel: StudentsViewModel
): Boolean {
    var allStudents: Flow<List<Student>> = studentsViewModel.allStudents  // Get access to all students (unused)
    var aStudent: Student
    
    // Use blocking call to retrieve student by ID synchronously
    runBlocking {
        var aFlowStudent: Student = studentsViewModel.getStudentById(studentId)
        // Commented code: if(aFlowStudent==null) return false
        aStudent = aFlowStudent
    }
    
    // Authentication checks
    if (aStudent == null) return false  // Student ID doesn't exist
    if (aStudent.studentPassword != password) return false  // Password doesn't match

    return true  // Authentication successful
}

@Composable
fun StaffLoginScreen() {
    // State for username and password
    val username = androidx.compose.runtime.remember { mutableStateOf("") }
    val password = androidx.compose.runtime.remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "Staff Login",
            modifier = Modifier.padding(bottom = 24.dp),
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )

        // Username TextField
        TextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Password TextField
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        // Login Button

        val context = androidx.compose.ui.platform.LocalContext.current
        val isLoggedIn = remember { true }
        val role = remember { "staff" }

        Button(
            onClick = {
                if (username.value == "admin" && password.value == "admin") {
                    val intent = Intent(context, TeacherDashboard::class.java)
                    context.startActivity(intent)
                }
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Login")
        }
    }
}

