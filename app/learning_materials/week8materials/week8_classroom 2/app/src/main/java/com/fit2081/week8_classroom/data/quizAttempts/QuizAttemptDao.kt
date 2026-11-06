package com.fit2081.week8_classroom.data.quizAttempts

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
/**
 * Data Access Object (DAO) for interacting with the
 * quiz_attempts table in the database.

 */
interface QuizAttemptDao {
    /**
     * Inserts a new [QuizAttempt] into the database.
     *
     * @param quizAttempt The [QuizAttempt] object to be inserted.
     */
    @Insert
    suspend fun insert(quizAttempt: QuizAttempt)

    /**
     * Retrieves all [QuizAttempt]s from the database as a [Flow] of lists.
     *
     * @return A [Flow] emitting a list of all [QuizAttempt]s in the table.
     */
    @Query("SELECT * FROM quiz_attempts")
    fun getAllQuizAttempt(): Flow<List<QuizAttempt>>

    /**
     * Retrieves all [QuizAttempt]s for a specific student ID.
     */
    @Query("Select * FROM quiz_attempts WHERE studentId = :studentId")
    fun getQuizAttemptByStudentId(studentId: String): Flow<List<QuizAttempt>>
}


