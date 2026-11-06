package com.fit2081.week6_db

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import com.fit2081.week6_db.data.PatientViewModel
import com.fit2081.week6_db.ui.theme.Week5_dbTheme
import com.fit2081.week6_db.data.Patient


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Week5_dbTheme {
                val viewModel: PatientViewModel = ViewModelProvider(
                    this, PatientViewModel.PatientViewModelFactory(this@MainActivity)
                )[PatientViewModel::class.java]

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AddPatientScreen(this@MainActivity, innerPadding, viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPatientScreen(context: Context, paddingValues: PaddingValues, viewModel: PatientViewModel) {
    var patientName by remember { mutableStateOf(TextFieldValue("")) }
    var patientAge by remember { mutableStateOf(TextFieldValue("")) }
    var patientAddress by remember { mutableStateOf(TextFieldValue("")) }
    val listOfPatients by viewModel.allPatients.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Hospital Management",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = patientName,
            onValueChange = { patientName = it },
            label = { Text("Patient Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = patientAge,
            onValueChange = { patientAge = it },
            label = { Text("Patient Age") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = patientAddress,
            onValueChange = { patientAddress = it },
            label = { Text("Patient Address") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Button(onClick = {

                viewModel.insert(
                    Patient(
                        name = patientName.text,
                        age = patientAge.text.toInt(),
                        address = patientAddress.text
                    )
                )
            }) {
                Text("Add Patient")
            }


            Button(
                onClick = { viewModel.deleteAllPatients() }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) { Text(text = "Delete All Patients") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(thickness = 4.dp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Makes LazyColumn take up remaining space
        ) {
            //iterates through the list of patients and creates a card for each patient
            items(listOfPatients) { patient ->

                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(100.dp)
                        .weight(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //First column to display patient details
                        Column(modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)) {
                            Text(text = "Name: ${patient.name}", fontWeight = FontWeight.Bold)
                            Text(text = "Age: ${patient.age}")
                            Text(text = "Address: ${patient.address}")
                        }
                        //Second column to display buttons
                        Column {
                            // This button deletes the patient when clicked
                            IconButton(onClick = {
                                viewModel.deletePatientById(patient.id)
                            }
                            ) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                            // this button shares the patient details when clicked
                            IconButton(onClick = {
                                val message = "Patient Name: ${patient.name}\n" +
                                        "Age: ${patient.age}\n" +
                                        "Address: ${patient.address}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, message)
                                }

                                context.startActivity(
                                    Intent.createChooser(
                                        intent,
                                        "Share patient details"
                                    )
                                )
                            }) {
                                Icon(
                                    Icons.Filled.Share, contentDescription = "Share"
                                )
                            }
                        }


                    }
                }

            }
        }

    }
}

