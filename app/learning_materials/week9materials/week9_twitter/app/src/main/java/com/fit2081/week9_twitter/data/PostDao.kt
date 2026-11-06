package com.fit2081.week9_twitter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.Random
import java.util.UUID
import kotlin.random.Random as KotlinRandom

/**
 * Data Access Object (DAO) for the Post entity.
 * Provides methods to access and manipulate posts in the database.
 */
@Dao
interface PostDao {
    /**
     * Get all posts from the database, ordered by creation time (newest first)
     */
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<Post>>

    /**
     * Insert a new post into the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post): Long

    /**
     * Insert multiple posts into the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>): List<Long>

    /**
     * Delete all posts from the database
     */
    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
    
    /**
     * Creates a random post with dummy data
     */

}

