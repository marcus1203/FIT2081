package com.fit2081.week8_classroom.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fit2081.week8_classroom.data.quizAttempts.QuizAttempt
import com.fit2081.week8_classroom.data.quizAttempts.QuizAttemptDao
import com.fit2081.week8_classroom.data.students.Student
import com.fit2081.week8_classroom.data.students.StudentDao
@Database(entities = [Student::class, QuizAttempt::class], version = 1, exportSchema = false)
/**
 * Abstract class representing the college database.
 * It extends RoomDatabase and provides access to the DAO interfaces for the entities.
 */
abstract class CollegeDatabase : RoomDatabase() {
    /**
     * Provides access to the StudentDao interface for performing
     * database operations on Student entities.
     * @return StudentDao instance.
     */
    abstract fun studentDao(): StudentDao

    /**
     * Provides access to the QuizAttemptDao interface for
     * performing database operations on QuizAttempt entities.
     * @return QuizAttemptDao instance.
     */
    abstract fun quizAttemptDao(): QuizAttemptDao

    companion object {
        // Singleton instance of the database
        @Volatile
        private var Instance: CollegeDatabase? = null

        /**
         * Retrieves the singleton instance of the database.
         * If an instance already exists, it returns the existing
         * instance. Otherwise, it creates a new instance of the database.
         * @param context The context of the application.
         * @return The singleton instance of CollegeDatabase.
         */
        fun getDatabase(context: Context): CollegeDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CollegeDatabase::class.java, "item_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}


