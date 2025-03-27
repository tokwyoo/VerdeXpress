package com.example.donations.ui.donacionEspecie.reu

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDialog(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))

    // Define el color verde para todo el DatePicker
    val verdeBoton = Color(0xFF78B153)

    // Personaliza el esquema de colores
    val customColorScheme = MaterialTheme.colorScheme.copy(
        primary = verdeBoton,
        onPrimary = Color.White,
        secondary = verdeBoton,
        onSecondary = Color.White,
        tertiary = verdeBoton
    )

    // Get today's date and convert to millis
    val calendar = Calendar.getInstance()
    val todayMillis = calendar.timeInMillis
    val maxDateCalendar = Calendar.getInstance().apply {
        add(Calendar.MONTH, 2)
    }
    val maxDateMillis = maxDateCalendar.timeInMillis

    // Create state with today as initial selection
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = todayMillis,
        selectableDates = object : SelectableDates {
            // Only allow selecting current and future dates
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Convertir los milisegundos a Calendar
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.timeInMillis = utcTimeMillis
                // Comparar con la fecha actual
                return !selectedCalendar.before(calendar) && !selectedCalendar.after(maxDateCalendar)
            }
        }
    )

    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    // Aplica el tema personalizado
    MaterialTheme(
        colorScheme = customColorScheme
    ) {
        DatePickerDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            // Convertir los milisegundos a Calendar
                            val selectedCalendar = Calendar.getInstance()
                            selectedCalendar.timeInMillis = millis

                            // Añadir un día para compensar el problema de zona horaria
                            selectedCalendar.add(Calendar.DAY_OF_MONTH, 1)

                            // Formatear la fecha
                            val formattedDate = dateFormatter.format(selectedCalendar.time)
                            onDateSelected(formattedDate)
                        }
                        onDismiss()
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss() }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}