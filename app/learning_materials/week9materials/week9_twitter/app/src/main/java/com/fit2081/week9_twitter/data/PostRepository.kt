package com.fit2081.week9_twitter.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID


/**
 * The repository implements the offline-first architecture pattern, where:
 * - Data is primarily stored and accessed from the local database
 * - When online, data is fetched from the remote API and cached locally
 * - When offline, data is served from the local database
 * This approach ensures the application remains functional regardless of network connectivity.
 */
class PostRepository(private val applicationContext: Context) {


        // Get database instance and create repository
        val database = AppDatabase.getDatabase(applicationContext)
        val postDao = database.postDao()


    /**
     * Retrofit instance configured for API communication
     * - Uses the base URL for the tweets API service
     * - Configures Gson converter for JSON parsing
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://34.129.121.193:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * API service interface implementation created by Retrofit
     * This is used to make network requests to the remote API
     */
    private val apiService = retrofit.create(TweetApiService::class.java)

    /**
     * Fetches all posts from either the network or local database.
     *
     * Implementation details:
     * - Checks for network availability first
     * - If online, attempts to fetch from API and updates local database
     * - If API call fails or offline, falls back to local database
     * - In worst-case scenarios, returns an empty list to avoid null values
     *
     * @return A List of Post objects representing all available posts
     */
    suspend fun getAllPosts(): List<Post> {
        return try {
            if (isNetworkAvailable()) {
                try {
                    // Fetch from API if online
                    val apiPosts = withContext(Dispatchers.IO) {
                        apiService.getTweets()
                    }
                    // Convert API response to Post entities and insert into database
                    val posts = apiPosts.map { apiPost ->
                        Post(
                            postId = apiPost.postId ?: "",
                            userName = apiPost.userName ?: "Unknown User",
                            subject = apiPost.subject ?: "No Subject",
                            content = apiPost.content ?: "",
                            createdAt = apiPost.createdAt?:""
                        )
                    }
                    // Save API posts to local database for offline access
                    if (posts.isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            posts.forEach { post ->
                                postDao.insertPost(post)
                            }
                        }
                    }
                    posts
                } catch (e: Exception) {
                    // If API call fails, fall back to local data
                    e.printStackTrace()
                    withContext(Dispatchers.IO) {
                        postDao.getAllPosts().first()
                    }
                }
            } else {
                // If offline, return local data
                withContext(Dispatchers.IO) {
                    postDao.getAllPosts().first()
                }
            }
        } catch (e: Exception) {
            // If everything fails, at least return an empty list
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Creates a new post, either by calling the remote API or locally.
     *
     * Implementation details:
     * - If online, calls the API endpoint to generate a new post
     * - If offline, creates a random post locally
     * - Updates the local database in both scenarios
     * - After creating the post, refreshes the posts list
     *
     * @return Boolean indicating if post creation was successful
     */
    suspend fun createPost(): Boolean {
        return try {
            if (isNetworkAvailable()) {
                // Make API call to create a post on the server
                val apiResponse = withContext(Dispatchers.IO) {
                    apiService.createTweet()
                }
                // Refresh posts to include the newly created one
                getAllPosts()
                return true
            } else {
                // If offline, create a local post with random content
                val randomPost = createLocalPost()
                postDao.insertPost(randomPost)
                // Refresh posts to include the newly created one
                getAllPosts()
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Deletes all posts from the local database.
     *
     * Note: This operation only affects local data and doesn't delete
     * posts from the remote API server.
     */
    suspend fun deleteAllPosts() {
        withContext(Dispatchers.IO) {
            postDao.deleteAllPosts()
        }
    }

    /**
     * Creates a random post with generated content for offline use.
     *
     * This helper method generates:
     * - A random username from predefined options
     * - A subject title
     * - Content based on content templates
     * - A formatted date-time string for the current time
     *
     * @return A new Post object with randomly generated data
     */
    private fun createLocalPost(): Post {
        // Define date formatters for consistent formatting
        val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
        val now = LocalDateTime.now()
        // Format the current date-time
        val formatDateTime = now.format(formatter)

        // Sample data collections for random generation
        val userNames = listOf(
            "John Smith", "Emma Wilson", "Michael Brown",
            "Sophia Johnson", "Robert Davis", "Olivia Jones"
        )
        val subjects = listOf("Local Post")
        val contentTemplates = listOf(
            "Just had an amazing experience with %s!",
            "Does anyone else think %s is overrated?",
            "I can't believe what's happening in %s right now",
            "Looking for recommendations about %s",
            "My thoughts on %s: absolutely fascinating!",
            "Today I learned something new about %s"
        )

        // Generate the post content
        val selectedSubject = subjects.random()
        val content = contentTemplates.random().format(selectedSubject.lowercase())

        // Create and return the post with a unique ID
        return Post(
            postId = UUID.randomUUID().toString(),
            userName = userNames.random(),
            subject = selectedSubject,
            content = content,
            createdAt = formatDateTime
        )
    }

    /**
     * Checks if the device has an active internet connection.
     *
     * Implementation details:
     * - Obtains the ConnectivityManager system service
     * - Queries for active network capabilities
     * - Checks if Wi-Fi, cellular, or ethernet transport is available
     *
     * @return true if the device is connected to the internet, false otherwise
     */
    fun isNetworkAvailable(): Boolean {
        // Get the ConnectivityManager system service
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // Check if the device has an active network
        val network = connectivityManager.activeNetwork ?: return false
        // Get the network capabilities for the active network
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        // Check if the network has any of the following transports:
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}

/**
 * Retrofit service interface that defines API endpoints for tweet operations.
 *
 * This interface is used by Retrofit to generate a concrete implementation
 * for making HTTP requests to the remote API server.
 */
interface TweetApiService {
    /**
     * GET endpoint to retrieve all tweets from the server.
     *
     * @return List<Post> containing all posts from the API
     */
    @GET("api/tweets")
    suspend fun getTweets(): List<Post>

    /**
     * POST endpoint to create a new tweet on the server.
     *
     * @return Post the newly created post returned by the API
     */
    @POST("api/tweets/new")
    suspend fun createTweet(): Post
}



