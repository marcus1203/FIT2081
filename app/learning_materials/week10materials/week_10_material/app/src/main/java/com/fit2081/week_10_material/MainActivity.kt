/**
 * This Android application demonstrates Material Design 3 principles implemented with Jetpack Compose.
 * The MainActivity showcases a comprehensive collection of modern UI components including cards, buttons,
 * text fields, chips, sliders, switches, and dialogs - all following Material Design guidelines.
 * It features a responsive layout with a navigation drawer, top app bar, bottom navigation bar, and
 * floating action button to illustrate proper component hierarchy. The UI elements are organized into
 * distinct sections within a scrollable LazyColumn, each with interactive state management to demonstrate
 * real-world usage patterns. This example serves as an educational reference for implementing 
 * Material Design components in Android applications using declarative UI paradigms.
 */
package com.fit2081.week_10_material

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit2081.week_10_material.ui.theme.Week_10_materialTheme
import kotlinx.coroutines.launch

/**
 * Main entry point for the Material Design showcase application.
 * This activity initializes the Jetpack Compose UI and applies the app's theme.
 */
class MainActivity : ComponentActivity() {
    /**
     * Initializes the activity, enables edge-to-edge display, and sets up the Compose content.
     * 
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display to use the full screen area
        enableEdgeToEdge()
        // Set the Compose content with the application theme
        setContent {
            Week_10_materialTheme {
                // Display the main Material Design showcase interface
                MaterialDesignShowcase()
            }
        }
    }
}

/**
 * Main composable function that showcases various Material Design 3 components.
 * Uses ExperimentalMaterial3Api since some components are still in experimental phase.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDesignShowcase() {
    // Initialize drawer state (closed by default)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    // Create a coroutine scope for launching operations like opening/closing the drawer
    val scope = rememberCoroutineScope()
    // Track the currently selected bottom navigation tab
    var selectedTab by remember { mutableStateOf(0) }
    // Define the available tabs for bottom navigation
    val tabs = listOf("Components", "Inputs", "Dialogs")

    // State variables for UI components
    // Controls the visibility of the demonstration dialog
    var showDialog by remember { mutableStateOf(false) }
    // Value for the non-password text field
    var textFieldValue by remember { mutableStateOf("") }
    // Value for the password text field
    var passwordValue by remember { mutableStateOf("") }
    // Value for the slider component (0-10 range)
    var sliderValue by remember { mutableStateOf(0f) }
    // State of the toggle switch (on/off)
    var switchChecked by remember { mutableStateOf(false) }

    // Display an AlertDialog when showDialog is true
    if (showDialog) {
        AlertDialog(
            // Close dialog when clicking outside
            onDismissRequest = { showDialog = false },
            // Dialog title component
            title = { Text("Material Dialog") },
            // Main content text of the dialog
            text = { Text("This is a Material 3 dialog example. " +
                    "Dialogs inform users about tasks and can contain " +
                    "critical information or require decisions.") },
            // Primary action button
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Confirm")
                }
            },
            // Secondary action button
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }

    // Navigation drawer layout that slides in from the side
    ModalNavigationDrawer(
        // Connect to the drawer state to control open/closed status
        drawerState = drawerState,
        // Define the content that appears in the drawer panel
        drawerContent = {
            ModalDrawerSheet {
                // Add spacing at the top of drawer
                Spacer(modifier = Modifier.height(16.dp))
                // Drawer header text
                Text(
                    "Material Design Demo",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineMedium
                )
                // Separator line between header and navigation items
                Divider()
                // Home navigation item (selected by default)
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
                )
                // Settings navigation item
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }
                )
                // Help navigation item
                NavigationDrawerItem(
                    label = { Text("Help") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Help") }
                )
            }
        },
    ) {
        // Main application scaffold that provides the app structure
        Scaffold(
            // Make scaffold fill the entire screen
            modifier = Modifier.fillMaxSize(),
            // Top app bar containing title and action icons
            topBar = {
                TopAppBar(
                    title = { Text("Material Design in Compose") },
                    // Menu button that opens the navigation drawer
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    // Action buttons displayed on the right side of the top bar
                    actions = {
                        // Search action button
                        IconButton(onClick = { /* Search action */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        // More options button
                        IconButton(onClick = { /* More action */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                    }
                )
            },
            // Floating action button displayed on top of the content
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* fab click */ },
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
            // Bottom navigation bar with tabs
            bottomBar = {
                NavigationBar {
                    // Create a navigation item for each tab
                    tabs.forEachIndexed { index, title ->
                        NavigationBarItem(
                            // Icon for the navigation item (different for each tab)
                            icon = { 
                                Icon(
                                    when (index) {
                                        0 -> Icons.Default.Menu
                                        1 -> Icons.Default.Edit
                                        else -> Icons.Default.Home
                                    },
                                    contentDescription = title
                                )
                            },
                            // Text label shown below the icon
                            label = { Text(title) },
                            // Highlight the currently selected tab
                            selected = selectedTab == index,
                            // Update selected tab when clicked
                            onClick = { selectedTab = index }
                        )
                    }
                }
            }
        ) { innerPadding ->
            // Main scrollable content area using LazyColumn for efficient scrolling
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    // Apply padding from the scaffold to avoid overlapping with app bars
                    .padding(innerPadding)
                    // Additional padding for content
                    .padding(16.dp),
                // Space between items in the list
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main heading for the showcase
                item {
                    Text(
                        "Material Design Components",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                // === CARDS SECTION ===
                item {
                    // Section header for cards
                    Text(
                        "Cards",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    // Elevated Card with shadow effect
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        // Apply medium elevation shadow (6dp)
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        // Card content arranged in a vertical column
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Card title
                            Text(
                                "Elevated Card",
                                style = MaterialTheme.typography.titleMedium
                            )
                            // Spacing between title and content
                            Spacer(modifier = Modifier.height(8.dp))
                            // Card description text
                            Text(
                                "Cards contain content and actions about a single subject. They're a convenient way to present information.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    // Spacing between cards
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Outlined Card with border instead of elevation
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Card content arranged in a vertical column
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Card title
                            Text(
                                "Outlined Card",
                                style = MaterialTheme.typography.titleMedium
                            )
                            // Spacing between title and content
                            Spacer(modifier = Modifier.height(8.dp))
                            // Card description text
                            Text(
                                "Cards can be outlined instead of elevated, showing a border instead of a shadow.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // === BUTTONS SECTION ===
                item {
                    // Section header for buttons
                    Text(
                        "Buttons",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    // First row of buttons with different styles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        // Add space between buttons
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Standard filled button with high emphasis
                        Button(onClick = { /* Filled button click */ }) {
                            Text("Filled")
                        }
                        
                        // Outlined button with medium emphasis
                        OutlinedButton(onClick = { /* Outlined button click */ }) {
                            Text("Outlined")
                        }
                        
                        // Text-only button with low emphasis
                        TextButton(onClick = { /* Text button click */ }) {
                            Text("Text")
                        }
                    }
                    
                    // Space between button rows
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Second row of buttons with additional styles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        // Add space between buttons
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Filled tonal button (softer filled style)
                        FilledTonalButton(onClick = { /* Filled tonal button click */ }) {
                            Text("Filled Tonal")
                        }
                        
                        // Elevated button with shadow effect
                        ElevatedButton(onClick = { /* Elevated button click */ }) {
                            Text("Elevated")
                        }
                    }
                }

                // === TEXT FIELDS SECTION ===
                item {
                    // Section header for text input fields
                    Text(
                        "Text Fields",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    // Outlined text field for name input
                    OutlinedTextField(
                        // Bind to the text state variable
                        value = textFieldValue,
                        // Update state when text changes
                        onValueChange = { textFieldValue = it },
                        // Floating label text
                        label = { Text("Name") },
                        // Icon at the beginning of the text field
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Person Icon") },
                        // Make the field full width
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Space between text fields
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Filled text field for password input
                    TextField(
                        // Bind to the password state variable
                        value = passwordValue,
                        // Update state when text changes
                        onValueChange = { passwordValue = it },
                        // Floating label text
                        label = { Text("Password") },
                        // Hide the actual password characters
                        visualTransformation = PasswordVisualTransformation(),
                        // Show password keyboard with secure input
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        // Icon at the beginning of the text field
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon") },
                        // Make the field full width
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // === CHIPS SECTION ===
                item {
                    // Section header for chips
                    Text(
                        "Chips",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    // Row container for displaying chips horizontally
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        // Add space between chips
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Suggestion chip - for displaying suggestions to the user
                        SuggestionChip(
                            onClick = { /* Chip click */ },
                            label = { Text("Suggestion") }
                        )
                        
                        // Filter chip - for filtering content, can be selected/unselected
                        FilterChip(
                            // This chip is in selected state
                            selected = true,
                            onClick = { /* Filter chip click */ },
                            label = { Text("Filter") }
                        )
                        
                        // Assist chip - for initiating actions with an icon
                        AssistChip(
                            onClick = { /* Assist chip click */ },
                            label = { Text("Assist") },
                            // Icon to display next to text
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Info,
                                    contentDescription = "Info",
                                    // Set icon size to be smaller
                                    Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }

                // === SLIDERS AND SWITCHES SECTION ===
                item {
                    // Section header for interactive controls
                    Text(
                        "Sliders & Switches",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Column {
                        // Display the current slider value with 1 decimal place
                        Text(
                            "Slider value: ${String.format("%.1f", sliderValue)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        // Continuous slider from 0 to 10
                        Slider(
                            // Bind to slider state value
                            value = sliderValue,
                            // Update state when slider position changes
                            onValueChange = { sliderValue = it },
                            // Set the minimum and maximum values
                            valueRange = 0f..10f,
                            // Make slider full width
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Space between slider and switch
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Row for switch and its label
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Toggle switch component
                            Switch(
                                // Bind to switch state
                                checked = switchChecked,
                                // Update state when switch is toggled
                                onCheckedChange = { switchChecked = it }
                            )
                            
                            // Space between switch and label
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // Label describing the switch function
                            Text("Enable feature")
                        }
                    }
                }

                // === DIALOG DEMONSTRATION SECTION ===
                item {
                    // Section header for dialogs
                    Text(
                        "Dialogs",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    // Button that triggers the dialog display
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Show Alert Dialog")
                    }
                }
                
                // === SURFACES SECTION ===
                item {
                    // Section header for surface demonstrations
                    Text(
                        "Surfaces",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    // Primary colored surface with text
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        // Use the theme's primary color
                        color = MaterialTheme.colorScheme.primary,
                        // Use the on-primary color for content (ensuring contrast)
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        // Apply rounded corners to the surface
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        // Center the text in the surface
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Primary Surface",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    
                    // Space between surfaces
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Secondary colored surface with text
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        // Use the theme's secondary color
                        color = MaterialTheme.colorScheme.secondary,
                        // Use the on-secondary color for content (ensuring contrast)
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        // Apply rounded corners to the surface
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        // Center the text in the surface
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Secondary Surface",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Creates a preview of the MaterialDesignShowcase composable.
 * This function is used by Android Studio to display a preview of the UI during development.
 */
@Preview(showBackground = true)
@Composable
fun MaterialDesignShowcasePreview() {
    Week_10_materialTheme {
        MaterialDesignShowcase()
    }
}

