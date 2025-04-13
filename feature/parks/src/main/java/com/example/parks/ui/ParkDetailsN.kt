package com.example.parks.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.design.MainAppBar
import com.example.parks.data.MapView
import com.example.parks.data.ParkDataA
import com.example.parks.data.formatShortFirestoreDate
import com.example.parks.data.getParkDetails
import com.example.parks.data.rememberUserFullName

@Composable
fun ParkDetailsN(parkName: String?, latitud: String? = null, longitud: String? = null, navController: NavController) {
    // Estados para almacenar los datos del parque y posibles errores
    val parkState = remember { mutableStateOf<ParkDataA?>(null) }
    val errorState = remember { mutableStateOf<String?>(null) }

    // Efecto lanzado para obtener los detalles del parque desde Firestore
    LaunchedEffect(parkName) {
        if (parkName != null) {
            getParkDetails(
                parkName = parkName,
                onSuccess = { park ->
                    // Usamos los datos de Firestore pero sobrescribimos las coordenadas si vienen por navegación
                    parkState.value = park.copy(
                        latitud = latitud ?: park.latitud,  // Usa la latitud de navegación o la de Firestore
                        longitud = longitud ?: park.longitud  // Usa la longitud de navegación o la de Firestore
                    )
                },
                onFailure = { exception ->
                    errorState.value = "Error: ${exception.message}"
                }
            )
        }
    }

    // Renderiza el contenido según el estado actual
    when {
        parkState.value != null -> {
            ParkDetailsContentN(park = parkState.value!!, navController)
        }
        errorState.value != null -> {
            Text(text = "Error: ${errorState.value}")
        }
        else -> {
            Text(text = "Cargando...")
        }
    }
}


@Composable
fun ParkDetailsContentN(park: ParkDataA, navController: NavController) {
    var selectedEstadoActual by remember { mutableStateOf(park.estado) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // AppBar VerdeXpress
        MainAppBar()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // A partir de aqui se definen los elementos que apareceran de la pantalla,
            // como el nombre del parque, ubicación, etc.
            item {
                /* Texto de prueba
                Text(
                    text = "LA NAVEGACIÓN FUNICONA YIPPEE"
                )*/
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        modifier = Modifier
                            .clickable {
                                // Lógica para manejar el clic en el ícono de retroceso
                                navController.navigate("Parques")
                            }
                    )

                    Text(
                        text = "Detalles del parque",
                        fontFamily = SFProDisplayBold,
                        fontSize = 20.sp,
                        color = verde,
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight(700),
                        letterSpacing = 0.25.sp,
                    )
                }
            }

            // Nombre del parque y ubicación
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Columna para texto
                    Column(
                        modifier = Modifier
                            .weight(0.5f)  // Ocupa la mayor parte del ancho
                    ) {
                        Text(
                            text = park.nombre,
                            fontSize = 25.sp,
                            fontFamily = SFProDisplayBold,
                            modifier = Modifier.padding(bottom = 4.dp),
                            fontWeight = FontWeight(700),
                            color = Color(0xFF000000),
                            letterSpacing = 0.25.sp,
                        )

                        Text(
                            text = park.ubi,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Contenedor para el MapView
                    Box(
                        modifier = Modifier
                            .weight(0.6f)  // Ocupa menos espacio
                            .width(165.dp)
                            .aspectRatio(1.9f)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        // Con esta funcion se puede visualizar el mapa
                        MapView(
                            latitud = park.latitud,
                            longitud = park.longitud
                        )
                    }
                }
            }

            // Imágenes del parque
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 16.dp)
                ) {
                    items(park.imagenes) { imagenUrl ->
                        Box(
                            modifier = Modifier
                                .size(width = 140.dp, height = 100.dp)
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = imagenUrl, // URL de la imagen
                                contentDescription = "Imagen del parque",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Indicador de página de imágenes (línea verde)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(4.dp)
                            .background(verde)
                    )
                }
            }

            // Recibiendo donaciones
            item {
                Text(
                    text = "Mantenimiento requerido",
                    fontSize = 20.sp,
                    fontFamily = SFProDisplayBold,
                    modifier = Modifier.padding(bottom = 8.dp),
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(700),
                    color = Color.Black,
                    letterSpacing = 0.25.sp,
                )
                Text(
                    text = "El parque requiere mantenimiento. Se aceptan donaciones.",
                    fontSize = 14.sp,
                    fontFamily = SFProDisplayBold,
                    modifier = Modifier.padding(bottom = 8.dp),
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(500),
                    color = Color.DarkGray,
                    letterSpacing = 0.25.sp,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Necesidades del parque (lista con bullets)
            item {
                Text(
                    text = "Necesidades del parque",
                    fontSize = 20.sp,
                    fontFamily = SFProDisplayBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                park.necesidades.forEach { necesidad ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(verde, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = necesidad, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Estado actual del parque
            item {
                Text(
                    text = "Estado actual del parque",
                    fontSize = 20.sp,
                    fontFamily = SFProDisplayBold,
                    modifier = Modifier.padding(bottom = 8.dp),
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000000),
                    letterSpacing = 0.25.sp,
                )

                Text(
                    text = selectedEstadoActual,
                    fontSize = 14.sp,
                    fontFamily = SFProDisplayBold,
                    color = verde,
                    modifier = Modifier.padding(bottom = 18.dp),
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(700),
                    letterSpacing = 0.25.sp,
                )
            }

            // Botón de donación
            item {
                Button(
                    onClick = { /*navController.navigate("Donaciones")*/ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = verde,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Hacer donación",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}