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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.donations.data.inicio.GetUserDonations
import com.example.parks.data.ParkDataA
import com.example.parks.data.getParkDetails
import com.example.parks.data.rememberUserFullName
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private val greenColor = Color(0xFF78B153)
private val lightGrayColor = Color(0xFFF5F6F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid ?: ""
    val userName = rememberUserFullName(userId)
    val scrollState = rememberScrollState()

    // Estados para las donaciones
    var donations by remember { mutableStateOf<List<GetUserDonations.UserDonation>>(emptyList()) }
    var isLoadingDonations by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }

    // Estados para los parques apoyados
    var supportedParks by remember { mutableStateOf<List<ParkDataA>>(emptyList()) }
    var isLoadingParks by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var supportedParksCount by remember { mutableStateOf(0) }

    // Cargar las donaciones al iniciar
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            // Cargar donaciones
            isLoadingDonations = true
            try {
                val userDonations = suspendCancellableCoroutine<List<GetUserDonations.UserDonation>> { continuation ->
                    GetUserDonations().getUserDonations(
                        onSuccess = { result -> continuation.resume(result) },
                        onFailure = { continuation.resumeWithException(Exception("Error al cargar donaciones")) }
                    )
                }

                donations = userDonations.take(3)
                isLoadingDonations = false

                // Obtener nombres únicos de parques de las donaciones
                val uniqueParkNames = userDonations.map { it.parkName }.distinct()
                supportedParksCount = uniqueParkNames.size

                // Cargar detalles de los parques
                isLoadingParks = true
                val parks = mutableListOf<ParkDataA>()

                // Cargar los parques secuencialmente
                for (parkName in uniqueParkNames.take(10)) {
                    try {
                        val parkData = suspendCancellableCoroutine<ParkDataA> { continuation ->
                            getParkDetails(
                                parkName = parkName,
                                onSuccess = { data -> continuation.resume(data) },
                                onFailure = { error -> continuation.resumeWithException(Exception(error.toString())) }
                            )
                        }
                        parks.add(parkData)
                    } catch (e: Exception) {
                        // Ignoramos errores individuales de parques
                    }
                }

                supportedParks = parks
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoadingDonations = false
                isLoadingParks = false
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = { MainAppBar() },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Hola, $userName",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 26.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Resumen de actividad - Muestra una lista de parques con la situacion actual de los parques que ha apoyado el usuario
                        Card(
                            modifier = Modifier.fillMaxWidth().height(445.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = lightGrayColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, greenColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp).verticalScroll(scrollState)
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
                                if (isLoadingParks){
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ){
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = greenColor,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                } else if (supportedParks.isEmpty()) {
                                    Text(
                                        text = "No hay parques apoyados",
                                        fontFamily = SFProDisplayMedium,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                } else {
                                    supportedParks.forEach{ park ->
                                        ParkItem(name = park.nombre, status = park.situacion)
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Parques apoyados - Muestra un conteo de la cantidad de parques que ha apoyado el usuario
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

                                if (isLoadingDonations) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = supportedParksCount.toString().padStart(2, '0'),
                                        fontFamily = SFProDisplayBold,
                                        color = Color.White,
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Últimas donaciones - Ahora clickable para navegar a la sección de donaciones
                        // se muestran las ultimas tres donaciones que ha hecho el usuario
                        Card(
                            onClick = { navController.navigate("Donaciones") },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = lightGrayColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, greenColor)
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
                                        text = "Ultimas donaciones",
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

                                if(isLoading){
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ){
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = greenColor,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                } else if (donations.isEmpty())
                                {
                                 Text(
                                     text = "No hay donaciones",
                                     fontFamily = SFProDisplayMedium,
                                     fontSize = 12.sp,
                                     color = Color.Gray
                                 )
                                } else {
                                    donations.forEach { donation ->
                                        DonationItemCompact(
                                            name = donation.parkName,
                                            date = donation.date,
                                            amount = when (donation.type){
                                                "monetaria" -> "Monto: ${donation.details.substringAfter("Monto ")}"
                                                else -> donation.details
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                            }
                        }

                        // Botones para las donaciones
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = lightGrayColor),
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
                                    // Botón Monetaria - Ahora navega a donacionMonetaria
                                    Card(
                                        onClick = { navController.navigate("donacionMonetaria") },
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

                                    // Botón en especie - Ahora navega a donacionEspecie
                                    Card(
                                        onClick = { navController.navigate("donacionEspecie") {
                                            launchSingleTop = true
                                            popUpTo("Inicio") { saveState = true }
                                        }
                                                  },
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

// Funcion para desplegar cada parque en la seccion de Resumen de actividad
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
        colors = CardDefaults.cardColors(containerColor = lightGrayColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, greenColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = name,
                fontFamily = SFProDisplayBold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // Utilizamos un Column para forzar el salto de línea completo
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Primera línea: "Se encuentra en"
                Text(
                    text = "Se encuentra en",
                    fontFamily = SFProDisplayMedium,
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )

                // Segunda línea: Estado con color
                Text(
                    text = status,
                    fontFamily = SFProDisplayBold,
                    fontSize = 11.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Funcion para mostrar cada donacion en la seccion de ultimas donaciones
@Composable
fun DonationItemCompact(name: String, date: String, amount: String) {
    Card(
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = greenColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = name,
                fontFamily = SFProDisplayBold,
                fontSize = 11.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // Utilizamos un row para que la fecha y el producto o cantidad donada se vean en la misma linea
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$date |",
                    fontFamily = SFProDisplayMedium,
                    fontSize = 9.sp,
                    color = Color.White,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "$amount",
                    fontFamily = SFProDisplayMedium,
                    fontSize = 9.sp,
                    color = Color.White
                )
            }
        }
    }
}