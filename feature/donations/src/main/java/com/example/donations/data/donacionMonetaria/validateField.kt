package com.example.donations.data.donacionMonetaria

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.util.Locale
import java.util.regex.Pattern

data class MonetariaFormState(
    val nombre: String = "",
    val correo: String = "",
    val numTel: String = "",
    val cantidad: String = "",
    val metodoPago: String = "",
    val parqueSeleccionado: String = "",
    val ubicacionSeleccionado: String = "",
    val quiereRecibo: Boolean? = null,
    val rfc: String = "",
    val razon: String = "",
    val domFiscal: String = ""
)

data class MonetariaValidationResult(
    val nombreError: String? = null,
    val correoError: String? = null,
    val numTelError: String? = null,
    val cantidadError: String? = null,
    val metodoPagoError: String? = null,
    val parqueSeleccionadoError: String? = null,
    val rfcError: String? = null,
    val razonError: String? = null,
    val domFiscalError: String? = null,
    val quiereReciboError: String? = null
)

@Composable
fun rememberMonetariaFormValidator() = remember {
    { state: MonetariaFormState ->
        val cantidadDouble = state.cantidad.toDoubleOrNull() ?: 0.0
        MonetariaValidationResult(
            nombreError = when {
                state.nombre.isEmpty() -> "El nombre no puede estar vacío"
                state.nombre.length > 100 -> "El nombre no puede exceder 100 caracteres"
                else -> null
            },
            correoError = when {
                state.correo.isEmpty() -> "El correo electrónico no puede estar vacío"
                !Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$").matcher(state.correo).matches() -> "Formato de correo electrónico inválido"
                else -> null
            },
            numTelError = when {
                state.numTel.isEmpty() -> "El teléfono no puede estar vacío"
                !state.numTel.matches(Regex("^\\d{10}$")) -> "El teléfono debe tener 10 dígitos"
                else -> null
            },
            cantidadError = when {
                state.cantidad.isEmpty() -> "La cantidad no puede estar vacía"
                cantidadDouble < 10.0 -> "El monto mínimo es \$10 MXN"
                cantidadDouble > 50000.0 -> "El monto máximo es \$50,000 MXN"
                else -> null
            },
            metodoPagoError = when {
                state.metodoPago.isEmpty() -> "Debes seleccionar un método de pago"
                else -> null
            },
            parqueSeleccionadoError = when {
                state.parqueSeleccionado.isEmpty() -> "Debes seleccionar un parque"
                else -> null
            },
            rfcError = when {
                state.quiereRecibo == true && state.rfc.isEmpty() -> "El RFC es obligatorio"
                state.quiereRecibo == true && !Pattern.compile("^([A-ZÑ&]{3,4}) ?(?:- ?)?(\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])) ?(?:- ?)?([A-Z\\d]{3})$").matcher(
                    state.rfc.uppercase(Locale.getDefault())
                ).matches() -> "Formato de RFC inválido"
                else -> null
            },
            razonError = when {
                // Una persona física puede querer recibo, y no tener una razón social
                //state.quiereRecibo == true && state.razon.isEmpty() -> "La razón social es obligatoria"
                state.quiereRecibo == true && state.razon.length > 100 -> "La razón social debe tener máximo 100 caracteres"
                else -> null
            },
            domFiscalError = when {
                state.quiereRecibo == true && state.domFiscal.isEmpty() -> "El domicilio fiscal es obligatorio"
                state.quiereRecibo == true && state.domFiscal.length > 200 -> "El domicilio fiscal debe tener máximo 200 caracteres"
                else -> null
            },
            quiereReciboError = when {
                state.quiereRecibo == null -> "Debes seleccionar si deseas recibo o no"
                else -> null
            }
        )
    }
}

fun MonetariaValidationResult.isValid(): Boolean {
    return nombreError == null &&
            correoError == null &&
            numTelError == null &&
            cantidadError == null &&
            metodoPagoError == null &&
            parqueSeleccionadoError == null &&
            rfcError == null &&
            razonError == null &&
            domFiscalError == null &&
            quiereReciboError == null
}