package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.FoodIntake
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.NutriCoachTip
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.Patient
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.FoodIntakeQuestionnaire

@Database(
    entities = [Patient::class, FoodIntake::class, NutriCoachTip::class, FoodIntakeQuestionnaire::class],
    version = 3, // <<<< INCREMENTED VERSION NUMBER
    exportSchema = false // It's good practice to set this explicitly
)
abstract class NutriTrackDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun foodIntakeDao(): FoodIntakeDao
    abstract fun nutriCoachTipDao(): NutriCoachTipDao
    abstract fun foodIntakeQuestionnaireDao(): FoodIntakeQuestionnaireDao

    companion object {
        @Volatile private var INSTANCE: NutriTrackDatabase? = null

        fun getInstance(context: Context): NutriTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NutriTrackDatabase::class.java, "nutritrack-db"
                )
                    .fallbackToDestructiveMigration() // This will clear the db on version change without migration
                    .build().also { INSTANCE = it }
            }
        }
    }
}
