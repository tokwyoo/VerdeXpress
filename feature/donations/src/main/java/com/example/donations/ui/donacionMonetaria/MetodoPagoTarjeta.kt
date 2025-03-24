package com.example.donations.ui.donacionMonetaria

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.R
import com.example.design.SecondaryAppBar
import com.example.donations.data.donacionMonetaria.DonacionMonetariaViewModel
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import java.time.LocalDateTime

@Composable
fun MetodoPagoTarjetaScreen(navController: NavController, viewModel: DonacionMonetariaViewModel) {
    // Obtener datos del ViewModel
    val nombre = viewModel.nombre
    val correo = viewModel.correo
    val numTel = viewModel.numTel
    val rfc = viewModel.rfc
    val razon = viewModel.razon
    val domFiscal = viewModel.domFiscal
    val parqueSeleccionado = viewModel.parqueSeleccionado
    val ubicacionSeleccionado = viewModel.ubicacionSeleccionado
    val cantidad = viewModel.cantidad
    val transactionId = viewModel.transactionId
    val transactionDate = viewModel.transactionDate

    // Stripe Payment Sheet
    val context = LocalContext.current
    val isPaymentSheetReady = viewModel.isPaymentSheetReady
    val isLoading = viewModel.isLoading
    val paymentSheet = rememberPaymentSheet { result ->
        viewModel.handlePaymentResult(result) // Manejar el resultado del pago
    }

    // Colors and styles
    val verdeBoton = Color(0xFF78B153)
    val roundedShape = RoundedCornerShape(12.dp)

    // Effect to prepare Payment Sheet when entering this screen
    LaunchedEffect(Unit) {
        if (viewModel.paymentIntentClientSecret == null) {
            viewModel.preparePaymentSheet()
        }
    }

    // Screen layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SecondaryAppBar(showIcon = true, onIconClick = {
            navController.popBackStack()
        })

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.showSuccessMessage) {
            // Mostrar mensaje de éxito y detalles de la transacción
            SuccessMessage(
                nombre = nombre,
                correo = correo,
                numTel = numTel,
                rfc = rfc,
                razon = razon,
                domFiscal = domFiscal,
                parqueSeleccionado = parqueSeleccionado,
                ubicacionSeleccionado = ubicacionSeleccionado,
                cantidad = cantidad,
                transactionId = transactionId,
                transactionDate = transactionDate
            )
        } else {
            // Formulario de pago
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Detalles de pago",
                    fontSize = 25.sp,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Donation summary card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = roundedShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Resumen de la donación",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                        )
                        Divider()
                        Text(text = "Nombre del donante: $nombre")
                        Text(text = "Correo electrónico: $correo")
                        Text(text = "Número de contacto: $numTel")

                        if (viewModel.quiereRecibo == true) {
                            Text(text = "RFC: $rfc")
                            if (razon.isNotEmpty()) {
                                Text(text = "Razón social: $razon")
                            }
                            Text(text = "Domicilio fiscal: $domFiscal")
                        }

                        Text(text = "Parque: $parqueSeleccionado")
                        Text(text = "Ubicación: $ubicacionSeleccionado")

                        Text(
                            text = "Monto: $${cantidad} MXN",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Show loading or payment button
                if (isLoading) {
                    CircularProgressIndicator(color = verdeBoton)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Preparando pago...")
                } else {
                    when (val result = viewModel.paymentStatus) {
                        is PaymentSheetResult.Completed -> {
                            // No se muestra nada aquí porque se maneja en el estado showSuccessMessage
                        }
                        is PaymentSheetResult.Failed -> {
                            // Mostrar mensaje de error
                            Text(
                                text = "Error en el pago: ${result.error.message}",
                                color = Color.Red
                            )
                        }
                        is PaymentSheetResult.Canceled -> {
                            Button(
                                onClick = {
                                    if (viewModel.paymentIntentClientSecret != null) {
                                        paymentSheet.presentWithPaymentIntent(
                                            paymentIntentClientSecret = viewModel.paymentIntentClientSecret!!,
                                            configuration = viewModel.paymentSheetConfig
                                        )
                                    } else {
                                        viewModel.preparePaymentSheet()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                                shape = roundedShape
                            ) {
                                Text(
                                    text = "Continuar con el pago",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                                )
                            }
                        }
                        null -> {
                            if (isPaymentSheetReady) {
                                Button(
                                    onClick = {
                                        if (viewModel.paymentIntentClientSecret != null) {
                                            paymentSheet.presentWithPaymentIntent(
                                                paymentIntentClientSecret = viewModel.paymentIntentClientSecret!!,
                                                configuration = viewModel.paymentSheetConfig
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                                    shape = roundedShape
                                ) {
                                    Text(
                                        text = "Pagar ahora",
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.preparePaymentSheet() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                                    shape = roundedShape
                                ) {
                                    Text(
                                        text = "Preparar pago",
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessMessage(
    nombre: String,
    correo: String,
    numTel: String,
    rfc: String?,
    razon: String?,
    domFiscal: String?,
    parqueSeleccionado: String,
    ubicacionSeleccionado: String,
    cantidad: String,
    transactionId: String?,
    transactionDate: String?,
    // Nuevo parámetro para recibir los últimos dígitos de la tarjeta
    lastCardDigits: String? = "4242" // Valor por defecto para compatibilidad
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color(0xFF78B153),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¡Pago exitoso!",
            fontSize = 28.sp,
            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tu donación ha sido procesada correctamente",
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.sf_pro_display_regular)),
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Detalles de la transacción",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )

                Divider(thickness = 1.dp, color = Color.LightGray)

                // Monto con estilo destacado
                InfoRow(
                    label = "Monto",
                    value = "$${cantidad} MXN",
                    valueStyle = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                        color = Color(0xFF78B153)
                    )
                )

                // Método de pago (tarjeta enmascarada)
                InfoRow(
                    label = "Método de pago",
                    value = "•••• •••• •••• ${lastCardDigits ?: "****"}"
                )

                // Fecha formateada
                if (!transactionDate.isNullOrEmpty()) {
                    InfoRow(
                        label = "Fecha",
                        value = formatDate(transactionDate)
                    )
                }

                // ID de transacción
                if (!transactionId.isNullOrEmpty()) {
                    InfoRow(
                        label = "ID de transacción",
                        value = transactionId,
                        valueStyle = TextStyle(fontSize = 14.sp)
                    )
                }

                Divider(thickness = 1.dp, color = Color.LightGray)

                // Detalles de la donación
                Text(
                    text = "Detalles de la donación",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                    modifier = Modifier.padding(top = 8.dp)
                )

                InfoRow(
                    label = "Parque",
                    value = parqueSeleccionado
                )

                InfoRow(
                    label = "Ubicación",
                    value = ubicacionSeleccionado
                )

                // Detalles del donante
                Text(
                    text = "Información del donante",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                    modifier = Modifier.padding(top = 8.dp)
                )

                InfoRow(
                    label = "Nombre",
                    value = nombre
                )

                InfoRow(
                    label = "Correo",
                    value = correo
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Gracias por tu donación!",
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.sf_pro_display_medium)),
            color = Color(0xFF78B153)
        )
    }
}

// Componente reutilizable para filas de información con espaciado mejorado
@Composable
private fun InfoRow(
    label: String,
    value: String,
    labelStyle: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.sf_pro_display_regular))
    ),
    valueStyle: TextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.sf_pro_display_regular))
    )
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = labelStyle,
            modifier = Modifier.weight(1f)
        )

        // Espacio fijo para garantizar separación mínima
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = value,
            style = valueStyle,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.End
        )
    }
}

// Función para formatear la fecha
private fun formatDate(dateString: String): String {
    return try {
        val dateTime = LocalDateTime.parse(dateString)
        val day = dateTime.dayOfMonth.toString().padStart(2, '0')
        val month = dateTime.monthValue.toString().padStart(2, '0')
        val year = dateTime.year
        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')

        "$day/$month/$year $hour:$minute hrs"
    } catch (e: Exception) {
        dateString // Devuelve la cadena original si hay un error de formato
    }
}