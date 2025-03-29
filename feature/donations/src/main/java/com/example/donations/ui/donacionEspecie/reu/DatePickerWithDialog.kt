package com.example.donations.ui.donacionEspecie.reu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDialog(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX")).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val verdeBoton = Color(0xFF78B153)

    val customColorScheme = MaterialTheme.colorScheme.copy(
        primary = verdeBoton,
        onPrimary = Color.White,
        secondary = verdeBoton,
        onSecondary = Color.White,
        tertiary = verdeBoton
    )

    // Configuramos el calendario en UTC
    val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        // Reseteamos horas, minutos, segundos y milisegundos
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val todayMillis = utcCalendar.timeInMillis

    // Fecha mínima (hoy)
    val minDateMillis = todayMillis

    // Fecha máxima (hoy + 2 meses)
    val maxDateCalendar = utcCalendar.clone() as Calendar
    maxDateCalendar.add(Calendar.MONTH, 2)
    val maxDateMillis = maxDateCalendar.timeInMillis

    // Estado del DatePicker
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (selectedDate.isNotEmpty()) {
            try {
                dateFormatter.parse(selectedDate)?.time ?: todayMillis
            } catch (e: Exception) {
                todayMillis
            }
        } else {
            todayMillis
        },
        initialDisplayedMonthMillis = todayMillis,
        yearRange = IntRange(
            utcCalendar.get(Calendar.YEAR),
            maxDateCalendar.get(Calendar.YEAR)
        ),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Permitimos desde hoy (incluyendo) hasta 2 meses después
                return utcTimeMillis >= minDateMillis && utcTimeMillis <= maxDateMillis
            }
        }
    )

    MaterialTheme(colorScheme = customColorScheme) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                // Aseguramos que la fecha esté dentro del rango permitido
                                if (millis in minDateMillis..maxDateMillis) {
                                    val formattedDate = dateFormatter.format(Date(millis))
                                    onDateSelected(formattedDate)
                                }
                            }
                            onDismiss()
                        },
                        enabled = datePickerState.selectedDateMillis?.let {
                            it in minDateMillis..maxDateMillis
                        } ?: false
                    ) {
                        Text("Aceptar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Selecciona una fecha",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    )
                }
            )
        }
    }
}