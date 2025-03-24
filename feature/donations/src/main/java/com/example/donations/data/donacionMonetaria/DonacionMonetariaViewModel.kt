package com.example.donations.data.donacionMonetaria

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

    // Detalles del método de pago
    var lastCardDigits by mutableStateOf<String?>(null)
    var cardBrand by mutableStateOf<String?>(null)
    var paymentMethodId by mutableStateOf<String?>(null)

    // Estado para mostrar el mensaje de éxito
    var showSuccessMessage by mutableStateOf(false)

    // Detalles de la transacción
    var transactionId by mutableStateOf<String?>(null)
    var transactionDate by mutableStateOf<String?>(null)
    var formattedTransactionDate by mutableStateOf<String?>(null)

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

        // Limpiar detalles del método de pago
        lastCardDigits = null
        cardBrand = null
        paymentMethodId = null

        // Limpiar detalles de la transacción
        transactionId = null
        transactionDate = null
        formattedTransactionDate = null
        showSuccessMessage = false
    }

    // Función para preparar la hoja de pago
    fun preparePaymentSheet() {
        if (cantidad.isEmpty()) return

        isLoading = true
        viewModelScope.launch {
            try {
                // Crear un PaymentIntent en el backend
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
                    merchantDisplayName = "VerdeXpress",
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
                transactionId = extractTransactionId(paymentIntentClientSecret)

                // Establecer y formatear la fecha de la transacción
                val now = LocalDateTime.now()
                transactionDate = now.toString()
                formattedTransactionDate = formatDate(now)

                // Obtener los detalles del método de pago desde el backend
                fetchPaymentMethodDetails(transactionId)

                showSuccessMessage = true
            }
            is PaymentSheetResult.Canceled -> {
                paymentStatus = result
            }
            is PaymentSheetResult.Failed -> {
                paymentStatus = result
            }
        }
    }

    // Función para extraer el ID de la transacción desde el clientSecret
    private fun extractTransactionId(clientSecret: String?): String? {
        return clientSecret?.substringBefore("_secret")
    }

    // Función para formatear la fecha de manera legible
    private fun formatDate(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        return dateTime.format(formatter)
    }

    // Función para obtener detalles del método de pago (tarjeta)
    private fun fetchPaymentMethodDetails(paymentIntentId: String?) {
        if (paymentIntentId == null) return

        viewModelScope.launch {
            try {
                // Hacemos una llamada al backend para obtener los detalles del método de pago
                // Este endpoint deberá ser implementado en tu backend
                val url = URL("https://yeddyrljnqczjxcqirjh.supabase.co/functions/v1/get-payment-method-details")
                val connection = url.openConnection() as HttpsURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InllZGR5cmxqbnFjemp4Y3FpcmpoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyNTQyNDYsImV4cCI6MjA1NTgzMDI0Nn0.QLt1enP5CyjmVBW1aTMuMksQIJcfGu9ROeBhr5MY-Sg")
                connection.setRequestProperty("x-client-info", "Android App")
                connection.doOutput = true

                // Cuerpo de la solicitud con el ID del PaymentIntent
                val requestBody = """
                {
                    "paymentIntentId": "$paymentIntentId"
                }
                """.trimIndent()

                connection.outputStream.use { os ->
                    os.write(requestBody.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                if (responseCode in 200..299) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(response)

                    // Extraer los detalles de la tarjeta
                    lastCardDigits = jsonObject.optString("last4", null)
                    cardBrand = jsonObject.optString("brand", null)
                    paymentMethodId = jsonObject.optString("paymentMethodId", null)

                    println("Detalles de la tarjeta obtenidos: Últimos 4 dígitos: $lastCardDigits, Marca: $cardBrand")
                } else {
                    // Si hay un error, intentamos un enfoque alternativo
                    println("Error al obtener detalles del método de pago. Usando método alternativo...")
                    useAlternativeApproach(paymentIntentId)
                }
            } catch (e: Exception) {
                println("Error al obtener detalles del método de pago: ${e.message}")
                // Intentar el enfoque alternativo si falla la primera aproximación
                useAlternativeApproach(paymentIntentId)
            }
        }
    }

    // Enfoque alternativo para obtener los detalles de la tarjeta
    private fun useAlternativeApproach(paymentIntentId: String) {
        viewModelScope.launch {
            try {
                // Intentar obtener los detalles directamente del PaymentIntent
                val url = URL("https://yeddyrljnqczjxcqirjh.supabase.co/functions/v1/get-payment-intent")
                val connection = url.openConnection() as HttpsURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InllZGR5cmxqbnFjemp4Y3FpcmpoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAyNTQyNDYsImV4cCI6MjA1NTgzMDI0Nn0.QLt1enP5CyjmVBW1aTMuMksQIJcfGu9ROeBhr5MY-Sg")
                connection.setRequestProperty("x-client-info", "Android App")
                connection.doOutput = true

                val requestBody = """
                {
                    "paymentIntentId": "$paymentIntentId"
                }
                """.trimIndent()

                connection.outputStream.use { os ->
                    os.write(requestBody.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                if (responseCode in 200..299) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(response)

                    // Extraer el payment_method.card.last4 y payment_method.card.brand
                    if (jsonObject.has("paymentMethod") && !jsonObject.isNull("paymentMethod")) {
                        val paymentMethodObj = jsonObject.getJSONObject("paymentMethod")
                        if (paymentMethodObj.has("card") && !paymentMethodObj.isNull("card")) {
                            val cardObj = paymentMethodObj.getJSONObject("card")
                            lastCardDigits = cardObj.optString("last4", null)
                            cardBrand = cardObj.optString("brand", null)
                            println("Detalles de la tarjeta obtenidos (alternativo): Últimos 4 dígitos: $lastCardDigits, Marca: $cardBrand")
                        }
                    }
                } else {
                    println("Error en el método alternativo. Código: $responseCode")
                    // Si todo falla, usamos valores predeterminados o valores de depuración
                    lastCardDigits = "1234" // Esto debería reemplazarse con datos reales en producción
                    cardBrand = "visa"     // Esto debería reemplazarse con datos reales en producción
                }
            } catch (e: Exception) {
                println("Error en el método alternativo: ${e.message}")
                // Si todo falla, usamos valores predeterminados o valores de depuración
                lastCardDigits = "1234" // Esto debería reemplazarse con datos reales en producción
                cardBrand = "visa"     // Esto debería reemplazarse con datos reales en producción
            }
        }
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

            Triple(clientSecret, customerId, ephemeralKey)
        } catch (e: Exception) {
            // Capturar y loguear cualquier excepción
            println("Error en fetchPaymentIntent: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}