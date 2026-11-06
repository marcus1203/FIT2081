package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.util

import android.content.Context
import android.util.Log
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.Patient

object CsvImporter {
    private const val TAG = "CsvImporter"

    fun parsePatientsCSV(context: Context): List<Patient> {
        val patients = mutableListOf<Patient>()
        Log.d(TAG, "Starting to parse CSV file")
        try {
            context.assets.open("data.csv").bufferedReader().useLines { lines ->
                val iterator = lines.iterator()
                if (iterator.hasNext()) {
                    val header = iterator.next()
                    Log.d(TAG, "CSV Header: $header")
                    val columnCount = header.split(",").size
                    Log.d(TAG, "Number of columns in header: $columnCount")
                }
                var lineCount = 0
                while (iterator.hasNext()) {
                    val line = iterator.next()
                    val tokens = line.split(",")
                    Log.d(TAG, "Processing line ${lineCount + 1} with ${tokens.size} columns")
                    
                    try {
                        if (tokens.size < 3) {
                            Log.e(TAG, "Line ${lineCount + 1} has insufficient columns: ${tokens.size}")
                            continue
                        }

                        val patient = Patient(
                            userId = tokens[1].trim(),
                            phoneNumber = tokens[0].trim(),
                            name = null,
                            password = null,
                            sex = tokens[2].trim(),
                            heifaTotalScoreMale = tokens[3].toDoubleOrNull(),
                            heifaTotalScoreFemale = tokens[4].toDoubleOrNull(),
                            discretionaryHeifaScoreMale = tokens[5].toDoubleOrNull(),
                            discretionaryHeifaScoreFemale = tokens[6].toDoubleOrNull(),
                            discretionaryServeSize = tokens[7].toDoubleOrNull(),
                            vegetablesHeifaScoreMale = tokens[8].toDoubleOrNull(),
                            vegetablesHeifaScoreFemale = tokens[9].toDoubleOrNull(),
                            vegetablesWithLegumesAllocatedServeSize = tokens[10].toDoubleOrNull(),
                            legumesAllocatedVegetables = tokens[11].toDoubleOrNull(),
                            vegetablesVariationsScore = tokens[12].toDoubleOrNull(),
                            vegetablesCruciferous = tokens[13].toDoubleOrNull(),
                            vegetablesTuberAndBulb = tokens[14].toDoubleOrNull(),
                            vegetablesOther = tokens[15].toDoubleOrNull(),
                            legumes = tokens[16].toDoubleOrNull(),
                            vegetablesGreen = tokens[17].toDoubleOrNull(),
                            vegetablesRedAndOrange = tokens[18].toDoubleOrNull(),
                            fruitHeifaScoreMale = tokens[19].toDoubleOrNull(),
                            fruitHeifaScoreFemale = tokens[20].toDoubleOrNull(),
                            fruitServeSize = tokens[21].toDoubleOrNull(),
                            fruitVariationsScore = tokens[22].toDoubleOrNull(),
                            fruitPome = tokens[23].toDoubleOrNull(),
                            fruitTropicalAndSubtropical = tokens[24].toDoubleOrNull(),
                            fruitBerry = tokens[25].toDoubleOrNull(),
                            fruitStone = tokens[26].toDoubleOrNull(),
                            fruitCitrus = tokens[27].toDoubleOrNull(),
                            fruitOther = tokens[28].toDoubleOrNull(),
                            grainsAndCerealsHeifaScoreMale = tokens[29].toDoubleOrNull(),
                            grainsAndCerealsHeifaScoreFemale = tokens[30].toDoubleOrNull(),
                            grainsAndCerealsServeSize = tokens[31].toDoubleOrNull(),
                            grainsAndCerealsNonWholeGrains = tokens[32].toDoubleOrNull(),
                            wholeGrainsHeifaScoreMale = tokens[33].toDoubleOrNull(),
                            wholeGrainsHeifaScoreFemale = tokens[34].toDoubleOrNull(),
                            wholeGrainsServeSize = tokens[35].toDoubleOrNull(),
                            meatAndAlternativesHeifaScoreMale = tokens[36].toDoubleOrNull(),
                            meatAndAlternativesHeifaScoreFemale = tokens[37].toDoubleOrNull(),
                            meatAndAlternativesWithLegumesAllocatedServeSize = tokens[38].toDoubleOrNull(),
                            legumesAllocatedMeatAndAlternatives = tokens[39].toDoubleOrNull(),
                            dairyAndAlternativesHeifaScoreMale = tokens[40].toDoubleOrNull(),
                            dairyAndAlternativesHeifaScoreFemale = tokens[41].toDoubleOrNull(),
                            dairyAndAlternativesServeSize = tokens[42].toDoubleOrNull(),
                            sodiumHeifaScoreMale = tokens[43].toDoubleOrNull(),
                            sodiumHeifaScoreFemale = tokens[44].toDoubleOrNull(),
                            sodiumMgMilligrams = tokens[45].toDoubleOrNull(),
                            alcoholHeifaScoreMale = tokens[46].toDoubleOrNull(),
                            alcoholHeifaScoreFemale = tokens[47].toDoubleOrNull(),
                            alcoholStandardDrinks = tokens[48].toDoubleOrNull(),
                            waterHeifaScoreMale = tokens[49].toDoubleOrNull(),
                            waterHeifaScoreFemale = tokens[50].toDoubleOrNull(),
                            water = tokens[51].toDoubleOrNull(),
                            waterTotalMl = tokens[52].toDoubleOrNull(),
                            beverageTotalMl = tokens[53].toDoubleOrNull(),
                            sugarHeifaScoreMale = tokens[54].toDoubleOrNull(),
                            sugarHeifaScoreFemale = tokens[55].toDoubleOrNull(),
                            sugar = tokens[56].toDoubleOrNull(),
                            saturatedFatHeifaScoreMale = tokens[57].toDoubleOrNull(),
                            saturatedFatHeifaScoreFemale = tokens[58].toDoubleOrNull(),
                            saturatedFat = tokens[59].toDoubleOrNull(),
                            unsaturatedFatHeifaScoreMale = tokens[60].toDoubleOrNull(),
                            unsaturatedFatHeifaScoreFemale = tokens[61].toDoubleOrNull(),
                            unsaturatedFatServeSize = tokens[62].toDoubleOrNull(),
                            hasCompletedInitialQuestionnaire = false
                        )
                        patients.add(patient)
                        lineCount++
                        Log.d(TAG, "Successfully added patient with ID: ${patient.userId}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing line ${lineCount + 1}: ${e.message}")
                    }
                }
                Log.d(TAG, "Successfully parsed $lineCount patients from CSV")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing CSV file", e)
        }
        return patients
    }
} 