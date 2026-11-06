package com.fit2081.week8_classroom.data.quizAttempts

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel class for managing quiz attempts data.
 * This class interacts with the QuizAttemptsRepository to perform database operations
 * and provides data to the UI.
 *
 * @param context The application context.
 */
class QuizAttemptViewModel(context: Context) : ViewModel() {

    private val quizAttemptRepository: QuizAttemptsRepository =
        QuizAttemptsRepository(context)

    /**
     * Flow of all quiz attempts.
     */
    val allAttempts: Flow<List<QuizAttempt>> = quizAttemptRepository.allAttempts;

    /**
     * Inserts a new quiz attempt into the database.
     * @param quizAttempt The quiz attempt to be inserted.
     */
    fun insertQuizAttempt(quizAttempt: QuizAttempt) {
        viewModelScope.launch { quizAttemptRepository.insert(quizAttempt) }
    }

    /**
     * Retrieves quiz attempts for a specific student ID.
     * @param studentId The ID of the student.
     * @return A Flow emitting a list of quiz attempts for the given student ID.
     */
    fun getQuizAttemptByStudentId(studentId: String):
            Flow<List<QuizAttempt>> = quizAttemptRepository.getQuizAttemptByStudentId(studentId)

    class QuizAttemptViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        // Use application context to avoid memory leaks
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            QuizAttemptViewModel(context) as T
    }
}

