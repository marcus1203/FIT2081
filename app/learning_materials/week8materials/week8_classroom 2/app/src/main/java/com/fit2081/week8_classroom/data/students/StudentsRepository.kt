package com.fit2081.week8_classroom.data.students

import android.content.Context
import com.fit2081.week8_classroom.data.CollegeDatabase
import com.fit2081.week8_classroom.data.students.StudentsWithAverageMark
import kotlinx.coroutines.flow.Flow

class StudentsRepository(context: Context) {

    // Get the StudentDao instance from the database
    private val studentsDao = CollegeDatabase.getDatabase(context).studentDao()

    /**
     * Inserts a new student into the database.
     * @param student The student object to be inserted.
     */
    suspend fun insertStudent(student: Student) {
        studentsDao.insert(student)
    }

    /**
     * Retrieves a student from the database by their ID.
     * @param studentId The ID of the student to retrieve.
     * @return The student object with the given ID.
     */
    suspend fun getStudentById(studentId: String): Student {
        return studentsDao.getStudentById(studentId)
    }

    /**
     * Retrieves all students from the database as a Flow.
     * This allows observing changes to the student list over time.
     * @return A Flow emitting a list of all students.
     */
    fun getAllStudents(): Flow<List<Student>> = studentsDao.getAll()

    /**
     * Retrieves students along with their average marks as a Flow.
     * This allows observing changes to the student list and their average marks over time.
     * @return A Flow emitting a list of StudentsWithAverageMark objects.
     *         Each StudentsWithAverageMark object contains student information and their average mark.
     */
    fun getStudentsWithAverageMarks(): Flow<List<StudentsWithAverageMark>> =
        studentsDao.getStudentsWithAverageMarks()

}
