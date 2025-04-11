package com.example.donations.ui.notificaciones

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.design.R
import com.example.donations.ui.inicio.LocalBottomBarState

// Creamos un CompositionLocal para compartir el estado del BottomBar
val LocalBottomBarState = compositionLocalOf { mutableStateOf(true) }

/**
 * Componente Composable para gestionar los filtros de notificaciones.
 * Integra todas las funcionalidades de filtrado en un solo componente.
 */
@Composable
fun NotificationFilterManager(
    navController: NavController,
    initialShowFilter: Boolean = false
) {
    // Estado para controlar la visibilidad del panel de filtros
    var isFilterVisible by remember { mutableStateOf(initialShowFilter) }

    // Estados para los filtros
    var appliedSortFilter by remember { mutableStateOf("") }
    var appliedTimeFilter by remember { mutableStateOf("") }

    // Acceder al estado del BottomBar
    val showBottomBar = LocalBottomBarState.current

    // Observar cambios en el estado para mostrar el filtro
    LaunchedEffect(navController) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("showFilterNotification")
            ?.observeForever { shouldShow ->
                isFilterVisible = shouldShow == true
                // Ocultar el BottomBar cuando se muestra el filtro
                if (shouldShow == true) {
                    showBottomBar.value = false
                } else {
                    showBottomBar.value = true
                }
            }
    }

    // Observar cambios en los filtros aplicados
    LaunchedEffect(navController) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Map<String, String>>("appliedNotificationFilters")
            ?.observeForever { filters ->
                if (filters != null) {
                    appliedSortFilter = filters["sort"] ?: ""
                    appliedTimeFilter = filters["time"] ?: ""
                }
            }
    }

    // Limpiar observadores al salir
    DisposableEffect(navController) {
        onDispose {
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("showFilterNotification")
            navController.currentBackStackEntry?.savedStateHandle?.remove<Map<String, String>>("appliedNotificationFilters")
        }
    }

    // Mostrar el panel de filtros si es visible
    if (isFilterVisible) {
        NotificationFilterPanel(
            initialSort = appliedSortFilter,
            initialTime = appliedTimeFilter,
            onDismiss = {
                isFilterVisible = false
                navController.currentBackStackEntry?.savedStateHandle?.set("showFilterNotification", false)
            },
            onApply = { filters ->
                navController.currentBackStackEntry?.savedStateHandle?.set("appliedNotificationFilters", filters)
                isFilterVisible = false
                navController.currentBackStackEntry?.savedStateHandle?.set("showFilterNotification", false)
            }
        )
    }
}

/**
 * Panel de filtros deslizante para las notificaciones.
 */
@Composable
fun NotificationFilterPanel(
    initialSort: String = "",
    initialTime: String = "",
    initialType: String = "",
    onDismiss: () -> Unit,
    onApply: (Map<String, String>) -> Unit
) {
    var selectedSort by remember { mutableStateOf(initialSort) }
    var selectedTime by remember { mutableStateOf(initialTime) }

    // Actualizar los valores iniciales cuando cambien
    LaunchedEffect(initialSort, initialTime, initialType) {
        selectedSort = initialSort
        selectedTime = initialTime
    }

    // Definir colores
    val gris = Color(0xFFEAEAEA)
    val verde = Color(0xFF78B153) // Color verde de la app

    // Estado para la animación
    val visibleState = remember { MutableTransitionState(false) }
    visibleState.targetState = true

    // Un Box para cubrir toda la pantalla cuando el panel está abierto
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10f)
    ) {
        // Fondo semi-transparente que cubre el resto de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .zIndex(10f)
                .clickable(onClick = onDismiss)
        )

        // Panel deslizante de filtros
        AnimatedVisibility(
            visibleState = visibleState,
            enter = slideInHorizontally(
                initialOffsetX = { it }, // Comienza fuera de la pantalla (derecha)
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it }, // Sale de la pantalla (derecha)
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier
                .fillMaxHeight()
                .width(306.dp)
                .align(Alignment.CenterEnd)
                .zIndex(100f)
        ) {
            Surface(
                modifier = Modifier.fillMaxHeight(),
                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Header verde
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(verde)
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            "Filtro de notificaciones",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = FontFamily(androidx.compose.ui.text.font.Font(R.font.sf_pro_display_bold))
                        )
                    }

                    // Contenido del filtro
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Ordenamiento por nombre
                        Text(
                            "Nombre",
                            fontFamily = FontFamily(androidx.compose.ui.text.font.Font(R.font.sf_pro_display_medium)),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            FilterButton(
                                text = "A-Z",
                                selected = selectedSort == "A-Z",
                                onClick = { selectedSort = if (selectedSort == "A-Z") "" else "A-Z" },
                                activeColor = verde,
                                inactiveColor = gris
                            )

                            FilterButton(
                                text = "Z-A",
                                selected = selectedSort == "Z-A",
                                onClick = { selectedSort = if (selectedSort == "Z-A") "" else "Z-A" },
                                activeColor = verde,
                                inactiveColor = gris
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        // Ordenamiento por tiempo
                        Text(
                            "Tiempo",
                            fontFamily = FontFamily(androidx.compose.ui.text.font.Font(R.font.sf_pro_display_medium)),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            FilterButton(
                                text = "Más recientes",
                                selected = selectedTime == "Más recientes",
                                onClick = { selectedTime = if (selectedTime == "Más recientes") "" else "Más recientes" },
                                activeColor = verde,
                                inactiveColor = gris,
                                modifier = Modifier.weight(1f)
                            )

                            FilterButton(
                                text = "Más antiguas",
                                selected = selectedTime == "Más antiguas",
                                onClick = { selectedTime = if (selectedTime == "Más antiguas") "" else "Más antiguas" },
                                activeColor = verde,
                                inactiveColor = gris,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp))


                        Spacer(modifier = Modifier.weight(1f))

                        // Botones de acción en la parte inferior
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    selectedSort = ""
                                    selectedTime = ""
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, verde),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = verde
                                )
                            ) {
                                Text(
                                    text = "Restablecer",
                                    fontFamily = FontFamily(androidx.compose.ui.text.font.Font(R.font.sf_pro_display_medium)),
                                    fontSize = 14.sp
                                )
                            }

                            Button(
                                onClick = {
                                    val filters = mapOf(
                                        "sort" to selectedSort,
                                        "time" to selectedTime,
                                    )
                                    onApply(filters)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = verde
                                )
                            ) {
                                Text(
                                    text = "Aplicar",
                                    fontFamily = FontFamily(androidx.compose.ui.text.font.Font(R.font.sf_pro_display_medium)),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Reusing the FilterButton component that exists in your code
@Composable
fun FilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(40.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) activeColor else inactiveColor,
            contentColor = if (selected) Color.White else Color.Black
        )
    ) {
        Text(
            text = text,
            fontFamily = FontFamily(androidx.compose.ui.text.font.Font(R.font.sf_pro_display_medium)),
            fontSize = 14.sp
        )
    }
}