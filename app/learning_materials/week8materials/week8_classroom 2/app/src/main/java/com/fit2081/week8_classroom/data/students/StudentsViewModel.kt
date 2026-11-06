package com.fit2081.week8_classroom.data.students

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.week8_classroom.data.students.StudentsWithAverageMark
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StudentsViewModel(context: Context) : ViewModel() {

    val repository = StudentsRepository(context = context)

    /**
     * Retrieves a Flow of all students.
     *
     * This Flow emits a list of [Student] objects, representing all students in the database.
     * It updates whenever the student data changes in the database.
     *
     * @return A Flow of a list of [Student] objects.
     */
    val allStudents: Flow<List<Student>> = repository.getAllStudents()

    /**
     * Inserts a new student into the database.
     *
     * This is a suspend function and should be called within
     * a coroutine or another suspend function.
     *
     * @param student The [Student] object to be inserted into the database.
     */
    suspend fun insert(student: Student) = repository.insertStudent(student)

    /**
     * Retrieves a student from the database by their ID.
     *
     * This is a suspend function and should be called within
     * a coroutine or another suspend function.
     *
     * @param studentId The ID of the student to retrieve.
     * @return The [Student] object with the specified ID, or null if no such student exists.
     */
    suspend fun getStudentById(studentId: String): Student {
        return repository.getStudentById(studentId)
    }

    /** Retrieves a Flow of students with their average marks.
     *  @return A flow of list of [StudentsWithAverageMark] objects*/
    fun getStudentsWithAverageMarks(): Flow<List<StudentsWithAverageMark>>
    = repository.getStudentsWithAverageMarks()


    // Factory class for creating instances of StudentsViewModel
    class StudentsViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            StudentsViewModel(context) as T
    }
}
