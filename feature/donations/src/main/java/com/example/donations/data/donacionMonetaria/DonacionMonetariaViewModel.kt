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

                // TODO: IMPLEMENTAR FLUJO DE GENERACIÓN DE COMPROBANTE
                // ---- INICIO FLUJO DE GENERACIÓN DE COMPROBANTE ----
                // Mandar datos al endpoint
                // Obtener la URL del comprobante
                var urlComprobante = ""


                // ---- FIN FLUJO DE GENERACIÓN DE COMPROBANTE ----

                // Guardar en Firestore solo cuando el pago es exitoso
                saveDonationToFirestore(
                    nombre = nombre,
                    correo = correo,
                    numTel = numTel,
                    cantidad = cantidad,
                    metodoPago = metodoPago,
                    parqueSeleccionado = parqueSeleccionado,
                    UbicacionSeleccionado = ubicacionSeleccionado,
                    quiereRecibo = quiereRecibo ?: false,
                    rfc = rfc,
                    razon = razon,
                    domFiscal = domFiscal,
                    cardBrand = cardBrand ?: "Tarjeta",
                    lastCardDigits = lastCardDigits ?: "****",
                    transactionId = transactionId ?: "N/A",
                    transactionDate = transactionDate ?: now.toString(),
                    urlComprobante = urlComprobante
                )

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
        if (paymentIntentId == null) {
            println("Error: paymentIntentId es null")
            return
        }

        println("Fetching payment method details for paymentIntentId: $paymentIntentId")

        viewModelScope.launch {
            try {
                // Llamada al endpoint
                val url = URL("https://yeddyrljnqczjxcqirjh.supabase.co/functions/v1/get-payment-method-details")
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
                    println("Response from get-payment-method-details: $response")

                    val jsonObject = JSONObject(response)
                    lastCardDigits = jsonObject.optString("last4", null)
                    cardBrand = jsonObject.optString("brand", null)
                    paymentMethodId = jsonObject.optString("paymentMethodId", null)

                    println("Detalles de la tarjeta obtenidos: Últimos 4 dígitos: $lastCardDigits, Marca: $cardBrand")
                } else {
                    println("Error en la respuesta: Código $responseCode")
                    useAlternativeApproach(paymentIntentId)
                }
            } catch (e: Exception) {
                println("Error al obtener detalles del método de pago: ${e.message}")
                useAlternativeApproach(paymentIntentId)
            }
        }
    }

    private fun useAlternativeApproach(paymentIntentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
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
                    println("Response from get-payment-intent: $response")

                    val jsonObject = JSONObject(response)
                    if (jsonObject.has("paymentIntent") && !jsonObject.isNull("paymentIntent")) {
                        val paymentIntentObj = jsonObject.getJSONObject("paymentIntent")
                        if (paymentIntentObj.has("paymentMethod") && !paymentIntentObj.isNull("paymentMethod")) {
                            val paymentMethodObj = paymentIntentObj.getJSONObject("paymentMethod")
                            if (paymentMethodObj.has("card") && !paymentMethodObj.isNull("card")) {
                                val cardObj = paymentMethodObj.getJSONObject("card")
                                // Verifica si el objeto "card" tiene los campos "last4" y "brand"
                                lastCardDigits = if (cardObj.has("last4") && !cardObj.isNull("last4")) {
                                    cardObj.optString("last4", null)
                                } else {
                                    "****" // Valor predeterminado si no hay "last4"
                                }
                                cardBrand = if (cardObj.has("brand") && !cardObj.isNull("brand")) {
                                    cardObj.optString("brand", null)
                                } else {
                                    "Tarjeta" // Valor predeterminado si no hay "brand"
                                }
                                println("Detalles de la tarjeta obtenidos (alternativo): Últimos 4 dígitos: $lastCardDigits, Marca: $cardBrand")
                            } else {
                                println("El método de pago no tiene detalles de tarjeta")
                                lastCardDigits = "****" // Valor predeterminado
                                cardBrand = "Tarjeta"  // Valor predeterminado
                            }
                        } else {
                            println("El PaymentIntent no tiene un método de pago asociado")
                            lastCardDigits = "****" // Valor predeterminado
                            cardBrand = "Tarjeta"  // Valor predeterminado
                        }
                    } else {
                        println("La respuesta no contiene un PaymentIntent válido")
                        lastCardDigits = "****" // Valor predeterminado
                        cardBrand = "Tarjeta"  // Valor predeterminado
                    }
                } else {
                    val errorResponse = connection.errorStream.bufferedReader().use { it.readText() }
                    println("Error en el método alternativo. Código: $responseCode, Respuesta: $errorResponse")
                    lastCardDigits = "****" // Valor predeterminado
                    cardBrand = "Tarjeta"  // Valor predeterminado
                }
            } catch (e: Exception) {
                println("Error en el método alternativo: ${e.message}")
                lastCardDigits = "****" // Valor predeterminado
                cardBrand = "Tarjeta"  // Valor predeterminado
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