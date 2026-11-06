package com.fit2081.week9_twitter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Main database class for the application.
 * 
 * This abstract class provides an interface for accessing the application's database.
 * It uses Room persistence library to handle database operations and defines
 * the database configuration including entities and DAOs.
 */
@Database(entities = [Post::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Provides access to the Post Data Access Object.
     * 
     * @return PostDao instance for database operations related to posts.
     */
    abstract fun postDao(): PostDao

    /**
     * Companion object to manage database instance using the Singleton pattern.
     * This ensures only one instance of the database is created throughout the app,
     * which is an important consideration for resource management.
     */
    companion object {
        /**
         * Volatile annotation ensures the INSTANCE is always up-to-date
         * and the same for all execution threads.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Gets the singleton database instance, creating it if it doesn't exist.
         * 
         * @param context The application context needed to create the database.
         * @return The singleton AppDatabase instance.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tweets_database"
                )
                .build()
                // Assign the newly created instance to INSTANCE
                INSTANCE = instance
                instance // Return the instance
            }
        }
    }
}



