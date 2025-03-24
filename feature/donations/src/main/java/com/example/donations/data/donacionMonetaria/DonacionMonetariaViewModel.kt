package com.example.donations.data.donacionMonetaria

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.time.LocalDateTime
import javax.net.ssl.HttpsURLConnection

class DonacionMonetariaViewModel : ViewModel() {
    // Datos del formulario
    var nombre: String = ""
    var correo: String = ""
    var numTel: String = ""
    var cantidad: String = ""
    var metodoPago: String = ""
    var parqueSeleccionado: String = ""
    var ubicacionSeleccionado: String = ""
    var quiereRecibo: Boolean? = null
    var rfc: String = ""
    var razon: String = ""
    var domFiscal: String = ""

    // Stripe-related fields
    var paymentIntentClientSecret by mutableStateOf<String?>(null)
    var customerConfig by mutableStateOf<PaymentSheet.CustomerConfiguration?>(null)
    var paymentSheetConfig by mutableStateOf<PaymentSheet.Configuration?>(null)
    var paymentStatus by mutableStateOf<PaymentSheetResult?>(null)
    var isPaymentSheetReady by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    // Estado para mostrar el mensaje de éxito
    var showSuccessMessage by mutableStateOf(false)

    // Detalles de la transacción
    var transactionId by mutableStateOf<String?>(null)
    var transactionDate by mutableStateOf<String?>(null)

    // Limpiar el ViewModel
    fun clear() {
        nombre = ""
        correo = ""
        numTel = ""
        cantidad = ""
        metodoPago = ""
        parqueSeleccionado = ""
        ubicacionSeleccionado = ""
        quiereRecibo = null
        rfc = ""
        razon = ""
        domFiscal = ""

        // Limpiar campos relacionados con Stripe
        paymentIntentClientSecret = null
        customerConfig = null
        paymentSheetConfig = null
        paymentStatus = null
        isPaymentSheetReady = false

        // Limpiar detalles de la transacción
        transactionId = null
        transactionDate = null
        showSuccessMessage = false
    }

    // Función para preparar la hoja de pago
    fun preparePaymentSheet() {
        if (cantidad.isEmpty()) return

        isLoading = true
        viewModelScope.launch {
            try {
                // Simular una llamada al backend para crear un PaymentIntent
                val paymentIntentData = fetchPaymentIntent()
                paymentIntentClientSecret = paymentIntentData.first
                val customerId = paymentIntentData.second

                if (customerId != null) {
                    customerConfig = PaymentSheet.CustomerConfiguration(
                        customerId,
                        paymentIntentData.third ?: ""
                    )
                }

                // Configurar la hoja de pago
                paymentSheetConfig = PaymentSheet.Configuration(
                    merchantDisplayName = "VerdExpress Donaciones",
                    customer = customerConfig,
                    allowsDelayedPaymentMethods = true
                )

                isPaymentSheetReady = true
            } catch (e: Exception) {
                // Manejar el error e imprimir detalles
                println("Error en preparePaymentSheet: ${e.message}")
                isPaymentSheetReady = false
                paymentStatus = PaymentSheetResult.Failed(Throwable("Error: ${e.message}"))
            } finally {
                isLoading = false
            }
        }
    }

    // Función para manejar el resultado del pago
    fun handlePaymentResult(result: PaymentSheetResult) {
        when (result) {
            is PaymentSheetResult.Completed -> {
                // Pago completado
                paymentStatus = result
                transactionId = extractTransactionId(paymentIntentClientSecret) // Extraer el ID de la transacción
                transactionDate = LocalDateTime.now().toString() // Obtener la fecha actual
                extractPaymentMethodDetails()
                showSuccessMessage = true // Mostrar mensaje de éxito
            }
            is PaymentSheetResult.Canceled -> {
                // Pago cancelado
                paymentStatus = result
            }
            is PaymentSheetResult.Failed -> {
                // Pago fallido
                paymentStatus = result
            }
        }
    }

    // Función para extraer el ID de la transacción desde el clientSecret
    private fun extractTransactionId(clientSecret: String?): String? {
        return clientSecret?.substringBefore("_secret")
    }

    // Función para obtener el PaymentIntent desde el backend
    private suspend fun fetchPaymentIntent(): Triple<String, String?, String?> = withContext(Dispatchers.IO) {
        try {
            // URL del endpoint
            val url = URL("https://yeddyrljnqczjxcqirjh.supabase.co/functions/v1/create-payment-intent")
            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")

            // Agregar el token de autorización de Supabase
            connection.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InllZGR5cmxqbnFjemp4Y3FpcmpoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyNTQyNDYsImV4cCI6MjA1NTgzMDI0Nn0.QLt1enP5CyjmVBW1aTMuMksQIJcfGu9ROeBhr5MY-Sg")

            // Opcional: Agregar el header de x-client-info que puede ayudar con el debugging
            connection.setRequestProperty("x-client-info", "Android App")

            connection.doOutput = true

            // Cuerpo de la solicitud
            val requestBody = """
        {
            "amount": ${cantidad.toIntOrNull() ?: 0},
            "currency": "mxn",
            "description": "Donación a $parqueSeleccionado",
            "email": "$correo"
        }
        """.trimIndent()

            // Enviar la solicitud
            connection.outputStream.use { os ->
                os.write(requestBody.toByteArray(Charsets.UTF_8))
            }

            // Obtener la respuesta
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                // Si la respuesta no es exitosa, lanzar una excepción
                val errorMessage = connection.errorStream.bufferedReader().use { it.readText() }
                throw Exception("Error en la solicitud: Código $responseCode, Mensaje: $errorMessage")
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(response)

            // Extraer datos de la respuesta
            val clientSecret = jsonObject.getString("clientSecret")
            val customerId = if (jsonObject.has("customerId")) jsonObject.getString("customerId") else null
            val ephemeralKey = if (jsonObject.has("ephemeralKey")) jsonObject.getString("ephemeralKey") else null

            // Logs para depuración
            println("Respuesta del backend: $response")
            println("Client Secret: $clientSecret")
            println("Customer ID: $customerId")
            println("Ephemeral Key: $ephemeralKey")

            Triple(clientSecret, customerId, ephemeralKey)
        } catch (e: Exception) {
            // Capturar y loguear cualquier excepción
            println("Error en fetchPaymentIntent: ${e.message}")
            e.printStackTrace() // Esto te dará más detalles en el logcat sobre la excepción
            throw e // Relanzar la excepción para manejarla en el ViewModel
        }
    }

    private fun extractPaymentMethodDetails() {
        viewModelScope.launch {
            try {
                // Esta es una implementación simulada
                // En una implementación real, harías una llamada al backend para obtener los detalles del pago
                // O usarías la API de Stripe para obtener esta información

                // Simulando una obtención de los últimos 4 dígitos
                delay(500) // Simular tiempo de respuesta
                var lastCardDigits = "4242" // Valor por defecto para pruebas

                // En una implementación real, obtendrías esto de la respuesta del API:
                // lastCardDigits = responseData.getString("last4")
            } catch (e: Exception) {
                println("Error al obtener detalles del método de pago: ${e.message}")
                var lastCardDigits = null
            }
        }
    }
}