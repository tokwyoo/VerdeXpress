package com.example.donations.ui.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.design.SecondaryAppBar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetalleDonacion(navController: NavController, donationType: String, donationId: String) {
    val db = FirebaseFirestore.getInstance()
    val scrollState = rememberScrollState()

    // State for loading indicator
    var isLoading by remember { mutableStateOf(true) }
    // States for Error handling
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Estados para donaciones monetarias
    var donanteNombre by remember { mutableStateOf("") }
    var donanteContacto by remember { mutableStateOf("") }
    var donanteCorreo by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var parqueSeleccionado by remember { mutableStateOf("") }
    var ubicacionSeleccionada by remember { mutableStateOf("") }
    var transactionId by remember { mutableStateOf("") }
    var createdAt by remember { mutableStateOf("") }

    // Estados para donaciones en especie
    var parqueDonado by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var tipoRecurso by remember { mutableStateOf("") }
    var recurso by remember { mutableStateOf("") }
    var cantidadEspecie by remember { mutableStateOf("") }
    var condicion by remember { mutableStateOf("") }
    var fechaEstimadaDonacion by remember { mutableStateOf("") }
    var imagenes by remember { mutableStateOf<List<String>>(emptyList()) }

    // Function to format timestamp
    fun formatTimestamp(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formatter.format(date)
    }

    // Effect to fetch donation data
    LaunchedEffect(donationType, donationId) {
        isLoading = true
        hasError = false

        try {
            val collectionPath = when (donationType) {
                "monetaria" -> "donaciones_monetaria"
                "especie" -> "donaciones_especie"
                else -> throw Exception("Tipo de donación no válido")
            }

            val documentSnapshot = db.collection(collectionPath).document(donationId).get().await()

            if (documentSnapshot.exists()) {
                when (donationType) {
                    "monetaria" -> {
                        donanteNombre = documentSnapshot.getString("donante_nombre") ?: ""
                        donanteContacto = documentSnapshot.getString("donante_contacto") ?: ""
                        donanteCorreo = documentSnapshot.getString("donante_correo") ?: ""
                        metodoPago = documentSnapshot.getString("metodo_pago") ?: ""
                        cantidad = documentSnapshot.getString("cantidad") ?: ""
                        parqueSeleccionado = documentSnapshot.getString("parque_seleccionado") ?: ""
                        ubicacionSeleccionada = documentSnapshot.getString("ubicacion_seleccionada") ?: ""
                        transactionId = documentSnapshot.getString("transaction_id") ?: ""

                        val timestamp = documentSnapshot.getTimestamp("created_at")
                        createdAt = if (timestamp != null) formatTimestamp(timestamp) else ""
                    }
                    "especie" -> {
                        donanteNombre = documentSnapshot.getString("donante_nombre") ?: ""
                        donanteContacto = documentSnapshot.getString("donante_contacto") ?: ""
                        parqueDonado = documentSnapshot.getString("parque_donado") ?: ""
                        ubicacion = documentSnapshot.getString("ubicacion") ?: ""
                        tipoRecurso = documentSnapshot.getString("tipo_recurso") ?: ""
                        recurso = documentSnapshot.getString("recurso") ?: ""
                        cantidadEspecie = documentSnapshot.getString("cantidad") ?: ""
                        condicion = documentSnapshot.getString("condicion") ?: ""
                        fechaEstimadaDonacion = documentSnapshot.getString("fecha_estimada_donacion") ?: ""

                        val imagenesArray = documentSnapshot.get("imagenes") as? List<String>
                        imagenes = imagenesArray ?: emptyList()

                        val timestamp = documentSnapshot.getTimestamp("created_at")
                        createdAt = if (timestamp != null) formatTimestamp(timestamp) else ""
                    }
                }
            } else {
                hasError = true
                errorMessage = "No se encontró la donación"
            }
        } catch (e: Exception) {
            hasError = true
            errorMessage = "Error al cargar la donación: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF8F8F8))
    ) {
        SecondaryAppBar()

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            hasError -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = errorMessage, color = Color.Red)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Volver")
                        }
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = "Detalles de la donación",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78B153)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Título según tipo de donación
                            Text(
                                text = when(donationType) {
                                    "monetaria" -> "Donación monetaria"
                                    "especie" -> "Donación en especie"
                                    else -> "Donación"
                                },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78B153)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Información común
                            InfoRow("Fecha de registro", createdAt)
                            InfoRow("Donante", donanteNombre)
                            InfoRow("Contacto", donanteContacto)

                            // Información específica
                            when (donationType) {
                                "monetaria" -> {
                                    InfoRow("Correo electrónico", donanteCorreo)
                                    InfoRow("Método de pago", metodoPago)
                                    InfoRow("Monto donado", cantidad)
                                    InfoRow("Parque seleccionado", parqueSeleccionado)
                                    InfoRow("Ubicación", ubicacionSeleccionada)
                                    InfoRow("ID de transacción", transactionId)
                                }
                                "especie" -> {
                                    InfoRow("Parque seleccionado", parqueDonado)
                                    InfoRow("Ubicación", ubicacion)
                                    InfoRow("Tipo de recurso", tipoRecurso)
                                    InfoRow("Recurso", recurso)
                                    InfoRow("Cantidad", cantidadEspecie)
                                    if (condicion.isNotEmpty()) {
                                        InfoRow("Condición", condicion)
                                    }
                                    InfoRow("Fecha estimada de entrega", fechaEstimadaDonacion)

                                    // Mostrar imágenes si existen
                                    if (imagenes.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Imágenes adjuntas",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        imagenes.forEach { imageUrl ->
                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = "Imagen de donación",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .padding(vertical = 8.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF78B153)
                        )
                    ) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    if (value.isNotEmpty()) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 16.sp
            )
        }
        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color(0xFFEEEEEE)
        )
    }
}