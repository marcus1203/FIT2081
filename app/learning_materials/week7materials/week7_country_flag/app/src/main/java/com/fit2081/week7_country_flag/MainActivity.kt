package com.fit2081.week7_country_flag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.fit2081.week7_country_flag.ui.theme.Week7_country_flagTheme












class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Week7_country_flagTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CountryFlagScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryFlagScreen(modifier: Modifier = Modifier) {
    var countryCode by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = countryCode,
            onValueChange = { countryCode = it },
            label = { Text("Country Code") },
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = { imageUrl = "https://flagcdn.com/w640/$countryCode.jpg" },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Get Flag")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if(imageUrl.isNotEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Country Flag",
                modifier = Modifier.size(200.dp)
            )
        }





    }
}
