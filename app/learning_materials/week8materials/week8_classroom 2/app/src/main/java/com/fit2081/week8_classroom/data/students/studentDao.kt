package com.fit2081.week8_classroom.data.students

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fit2081.week8_classroom.data.students.StudentsWithAverageMark
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    /**
     * Inserts a new student into the database.
     *
     * @param student The [Student] object to be inserted.
     */
    @Insert
    suspend fun insert(student: Student)

    /**
     * Retrieves a student from the database based on their ID.
     *
     * @param studentId The ID of the student to retrieve.
     * @return The [Student] object if found,
     * null otherwise (handled by Room as per suspend function).
     */
    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentById(studentId: String): Student

    /**
     * Retrieves all students from the database.
     *
     * @return A [Flow] emitting a list of all [Student]
     * objects in the database.  The Flow will emit
     * a new list whenever the underlying data changes.
     */
    @Query("SELECT * FROM students")
    fun getAll(): Flow<List<Student>>

    /**
     * Retrieves a list of students along with their
     * average quiz marks.
     *
     * This query performs an inner join between the `students`
     * and `quiz_attempts` tables,
     * calculates the average `finalMark` for each student,
     * and groups the results by student ID.
     *
     * @return A [Flow] emitting a list of [StudentsWithAverageMark] objects.
     * Each object contains the
     * student's ID, name, and average quiz mark.
     * The Flow will emit a new list whenever the underlying data changes.
     */
    @Query("SELECT students.studentId, students.studentName, AVG(quiz_attempts.finalMark) " +
            "as averageMark FROM students Inner Join quiz_attempts " +
            "ON students.studentId = quiz_attempts.studentId Group By students.studentId")
    fun getStudentsWithAverageMarks(): Flow<List<StudentsWithAverageMark>>
}



