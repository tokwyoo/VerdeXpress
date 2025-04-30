package com.example.donations.ui.inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
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
import com.example.donations.data.inicio.GetUserDonations
import com.example.donations.R as RD
import com.example.design.R

@Composable
fun DonationsScreen(
    navController: NavController,
    showDialog: Boolean = false,
    showFilter: Boolean = false
) {

    DonationFilterManager(
        navController = navController,
        initialShowFilter = showFilter
    )

    // Estado para controlar la visibilidad de los diálogos
    var isDialogVisible by rememberSaveable { mutableStateOf(showDialog) }
    var isFilterVisible by rememberSaveable { mutableStateOf(showFilter) }

    // Estados para gestionar las donaciones
    var isLoading by remember { mutableStateOf(true) }
    var donationsList by remember { mutableStateOf<List<GetUserDonations.UserDonation>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Instancia del gestor de donaciones
    val userDonationManager = remember { GetUserDonations() }

    // Cargar las donaciones al iniciar la pantalla
    LaunchedEffect(Unit) {
        userDonationManager.getUserDonations(
            onSuccess = { donations ->
                donationsList = donations
                isLoading = false
            },
            onFailure = { exception ->
                errorMessage = "Error al cargar donaciones: ${exception.message}"
                isLoading = false
            }
        )
    }

    // Limpiar listeners al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            userDonationManager.removeListeners()
        }
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

    // Estados para los filtros aplicados
    var appliedSortFilter by remember { mutableStateOf("") }
    var appliedTimeFilter by remember { mutableStateOf("") }

    // Observar cambios en los filtros aplicados
    LaunchedEffect(navController) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Map<String, String>>("appliedFilters")
            ?.observeForever { filters ->
                if (filters != null) {
                    appliedSortFilter = filters["sort"] ?: ""
                    appliedTimeFilter = filters["time"] ?: ""

                    // Aplicar los filtros a la lista de donaciones
                    if (isLoading || errorMessage != null) return@observeForever

                    var filteredList = donationsList

                    // Aplicar filtro de ordenamiento por nombre (A-Z o Z-A)
                    filteredList = when (appliedSortFilter) {
                        "A-Z" -> filteredList.sortedBy { it.parkName }
                        "Z-A" -> filteredList.sortedByDescending { it.parkName }
                        else -> filteredList
                    }

                    // Aplicar filtro de tiempo (si no hay filtro por nombre o después de aplicarlo)
                    filteredList = when (appliedTimeFilter) {
                        "Más recientes" -> filteredList.sortedByDescending { it.rawDate }
                        "Más antiguas" -> filteredList.sortedBy { it.rawDate }
                        else -> filteredList
                    }

                    donationsList = filteredList
                }
            }
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

            // Mostrar el contenido según el estado
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF78B153))
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = errorMessage ?: "Error desconocido",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.Red
                                ),
                                fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Intentar de nuevo",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color(0xFF78B153),
                                    textDecoration = TextDecoration.Underline
                                ),
                                fontFamily = FontFamily(Font(R.font.sf_pro_display_medium)),
                                modifier = Modifier.clickable {
                                    isLoading = true
                                    errorMessage = null
                                    userDonationManager.getUserDonations(
                                        onSuccess = { donations ->
                                            donationsList = donations
                                            isLoading = false
                                        },
                                        onFailure = { exception ->
                                            errorMessage = "Error al cargar donaciones: ${exception.message}"
                                            isLoading = false
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
                donationsList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No has realizado donaciones todavía",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Gray
                            ),
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                        )
                    }
                }
                else -> {
                    // Lista deslizable de donaciones
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(donationsList) { donation ->
                            DonationItem(
                                donation = donation,
                                onDetailsClick = {
                                    navController.navigate("DetalleDonacion/${donation.type}/${donation.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Componente individual para mostrar cada donación
@Composable
fun DonationItem(
    donation: GetUserDonations.UserDonation,
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
                ),
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