package com.fit2081.week8_classroom.data.students

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a student entity within the Room database.

 */
@Entity(tableName = "students")
data class Student(
    // Primary key with auto-generation
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentName: String, // Student's name
    val studentPassword: String, // Student's password
    // Student ID (optional, defaults to empty string)
    val studentId: String = ""
)
