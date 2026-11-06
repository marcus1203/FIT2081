package com.fit2081.week7_currancy_exchange

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit2081.week7_currancy_exchange.data.repository.RatesRepository
import com.fit2081.week7_currancy_exchange.ui.theme.Week7_currancy_exchangeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Week7_currancy_exchangeTheme {
                CurrencyExchangeApp()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyExchangeApp() {
    var repository: RatesRepository= RatesRepository()
    var baseCurrency by remember { mutableStateOf("") }
    var targetCurrency by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Currency Exchange") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = baseCurrency,
                onValueChange = { baseCurrency = it },
                label = { Text("Base Currency") }
            )
            OutlinedTextField(
                value = targetCurrency,
                onValueChange = { targetCurrency = it },
                label = { Text("Target Currency") }
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") }
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        val rate = repository.getRate(baseCurrency, targetCurrency)?.rates[targetCurrency]
                        if (rate != null) {
                            result = String.format("%.2f", (amount.toDouble() * rate))
                        }
                    }
                }
            ) {
                Text("Get Rate")
            }
            if (result.isNotEmpty()) {
                Text(
                    text = "Result: $result $targetCurrency",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold


                )
            }
                }
            }
        }
