// Package declaration for the animation examples app
package com.fit2081.week10_animation

// Import Android core functionality
import android.os.Bundle
// Import Jetpack Compose Activity components
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
// Import Compose Animation libraries for various animation types
import androidx.compose.animation.*
import androidx.compose.animation.core.*
// Import foundation components for UI basics
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
// Import Material Design 3 components
import androidx.compose.material3.*
// Import Compose runtime components for state management
import androidx.compose.runtime.*
// Import UI utilities and modifiers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
// Import Preview functionality for design-time preview
import androidx.compose.ui.tooling.preview.Preview
// Import measurement units
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import app theme
import com.fit2081.week10_animation.ui.theme.Week10_animationTheme

/**
 * Main Activity class that serves as the entry point for the animation examples app
 * This class demonstrates various animation techniques available in Jetpack Compose
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created
     * Sets up the Compose UI through setContent
     * @param savedInstanceState Bundle containing the activity's previously saved state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Apply the app theme to all composables
            Week10_animationTheme {
                // Create a full-screen surface with the theme's background color
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Display the main screen containing all animation demos
                    AnimationDemoScreen()
                }
            }
        }
    }
}



/**
 * Main composable function that displays all animation examples in a scrollable column
 * This screen serves as the container for all individual animation demos
 */
@Composable
fun AnimationDemoScreen() {
    // Create a scroll state to enable vertical scrolling through demos
    val scrollState = rememberScrollState()

    // Main column container for all animation demos
    Column(
        modifier = Modifier
            .fillMaxSize() // Use the entire available screen space
            .verticalScroll(scrollState) // Enable vertical scrolling for content
            .padding(16.dp), // Add padding around all content
        verticalArrangement = Arrangement.spacedBy(24.dp) // Add vertical space between each demo
    ) {
        // Title for the animation examples screen
        Text(
            text = "FIT2081-Animation Examples",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Display all animation demo components
        FadeAnimationDemo()

        // Color Animation Example
        ColorChangeAnimationDemo()

        // Size Animation Example
        SizeAnimationDemo()

        // Spring Animation Example
        SpringAnimationDemo()

        // Content Animation Example
        ContentAnimationDemo()

        // Infinite Animation Example
        InfiniteAnimationDemo()

        // Layout Change Animation Example
        LayoutChangeAnimationDemo()

        // Add extra space at the bottom for better scrolling experience
        Spacer(modifier = Modifier.height(32.dp))
    }
}



/**
 * Demonstrates fade in/out animation using AnimatedVisibility
 * This example shows how to make UI elements appear and disappear with smooth transitions
 */
@Composable
fun FadeAnimationDemo() {
    // State to track visibility of the animated element
    var visible by remember { mutableStateOf(true) }

    Column {
        // Section title
        Text("Fade In/Out Animation", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // Button to toggle visibility state
        Button(onClick = { visible = !visible }) {
            Text(if (visible) "Hide" else "Show")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AnimatedVisibility controls the fade animation based on the visible state
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1000)), // 1 second fade in animation
            exit = fadeOut(animationSpec = tween(1000))  // 1 second fade out animation
        ) {
            // Blue box that will fade in/out
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Blue)
            )
        }
    }
}


/**
 * Demonstrates color transition animation using animateColorAsState
 * This example shows how to smoothly transition between two colors
 */
@Composable
fun ColorChangeAnimationDemo() {
    // State to track color change state
    var colorChanged by remember { mutableStateOf(false) }

    // Animated color state that transitions between Cyan and Magenta
    val backgroundColor by animateColorAsState(
        targetValue = if (colorChanged) Color.Magenta else Color.Cyan, // Target color based on state
        animationSpec = tween(1000), // 1 second color transition animation
        label = "colorAnimation" // Label for debugging
    )

    Column {
        // Section title
        Text("Color Change Animation", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // Box that changes color when clicked
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp)) // Rounded corners
                .background(backgroundColor) // Apply the animated color
                .clickable { colorChanged = !colorChanged } // Toggle color on click
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Instruction text
        Text("Tap to change color", fontSize = 12.sp)
    }
}


/**
 * Demonstrates size animation using animateDpAsState with spring physics
 * This example shows how to animate the size of a UI element with a bouncy effect
 */
@Composable
fun SizeAnimationDemo() {
    // State to track expanded/contracted status
    var expanded by remember { mutableStateOf(false) }

    // Animate size with spring physics for a bouncy effect
    val size by animateDpAsState(
        targetValue = if (expanded) 150.dp else 100.dp, // Toggle between two sizes
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, // Controls bounciness
            stiffness = Spring.StiffnessLow // Controls animation speed
        ),
        label = "sizeAnimation" // Label for debugging
    )

    Column {
        // Section title
        Text("Size Animation with Spring", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // Box that changes size with animation when clicked
        Box(
            modifier = Modifier
                .size(size) // Apply the animated size
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Green)
                .clickable { expanded = !expanded } // Toggle size on click
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Instruction text
        Text("Tap to resize", fontSize = 12.sp)
    }
}



/**
 * Demonstrates spring physics animation with offsetX
 * This example shows how to animate position using spring physics for a natural feel
 */
@Composable
fun SpringAnimationDemo() {
    // State to control animation start/reset
    var isAnimating by remember { mutableStateOf(false) }

    // Define spring configuration parameters
    val springStiff = Spring.StiffnessMedium
    val springDamping = Spring.DampingRatioMediumBouncy

    // Animate horizontal offset using spring physics
    val offsetX by animateFloatAsState(
        targetValue = if (isAnimating) 150f else 0f, // Move box right when animating
        animationSpec = spring(
            dampingRatio = springDamping, // Controls bounciness
            stiffness = springStiff   // Controls speed and resistance
        ),
        label = "springAnimation" // Label for debugging
    )

    Column {
//        Text("Spring Physics Animation", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // Button to trigger animation
        Button(onClick = { isAnimating = !isAnimating }) {
            Text(if (isAnimating) "Reset" else "Animate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Container for the animated box
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
        ) {
            // Box that will move horizontally with spring animation
            Box(
                modifier = Modifier
                    .offset(x = offsetX.dp) // Apply the animated offset
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEC407A))
            ) {
                // Text inside the animated box
                Text(
                    "Spring",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp)
                )
            }
        }

        // Description of the spring physics parameters
        Text(
            "Spring physics: DampingRatio=${springDamping}, Stiffness=${springStiff}",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

/**
 * Demonstrates content transition animation when switching between pages
 * Uses ExperimentalAnimationApi for AnimatedContent
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ContentAnimationDemo() {
    // State to track current page
    var currentPage by remember { mutableStateOf(0) }

    Column {
        // Section title
        Text("Content Change Animation", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // Navigation buttons for page switching
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Page 1 button
            Button(onClick = { currentPage = 0 }) {
                Text("Page 1")
            }

            // Page 2 button
            Button(onClick = { currentPage = 1 }) {
                Text("Page 2")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AnimatedContent handles smooth transitions between different content
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                // Slide in from right or left depending on navigation direction
                slideInHorizontally(animationSpec = tween(300)) { fullWidth ->
                    fullWidth * (if (targetState > initialState) 1 else -1)
                } with
                // Slide out in the opposite direction
                slideOutHorizontally(animationSpec = tween(300)) { fullWidth ->
                    -fullWidth * (if (targetState > initialState) 1 else -1)
                }
            },
            label = "contentAnimation" // Label for debugging
        ) { page ->
            // Display different content based on current page
            when (page) {
                // Page 1 content - red card
                0 -> Card(
                    modifier = Modifier.size(200.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE57373))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Page 1 Content")
                    }
                }
                // Page 2 content - green card
                1 -> Card(
                    modifier = Modifier.size(200.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF81C784))
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Page 2 Content")
                    }
                }
            }
        }
    }
}


/**
 * Demonstrates continuous animations that run indefinitely using InfiniteTransition
 * Shows how to create animations that continue running without user interaction
 */
@Composable
fun InfiniteAnimationDemo() {
    // Create an infinite transition object that manages ongoing animations
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    // Continuous rotation animation (0 to 360 degrees)
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing), // 2 second linear rotation
            repeatMode = RepeatMode.Restart // Start from beginning after each cycle
        ),
        label = "rotation" // Label for debugging
    )

    // Continuous pulsing animation (scaling between 0.8 and 1.2)
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000), // 1 second transition
            repeatMode = RepeatMode.Reverse // Reverse direction after each cycle
        ),
        label = "scale" // Label for debugging
    )

    Column {
        // Section title
        Text("Infinite Animation", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // Container for centered animation
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Box that rotates and pulses indefinitely
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        rotationZ = rotation // Apply rotation animation
                        scaleX = scale      // Apply horizontal scale animation
                        scaleY = scale      // Apply vertical scale animation
                    }
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFA000)) // Orange/amber color
            )
        }
    }
}


/**
 * Demonstrates layout size changes with smooth animation using animateContentSize
 * Shows how to animate the container size when content expands or collapses
 */
@Composable
fun LayoutChangeAnimationDemo() {
    // State to track expanded/collapsed status
    var expanded by remember { mutableStateOf(false) }

    Column {
        // Section title
        Text("Layout Change Animation", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // Card that animates its size when content changes
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize( // Animates size changes when content changes
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy, // Controls bounciness
                        stiffness = Spring.StiffnessLow // Controls animation speed
                    )
                )
                .clickable { expanded = !expanded }, // Toggle expanded state on click
            colors = CardDefaults.cardColors(containerColor = Color(0xFF90CAF9)) // Light blue color
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Card title
                Text(
                    text = "Animate Layout Changes",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Conditional content that appears/disappears with animation
                if (expanded) {
                    // Additional text that appears when expanded
                    Text(
                        text = "This is an example of animating layout changes with animateContentSize modifier. " +
                                "This paragraph appears and disappears with a smooth animation that adjusts the card's size. " +
                                "Click anywhere on this card to toggle the expanded state.",
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Buttons that appear when expanded
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // First action button
                        Button(
                            onClick = { }, // No action defined
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0))
                        ) {
                            Text("Action 1")
                        }

                        // Second action button
                        Button(
                            onClick = { }, // No action defined
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0))
                        ) {
                            Text("Action 2")
                        }
                    }
                } else {
                    // Simple instruction text when collapsed
                    Text("Tap to expand", fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * Preview function for the animation demo screen
 * Allows viewing the UI in Android Studio's preview pane without running the app
 * The showBackground=true parameter ensures the UI is displayed with a background
 */
@Preview(showBackground = true)
@Composable
fun AnimationPreview() {
    Week10_animationTheme {
        AnimationDemoScreen()
    }
}
