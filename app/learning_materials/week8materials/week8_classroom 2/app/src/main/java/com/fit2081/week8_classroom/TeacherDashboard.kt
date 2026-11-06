package com.fit2081.week8_classroom

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fit2081.week8_classroom.data.AuthManager
import com.fit2081.week8_classroom.data.quizAttempts.QuizAttempt
import com.fit2081.week8_classroom.data.quizAttempts.QuizAttemptViewModel
import com.fit2081.week8_classroom.data.students.StudentsWithAverageMark
import com.fit2081.week8_classroom.data.students.Student
import com.fit2081.week8_classroom.data.students.StudentsViewModel
import com.fit2081.week8_classroom.ui.theme.Week8_classroomTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val TAG = "TeacherDashboard"


sealed class TeacherDashboardScreen(val route: String) {
    object AddStudent : TeacherDashboardScreen("add_student")
    object ListStudents : TeacherDashboardScreen("list_students")
    object StudentAttempts : TeacherDashboardScreen("student_attempts/{id}")
}

class TeacherDashboard : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Initialize the StudentsViewModel using ViewModelProvider with a factory
            val studentViewModel: StudentsViewModel = ViewModelProvider(
                this, StudentsViewModel.StudentsViewModelFactory(this@TeacherDashboard)
            )[StudentsViewModel::class.java]


            // Initialize the QuizAttemptViewModel using ViewModelProvider with a factory
            val quizAttemptViewModel: QuizAttemptViewModel = ViewModelProvider(
                this, QuizAttemptViewModel.QuizAttemptViewModelFactory(this@TeacherDashboard)
            )[QuizAttemptViewModel::class.java]


            val navController = rememberNavController()
            Week8_classroomTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text("Teacher Dashboard")
                            }
                        )
                    },
                    bottomBar = {
                        BottomAppBar(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary,
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                IconButton(onClick = { navController.navigate(TeacherDashboardScreen.AddStudent.route) }) {
                                    Icon(Icons.Filled.Add, contentDescription = "Add Student")
                                }
                                IconButton(onClick = { navController.navigate(TeacherDashboardScreen.ListStudents.route) }) {
                                    Icon(Icons.Filled.List, contentDescription = "List Students")
                                }
                            }
                        }
                    },
                ) { innerPadding ->
                    TeacherDashboardContent(
                        modifier = Modifier.padding(innerPadding),
                        studentViewModel = studentViewModel,
                        navController = navController,
                        quizAttemptViewModel = quizAttemptViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun TeacherDashboardContent(
    modifier: Modifier = Modifier,
    studentViewModel: StudentsViewModel,
    navController: NavHostController,
    quizAttemptViewModel: QuizAttemptViewModel
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavHostTeacher(
            navController = navController,
            studentViewModel = studentViewModel,
            quizAttemptViewModel = quizAttemptViewModel
        )
    }
}


@Composable
fun NavHostTeacher(
    navController: NavHostController,  // Navigation controller to navigate between screens
    studentViewModel: StudentsViewModel,  // ViewModel to manage student data
    quizAttemptViewModel: QuizAttemptViewModel,  // ViewModel to manage quiz attempt data
    modifier: Modifier = Modifier  // Optional modifier for customizing the layout
) {
    // Create a navigation host to manage navigation between different teacher dashboard screens
    NavHost(
        navController = navController,  // Controller that handles navigation events
        startDestination = TeacherDashboardScreen.AddStudent.route,  // Initial screen to display is AddStudent
        modifier = modifier  // Apply any layout modifiers passed to this composable
    ) {
        // Define navigation route for the Add Student screen
        composable(TeacherDashboardScreen.AddStudent.route) {
            AddStudent(studentViewModel = studentViewModel)  // Display AddStudent composable with required ViewModel
        }
        
        // Define navigation route for the List Students screen
        composable(TeacherDashboardScreen.ListStudents.route) {
            ListStudents(studentsViewModel = studentViewModel, navController)  // Display ListStudents with ViewModel and navController
        }
        
        // Define navigation route for the Student Attempts screen with dynamic ID parameter
        composable(TeacherDashboardScreen.StudentAttempts.route) {
            val studentId = it.arguments?.getString("id")  // Extract the student ID from the navigation arguments
            // Display the StudentsAttempts composable with quiz attempt data for the specific student
            StudentsAttempts(quizAttemptViewModel = quizAttemptViewModel, studentId.toString())
        }
    }
}

@Composable
fun StudentsAttempts(quizAttemptViewModel: QuizAttemptViewModel, studentId: String) {
    // Fetch all quiz attempts for the specified student ID and collect them as a state
    // that updates when the underlying flow emits new data.
    // The initial value is an empty list to avoid null states.
    val quizAttempts by quizAttemptViewModel.getQuizAttemptByStudentId(studentId).collectAsStateWithLifecycle(emptyList())

    // Create a scrollable lazy column to display the list of quiz attempts
    // with horizontal and vertical padding for better visual spacing
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Generate list items dynamically based on the number of quiz attempts
        // Each item in the list is a QuizItem composable that displays a single quiz attempt
        items(quizAttempts.size) { index ->
            QuizItem(quizAttempt = quizAttempts[index])
        }
    }
}

@Composable
fun QuizItem(quizAttempt: QuizAttempt){
    // Create a Material Design card to display quiz attempt information
    // The card has a consistent appearance and provides visual separation between list items
    Card(
        modifier = Modifier
            .fillMaxWidth()     // Make the card fill the available width
            .padding(vertical = 4.dp),  // Add vertical spacing between cards
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),  // Add subtle shadow for depth
    ){
        // Arrange quiz attempt details in a horizontal row with equal spacing
        Row(
            modifier = Modifier
                .fillMaxWidth()  // Make row fill the card width
                .padding(16.dp),  // Add internal padding for content
            horizontalArrangement = Arrangement.SpaceBetween,  // Distribute items evenly across the row
            verticalAlignment = Alignment.CenterVertically  // Center items vertically
        ) {
            Text(text = quizAttempt.quizId)  // Display quiz identifier
            Text(text = quizAttempt.quizDate)  // Display quiz date
            Text(text = String.format("%.2f", quizAttempt.finalMark), fontSize = 16.sp)  // Display formatted mark with 2 decimal places
        }
    }
}

@Composable
fun AddStudent(studentViewModel: StudentsViewModel) {
    // State variables to track input field values, using remember to preserve state across recompositions
    var studentId by remember { mutableStateOf("") }     // Tracks student ID input
    var studentName by remember { mutableStateOf("") }   // Tracks student name input
    var studentPassword by remember { mutableStateOf("") }  // Tracks student password input

    // Form layout with centered alignment for input fields and submit button
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Input field for student ID with label
        OutlinedTextField(
            value = studentId,
            onValueChange = { studentId = it },  // Update state when text changes
            label = { Text("Student ID") }
        )
        
        // Input field for student name with label
        OutlinedTextField(
            value = studentName,
            onValueChange = { studentName = it },  // Update state when text changes
            label = { Text("Student Name") }
        )
        
        // Input field for student password with label
        OutlinedTextField(
            value = studentPassword,
            onValueChange = { studentPassword = it },  // Update state when text changes
            label = { Text("Student Password") }
        )
        
        // Add vertical space between input fields and button
        Spacer(modifier = Modifier.height(16.dp))
        
        // Button to submit the form and create a new student
        Button(onClick = {
            // Validate input fields are not empty before processing
            if (studentId.isNotBlank() && studentName.isNotBlank()) {
                // Get current date and time to track when the student was added
                val currentDateTime = LocalDateTime.now()
                val formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val formattedDateTime = currentDateTime.format(formatter)

                // Create new Student object with input values
                val newStudent = Student(
                    studentId = studentId,
                    studentName = studentName,
                    studentPassword = studentPassword
                )

                // Launch coroutine to insert student in background thread to avoid blocking UI
                CoroutineScope(Dispatchers.IO).launch {
                    studentViewModel.insert(newStudent)  // Insert student into database
                    Log.d(TAG, "Added Student: $newStudent")  // Log success for debugging
                    
                    // Reset input fields after successful addition
                    studentId = ""
                    studentName = ""
                }
            }
        }) {
            Text("Add Student")  // Button label
        }
    }
}


@Composable
fun ListStudents(studentsViewModel: StudentsViewModel, navController: NavHostController) {
    // Retrieve list of students with their average marks using Flow and collect as state
    // The list automatically updates when the underlying database changes
    // An empty list is provided as initial value to avoid null states
    val students by studentsViewModel.getStudentsWithAverageMarks()
        .collectAsStateWithLifecycle(emptyList())
    
    // Create a scrollable lazy column to display the student list
    // with appropriate padding around the content
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Generate list items dynamically based on the number of students
        // Each item in the list is a StudentItem composable representing a single student
        items(students.size) { index ->
            StudentItem(student = students[index], navController)
        }
    }
}

@Composable
fun StudentItem(student: StudentsWithAverageMark, navController: NavHostController) {
    // Create a clickable Material Design card for displaying student information
    // The card acts as a container that visually groups related content
    Card(
        modifier = Modifier
            .fillMaxWidth()  // Make the card fill the available width
            .padding(vertical = 4.dp),  // Add vertical spacing between cards
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),  // Add subtle shadow for depth
        onClick = {
            // Navigate to the StudentAttempts screen when card is clicked
            // Replace the placeholder {id} with the actual studentId in the route
            navController.navigate(TeacherDashboardScreen.StudentAttempts.route.replace("{id}", student.studentId))
        }
    ) {
        // Arrange student details in a horizontal row with equal spacing
        Row(
            modifier = Modifier
                .fillMaxWidth()  // Make row fill the card width
                .padding(16.dp),  // Add internal padding for content
            horizontalArrangement = Arrangement.SpaceBetween,  // Distribute items evenly across the row
            verticalAlignment = Alignment.CenterVertically  // Center items vertically
        ) {
            Text(text = student.studentName)  // Display student name
            Text(text = student.studentId)    // Display student ID
            Text(text = String.format("%.2f", student.averageMark), fontSize = 16.sp)  // Display formatted average mark with 2 decimal places
        }
    }
}

