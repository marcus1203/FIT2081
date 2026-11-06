package com.fit2081.week8_classroom.data.quizAttempts

import android.content.Context
import com.fit2081.week8_classroom.data.CollegeDatabase
import kotlinx.coroutines.flow.Flow

class QuizAttemptsRepository(context: Context) {
    // Create an instance of the QuizAttempt DAO
    private val quizAttemptDao =
        CollegeDatabase.getDatabase(context).quizAttemptDao()

    /**
     * Retrieve all quiz attempts from the database.
     * @return A Flow emitting a list of all quiz attempts.
     */
    val allAttempts: Flow<List<QuizAttempt>> = quizAttemptDao.getAllQuizAttempt()

    /**
     * Insert a new quiz attempt into the database.
     * @param attempt The QuizAttempt object to be inserted.
     */
    suspend fun insert(attempt: QuizAttempt) {
        quizAttemptDao.insert(attempt)
    }

    /**
     * Retrieve quiz attempts for a specific student from the database.
     * @param studentId The ID of the student.
     * @return A Flow emitting a list of quiz attempts for the specified student.
     */
    fun getQuizAttemptByStudentId(studentId: String): Flow<List<QuizAttempt>> =
        quizAttemptDao.getQuizAttemptByStudentId(studentId)
}

