package com.example.donations.data.donacionEspecie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

data class DonationFormState(
    val donorName: String = "",
    val contactNumber: String = "",
    val parkToDonate: String = "",
    val location: String = "",
    val resourceType: String = "",
    val resource: String = "",
    val quantity: String = "",
    val condition: String = "",
    val selectedImageUris: List<android.net.Uri> = emptyList(),
    val estimatedDonationDate: String = ""
)

data class DonationValidationResult(
    val donorNameError: String? = null,
    val contactNumberError: String? = null,
    val parkToDonateError: String? = null,
    val locationError: String? = null,
    val resourceTypeError: String? = null,
    val resourceError: String? = null,
    val quantityError: String? = null,
    val conditionError: String? = null,
    val imageError: String? = null,
    val estimatedDonationDateError: String? = null
)

@Composable
fun rememberDonationFormValidator() = remember {
    { state: DonationFormState ->
        val quantityInt = state.quantity.toIntOrNull() ?: 0
        DonationValidationResult(
            donorNameError = when {
                state.donorName.isEmpty() -> "El nombre no puede estar vacío"
                state.donorName.length > 100 -> "El nombre no puede exceder 100 caracteres"
                else -> null
            },
            contactNumberError = when {
                state.contactNumber.isEmpty() -> "El número de contacto no puede estar vacío"
                state.contactNumber.length < 10 -> "El número de contacto debe tener al menos 10 dígitos"
                else -> null
            },
            parkToDonateError = when {
                state.parkToDonate.isEmpty() -> "Debes seleccionar un parque"
                else -> null
            },
            locationError = when {
                state.location.isEmpty() -> "La ubicación no puede estar vacía"
                else -> null
            },
            resourceTypeError = when {
                state.resourceType.isEmpty() -> "Debes seleccionar un tipo de recurso"
                else -> null
            },
            resourceError = when {
                state.resource.isEmpty() -> "Debes seleccionar un recurso"
                else -> null
            },
            quantityError = when {
                state.quantity.isEmpty() -> "La cantidad no puede estar vacía"
                quantityInt < 1 -> "La cantidad mínima es 1"
                quantityInt > 10 -> "La cantidad máxima es 10"
                else -> null
            },
            conditionError = when {
                // Solo validar la condición si el tipo de recurso es "Mobiliario"
                state.resourceType == "Mobiliario" && state.condition.isEmpty() -> "Debes seleccionar una condición"
                else -> null
            },
            imageError = when {
                state.selectedImageUris.isEmpty() -> "Debes seleccionar al menos una imagen."
                state.selectedImageUris.size > 3 -> "Solo puedes subir un máximo de 3 imágenes."
                else -> null
            },
            estimatedDonationDateError = when {
                state.estimatedDonationDate.isEmpty() -> "Debes seleccionar una fecha estimada de donación"
                !isValidDateFormat(state.estimatedDonationDate) -> "Formato de fecha inválido"
                !isFutureOrTodayDate(state.estimatedDonationDate) -> "La fecha debe ser hoy o en el futuro"
                else -> null
            }
        )
    }
}

fun DonationValidationResult.isValid(): Boolean {
    return donorNameError == null &&
            contactNumberError == null &&
            parkToDonateError == null &&
            locationError == null &&
            resourceTypeError == null &&
            resourceError == null &&
            quantityError == null &&
            conditionError == null &&
            imageError == null
}

private fun isValidDateFormat(dateString: String): Boolean {
    return try {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
        formatter.isLenient = false
        formatter.parse(dateString)
        true
    } catch (e: Exception) {
        false
    }
}

private fun isFutureOrTodayDate(dateString: String): Boolean {
    return try {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
        val selectedDate = formatter.parse(dateString)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        val today = LocalDate.now()
        selectedDate?.isEqual(today) ?: false || selectedDate?.isAfter(today) ?: false
    } catch (e: Exception) {
        false
    }
}