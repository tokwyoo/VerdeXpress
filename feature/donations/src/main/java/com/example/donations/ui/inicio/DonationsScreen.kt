package com.example.donations.ui.inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.donations.R as RD
import com.example.design.R

data class Donation(
    val parkName: String,
    val details: String,
    val date: String
)

@Composable
fun DonationsScreen(
    navController: NavController,
    showDialog: Boolean = false,
    showFilter: Boolean = false
) {
    // Estado para controlar la visibilidad de los diálogos
    var isDialogVisible by rememberSaveable { mutableStateOf(showDialog) }
    var isFilterVisible by rememberSaveable { mutableStateOf(showFilter) }

    // Lista de donaciones de ejemplo
    val donationsList = remember {
        listOf(
            Donation("Las Minitas", "Monto XX,XXX", "09 Marzo 2025"),
            Donation("Botánico", "Recurso donado \"Semillas\"", "09 Marzo 2025"),
            Donation("El Mirador", "Recurso donado \"Herramientas\"", "01 Marzo 2025"),
            Donation("Los Pinos", "Monto XX,XXX", "26 Febrero 2025"),
            Donation("La Cañada", "Monto XX,XXX", "26 Febrero 2025"),
            Donation("El Bosque", "Monto XX,XXX", "26 Febrero 2025"),
            Donation("Las Flores", "Monto XX,XXX", "26 Febrero 2025"),
            Donation("El Lago", "Monto XX,XXX", "26 Febrero 2025")
        )
    }

    // Observar cambios en el estado de navegación para mostrar el diálogo de donación
    LaunchedEffect(navController) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("showDonationDialog")
            ?.observeForever { shouldShow ->
                isDialogVisible = shouldShow == true
            }
    }

    // Mostrar diálogo de tipo de donación si es necesario
    if (isDialogVisible) {
        DonationTypeDialog(
            onDismiss = {
                isDialogVisible = false
                navController.currentBackStackEntry?.savedStateHandle?.set("showDonationDialog", false)
            },
            navController = navController
        )
    }

    // Observar cambios en el estado de navegación para mostrar la pantalla de filtro
    LaunchedEffect(navController) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("showFilterDonation")
            ?.observeForever { shouldShow ->
                isFilterVisible = shouldShow == true
            }
    }

    // Mostrar pantalla de filtro si es necesario
    if (isFilterVisible) {
        FilterDonationScreen(
            onDismiss = {
                isFilterVisible = false
                navController.currentBackStackEntry?.savedStateHandle?.set("showFilterDonation", false)
            },
            navController = navController
        )
    }

    // Estructura principal de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF))
    ) {
        MainAppBar()

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 26.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Título
                Text(
                    text = "Historial de donaciones",
                    style = TextStyle(
                        fontSize = 25.sp,
                        color = Color.Black
                    ),
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))

                )

                // Botón de filtro
                Image(
                    painter = painterResource(id = RD.drawable.filter_list),
                    contentDescription = "Filtrar donaciones",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "showFilterDonation",
                                true
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo
            Text(
                text = "Últimas donaciones hechas",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black
                ),
                fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))

            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lista deslizable de donaciones
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(donationsList) { donation ->
                    DonationItem(
                        donation = donation,
                        onDetailsClick = {
                            // Aquí puedes agregar la navegación a la pantalla de detalles
                            // navController.navigate("donation_details/${donation.parkName}")
                        }
                    )
                }
            }
        }
    }
}

// Componente individual para mostrar cada donación
@Composable
fun DonationItem(
    donation: Donation,
    onDetailsClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(size = 8.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF78B153),
                shape = RoundedCornerShape(size = 8.dp)
            )
    ) {
        // Contenido de la donación
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Nombre del parque
            Text(
                text = "Parque ${donation.parkName}",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black
                ) ,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))

                )
            Spacer(modifier = Modifier.height(4.dp))
            // Detalles de la donación
            Text(
                text = donation.details,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Black
                ),
                fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Fila inferior con fecha y botón de detalles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fecha
                Text(
                    text = donation.date,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Black
                    ),
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                )

                // Botón de detalles
                Text(
                    text = "Ver detalles",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Black,
                        textDecoration = TextDecoration.Underline

                    ),

                    fontFamily = FontFamily(Font(R.font.sf_pro_display_medium)),
                    modifier = Modifier.clickable { onDetailsClick() }
                )
            }
        }
    }
}
