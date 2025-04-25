package com.example.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.design.SFProDisplayBold
import com.example.design.SFProDisplayMedium

// Definir el color verde como una constante a nivel de archivo para que esté disponible en todas las funciones
private val greenColor = Color(0xFF78B153)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        containerColor = Color.White,
        topBar = { MainAppBar() },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = "Hola, Usuario",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // Contenido principal según la imagen del prototipo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Columna izquierda: Resumen de actividad
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Resumen de actividad
                        Card(
                            modifier = Modifier.fillMaxWidth().height(428.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, greenColor) // Cambiado a verde
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Text(
                                    text = "Resumen de actividad",
                                    fontFamily = SFProDisplayBold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = "Estado de los últimos parques apoyados",
                                    fontFamily = SFProDisplayMedium,
                                    fontSize = 10.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(5.dp))

                                // Lista de parques
                                ParkItem(name = "Parque Pick Me", status = "desarrollo")
                                Spacer(modifier = Modifier.height(4.dp))

                                ParkItem(name = "Parque 'Nombre'", status = "recibiendo donaciones")
                                Spacer(modifier = Modifier.height(4.dp))

                                ParkItem(name = "Parque 'Nombre'", status = "financiación completada")
                                Spacer(modifier = Modifier.height(4.dp))

                                ParkItem(name = "Parque 'Nombre'", status = "financiación completada")
                                Spacer(modifier = Modifier.height(4.dp))

                                ParkItem(name = "Parque 'Nombre'", status = "financiación completada")
                            }
                        }
                    }

                    // Columna derecha: Parques apoyados, Últimas donaciones y Donaciones
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Parques apoyados
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = greenColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Parques apoyados",
                                    fontFamily = SFProDisplayBold,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )

                                Text(
                                    text = "03",
                                    fontFamily = SFProDisplayBold,
                                    color = Color.White,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Últimas donaciones
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, greenColor) // Cambiado a verde
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Últimas donaciones",
                                        fontFamily = SFProDisplayBold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Ver más",
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Donaciones
                                DonationItemNew(
                                    name = "Parque Las Minitas",
                                    date = "09 Marzo 2025",
                                    amount = "Monto"
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                DonationItemNew(
                                    name = "Parque 'Nombre'",
                                    date = "03 Marzo 2025",
                                    amount = "Nombre del recurso"
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                DonationItemNew(
                                    name = "Parque 'Nombre'",
                                    date = "27 Febrero 2025",
                                    amount = "Monto"
                                )
                            }
                        }

                        // Donaciones (botones del prototipo)
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, greenColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Donaciones",
                                    fontFamily = SFProDisplayBold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Botón Monetaria
                                    Card(
                                        modifier = Modifier.weight(1f).height(70.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = CardDefaults.cardColors(containerColor = greenColor),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Monetaria",
                                                fontFamily = SFProDisplayBold,
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = "$",
                                                fontFamily = SFProDisplayBold,
                                                color = Color.White,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // Botón En especie
                                    Card(
                                        modifier = Modifier.weight(1f).height(70.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = CardDefaults.cardColors(containerColor = greenColor),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "En especie",
                                                fontFamily = SFProDisplayBold,
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = "En especie",
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
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
    }
}

@Composable
fun ParkItem(name: String, status: String) {
    val statusColor = when (status.lowercase()) {
        "desarrollo" -> greenColor
        "recibiendo donaciones" -> greenColor
        "financiación completada" -> greenColor
        else -> greenColor
    }

    Card(
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp), // MODIFICACIÓN: Añadido padding horizontal y vertical
            horizontalAlignment = Alignment.Start // Alineado a la izquierda
        ) {
            Text(
                text = name,
                fontFamily = SFProDisplayBold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp) // MODIFICACIÓN: Espacio inferior para el nombre
            )

            //Spacer(modifier = Modifier.height(2.dp)) // Eliminado el Spacer

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Se encuentra en ",
                    fontFamily = SFProDisplayMedium,
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )

                Text(
                    text = status,
                    fontFamily = SFProDisplayBold,
                    fontSize = 11.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )

                if (status == "financiación completada") {
                    Text(
                        text = ".",
                        fontFamily = SFProDisplayMedium,
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun DonationItemNew(name: String, date: String, amount: String) {
    Card(
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = greenColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp), // MODIFICACIÓN: Añadido padding horizontal y vertical
            horizontalAlignment = Alignment.Start // Alineado a la izquierda
        ) {
            Text(
                text = name,
                fontFamily = SFProDisplayBold,
                fontSize = 13.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp) // MODIFICACIÓN: Espacio inferior para el nombre
            )

            Row(
                verticalAlignment = Alignment.CenterVertically // MODIFICACIÓN: Centrar verticalmente
            ) {
                Text(
                    text = date,
                    fontFamily = SFProDisplayMedium,
                    fontSize = 11.sp,
                    color = Color.White,
                    modifier = Modifier.padding(4.dp) // MODIFICACIÓN: Espacio a la derecha de la fecha
                )

                Text(
                    text = "• $amount",
                    fontFamily = SFProDisplayMedium,
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }
    }
}