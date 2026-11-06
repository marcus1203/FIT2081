package com.fit2081.week9_twitter.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for handling post-related UI operations and data management.
 *
 * This class extends AndroidViewModel to have access to the Application context
 * which is required for the repository. It serves as the communication center
 * between the UI (MainActivity) and the data layer (PostRepository).
 *
 * Key responsibilities:
 * - Provide observable state of posts to the UI
 * - Handle UI-triggered operations like post creation and deletion
 * - Manage data loading and refreshing operations
 * - Abstract away the complexity of threading and data fetching
 *
 * @property application The application instance provided by the Android framework
 */
class PostViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Repository instance for handling all data operations.
     * This is the single point of contact for the ViewModel to interact with data sources.
     */
    private val repository: PostRepository = PostRepository(application.applicationContext)

    /**
     * Private mutable state flow that stores the current list of posts.
     * Using StateFlow provides a way to observe changes to the data over time.
     */
    private val _allPosts = MutableStateFlow<List<Post>>(emptyList())

    /**
     * Public immutable StateFlow that exposes the current list of posts to observers.
     * This property enables the UI to react to changes in the post data while
     * preventing direct mutation from outside this class.
     */
    val allPosts: StateFlow<List<Post>>
        get() = _allPosts.asStateFlow()

    /**
     * Initialize the ViewModel by loading posts from the repository.
     * This ensures data is available as soon as the UI starts observing.
     */
    init {
        // Trigger initial data fetch on ViewModel creation
        refreshPosts()
    }

    /**
     * Generates and inserts a new random post into the database.
     *
     * This method is typically called in response to user actions like
     * pressing the "Add Tweet" button. It delegates the actual work
     * of creating the post to the repository and runs in a background thread.
     */
    fun generateRandomPost() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createPost()
        }
    }

    /**
     * Refreshes the posts list by fetching the latest data from the repository.
     *
     * This method is responsible for updating the observed StateFlow with
     * the most current data. It can be triggered manually (e.g., by pulling to refresh)
     * or automatically when data changes might have occurred.
     */
    fun refreshPosts() {
        viewModelScope.launch {
            _allPosts.value = repository.getAllPosts()
            println("Posts refreshed: ${_allPosts.value.size} posts loaded")
        }
    }

    /**
     * Checks if the device currently has an active internet connection.
     *
     * This method delegates to the repository's network checking functionality
     * and is used by the UI to display the online/offline status indicator.
     *
     * @return true if the device is connected to the internet, false otherwise
     */
    fun isNetworkAvailable(): Boolean {
        return repository.isNetworkAvailable()
    }

    /**
     * Deletes all posts from the local database.
     *
     * This is typically called in response to the user clicking the delete/bin
     * icon and confirming the deletion action. After deleting all posts,
     * it refreshes the UI to reflect the empty state.
     */
    fun deleteAllPosts() {
        viewModelScope.launch {
            repository.deleteAllPosts()
            refreshPosts() // Refresh the UI after deletion
        }
    }
}

