package com.fit2081.week9_twitter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a post/tweet in the database.
 * This class defines the structure of a post with all its attributes.
 * It's annotated as a Room entity, which allows Room to create a database table
 * for storing post data.
 */
@Entity(tableName = "posts")
data class Post(
    /**
     * Unique identifier for this post.
     * This serves as the primary key in the database table.
     */
    @PrimaryKey
    val postId: String,
    /**
     * The name of the user who created this post.
     * Typically includes first and last name.
     */
    val userName: String,
    /**
     * The subject or title of the post.
     * Provides a brief description of what the post is about.
     */
    val subject: String,
    /**
     * The main content of the post.
     * Contains the actual message that the user wants to share.
     */
    val content: String,
    /**
     * The date and time when this post was created.
     * Stored as a string to maintain compatibility with different time formats.
     */
    val createdAt: String
)