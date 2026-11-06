package com.fit2081.week9_twitter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fit2081.week9_twitter.data.Post
import com.fit2081.week9_twitter.data.PostViewModel
import com.fit2081.week9_twitter.ui.theme.Week9_twitterTheme
import kotlinx.coroutines.delay

/**
 * Main activity for the Twitter Clone application.
 * This class serves as the entry point of the application and hosts the main UI components
 * built with Jetpack Compose.
 *
 * The app follows MVVM architecture pattern:
 * - View: MainActivity and its composables (MainScreen, ListTweets, PostCard)
 * - ViewModel: PostViewModel that handles business logic and data operations
 * - Model: Post entity and repository (handled through PostViewModel)
 */
class MainActivity : ComponentActivity() {
    /**
     * ViewModel instance that handles data operations and UI state
     * Using by viewModels() delegates the lifecycle management to the activity
     */
    private val postViewModel: PostViewModel by viewModels()
    
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display (immersive mode)
        enableEdgeToEdge()
        // Set up the Compose UI
        setContent {
            // Apply the application theme
            Week9_twitterTheme {
                // Display the main screen with the ViewModel instance
                MainScreen(postViewModel)
            }
        }
    }
}

/**
 * Main screen composable that sets up the app scaffold with top bar and content area.
 * 
 * Features:
 * - Top app bar with network status indicator
 * - Refresh button to manually update posts
 * - Delete button to remove all posts (with confirmation)
 * - Add button to create new random posts
 * - Content area displaying the list of tweets
 *
 * @param postViewModel The ViewModel that provides data and handles user actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(postViewModel: PostViewModel) {
    // State to track network connection status
    var isOnline by remember { mutableStateOf(false) }
    // State to control delete confirmation dialog
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    
    // Check network status periodically (every 5 seconds)
    // this could be improved with a more efficient approach such as using
    // a BroadcastReceiver that listens for network changes
    LaunchedEffect(Unit) {
        while (true) {
            isOnline = postViewModel.isNetworkAvailable()
            delay(5000)
        }
    }
    
    // Main scaffold that provides the app structure
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Twitter Clone")
                        Spacer(modifier = Modifier.width(8.dp))
                        // Network status badge (green for online, red for offline)
                        Surface(
                            color = if (isOnline) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.error,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = if (isOnline) "Online" else "Offline",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                actions = {
                    // Delete all posts button (triggers confirmation dialog)
                    IconButton(onClick = { showDeleteConfirmationDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete All Posts"
                        )
                    }
                    // Manually refresh posts from source
                    IconButton(onClick = { postViewModel.refreshPosts() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Posts"
                        )
                    }
                    // Create a new random post
                    Button(
                        onClick = { postViewModel.generateRandomPost() },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add",
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        // Content area displaying the list of tweets
        ListTweets(
            viewModel = postViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
    
    // Confirmation dialog for deleting all posts (shown conditionally)
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("Delete All Tweets") },
            text = { Text("Are you sure you want to delete all tweets? This action cannot be undone.") },
            confirmButton = {
                // Delete button (red to indicate destructive action)
                Button(
                    onClick = {
                        postViewModel.deleteAllPosts()
                        showDeleteConfirmationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                // Cancel button
                OutlinedButton(
                    onClick = { showDeleteConfirmationDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Composable that displays the list of tweets from the ViewModel.
 * 
 * Features:
 * - Shows a list of tweets in a scrollable column when available
 * - Displays an empty state message when no tweets exist
 * - Each tweet is represented as a card
 *
 * @param viewModel The ViewModel that provides the tweet data
 * @param modifier Modifier for customizing the layout
 */
@Composable
fun ListTweets(viewModel: PostViewModel, modifier: Modifier = Modifier) {
    // Trigger posts refresh when component is composed
    viewModel.refreshPosts()
    
    // Collect posts from viewModel.allPosts flow using collectAsState
    val posts by viewModel.allPosts.collectAsState(initial = emptyList())
    
    if (posts.isEmpty()) {
        // Empty state: Display a message when no posts are available
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No tweets available",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Click 'Add Tweet' to create one",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else {
        // List state: Display all posts in a scrollable LazyColumn
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts) { post ->
                PostCard(post)
            }
        }
    }
}

/**
 * Composable card that displays a single tweet/post.
 * 
 * Features:
 * - Shows the username and creation time
 * - Displays the subject in a highlighted style
 * - Shows the content with ellipsis for long text
 * - Applies consistent styling using Material Design
 *
 * @param post The Post entity containing the data to display
 */
@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row: Username and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.userName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = post.createdAt,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subject line (highlighted with primary color)
            Text(
                text = post.subject,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            // Main content with ellipsis for long text
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

