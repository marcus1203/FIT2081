package com.fit2081.week8_classroom

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import androidx.lifecycle.ViewModelProvider
import com.fit2081.week8_classroom.data.AuthManager
import com.fit2081.week8_classroom.data.quizAttempts.QuizAttempt
import kotlin.random.Random
import com.fit2081.week8_classroom.data.quizAttempts.QuizAttemptViewModel
import com.fit2081.week8_classroom.ui.theme.Week8_classroomTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentsDashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val quizAttemptViewModel: QuizAttemptViewModel = ViewModelProvider(
                this, QuizAttemptViewModel.QuizAttemptViewModelFactory(this@StudentsDashboard)
            )[QuizAttemptViewModel::class.java]
            Week8_classroomTheme {
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)) { innerPadding ->
                    AttemptQuiz(innerPadding, quizAttemptViewModel)
                }
            }
        }
    }
}

@Composable
fun AttemptQuiz(innerPadding: PaddingValues, quizAttemptViewModel: QuizAttemptViewModel) {
    // Column layout to arrange UI elements vertically with padding
    Column(modifier = Modifier.padding(innerPadding)) {
        // Mutable state variables to track checkbox states for each question
        var q1 by remember { mutableStateOf(false) }
        var q2 by remember { mutableStateOf(false) }
        var q3 by remember { mutableStateOf(false) }

        // Get the current context
        val _context = LocalContext.current

        // Display the quiz title with styling
        Text(
            text = "FIT2081 Mobile Quiz",
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        )
        // Add vertical space
        Spacer(modifier = Modifier.padding(10.dp))
        // Display instructions
        Text(text = "Select the correct answers to the following statements")
        // Add vertical space
        Spacer(modifier = Modifier.padding(20.dp))
        // Row layout for the first question with checkbox and text
        Row(
            // Align content vertically in the center
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = q1,
                onCheckedChange = { q1 = it }
            )
            // Display the first question text
            Text(text = "Paris is the capital of France.")
        }
        // Row layout for the second question with checkbox and text
        Row(
            // Align content vertically in the center
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = q2,
                onCheckedChange = { q2 = it }
            )
            // Display the second question text
            Text(text = "Vincent van Gogh painted the Mona Lisa")
        }
        // Row layout for the third question with checkbox and text
        Row(
            // Align content vertically in the center
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(checked = q3, onCheckedChange = { q3 = it })
            // Display the third question text
            Text(text = "Mount Kosciuszko is the highest mountain in Australia")
        }
        Spacer(modifier = Modifier.padding(20.dp))
        // Submit button
        Button(
            onClick = {
                // Calculate the total mark based on selected answers
                var totalMark = 0.0
                if (q1) totalMark += 1
                if (!q2) totalMark += 1
                if (q3) totalMark += 1

                // Generate a random 4-digit number for the quiz ID
                var random4digits = Random.nextInt(1000, 9999)
                // Create the quiz ID string
                var quizId = "Qz$random4digits"
                // Get the current date in dd/MM/yyyy format
                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                val currentDate = dateFormat.format(System.currentTimeMillis())
                // Create a QuizAttempt object with student ID, quiz ID, date, and final mark
                var attempt: QuizAttempt = QuizAttempt(
                    studentId = AuthManager.getStudentId().toString(),
                    quizId = quizId,
                    quizDate = currentDate,
                    finalMark = totalMark
                )
                // Launch a coroutine to insert the quiz attempt into the database
                CoroutineScope(Dispatchers.IO).launch {
                    quizAttemptViewModel.insertQuizAttempt(attempt)
                }
                // Reset the checkbox states after submission
                q1 = false
                q2 = false
                q3 = false
                Toast.makeText(_context, "Quiz Attempt Submitted", Toast.LENGTH_SHORT).show()

            }, modifier = Modifier.align(Alignment.End)
        ) {
            Text("Submit Quiz Attemp")
        }
        // Add a horizontal divider line
        HorizontalDivider()
    }
}
