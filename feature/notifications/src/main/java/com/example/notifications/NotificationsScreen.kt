package com.example.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import com.example.donations.data.notificaciones.GetUserNotifications
import com.example.design.R
import com.example.donations.ui.notificaciones.NotificationFilterManager
import com.example.donations.R as RD
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    navController: NavController,
    showFilter: Boolean = false
) {
    NotificationFilterManager(
        navController = navController,
        initialShowFilter = showFilter
    )

    // Estado para controlar la visibilidad del filtro
    var isFilterVisible by rememberSaveable { mutableStateOf(showFilter) }

    // Estados para gestionar las notificaciones
    var isLoading by remember { mutableStateOf(true) }
    var notificationsList by remember { mutableStateOf<List<GetUserNotifications.UserNotification>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Instancia del gestor de notificaciones
    val userNotificationManager = remember { GetUserNotifications() }

    // Cargar las notificaciones al iniciar la pantalla
    LaunchedEffect(Unit) {
        userNotificationManager.getUserNotifications(
            onSuccess = { notifications ->
                notificationsList = notifications
                isLoading = false
            },
            onFailure = { exception ->
                errorMessage = "Error al cargar notificaciones: ${exception.message}"
                isLoading = false
            }
        )
    }

    // Limpiar listeners al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            userNotificationManager.removeListeners()
        }
    }

    // Estados para los filtros aplicados
    var appliedSortFilter by remember { mutableStateOf("") }
    var appliedTimeFilter by remember { mutableStateOf("") }
    var appliedTypeFilter by remember { mutableStateOf("") }

    // Observar cambios en los filtros aplicados
    LaunchedEffect(navController) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Map<String, String>>("appliedNotificationFilters")
            ?.observeForever { filters ->
                if (filters != null) {
                    appliedSortFilter = filters["sort"] ?: ""
                    appliedTimeFilter = filters["time"] ?: ""
                    appliedTypeFilter = filters["type"] ?: ""

                    // Aplicar los filtros a la lista de notificaciones
                    if (isLoading || errorMessage != null) return@observeForever

                    var filteredList = notificationsList

                    // Aplicar filtro de ordenamiento por nombre (A-Z o Z-A)
                    filteredList = when (appliedSortFilter) {
                        "A-Z" -> filteredList.sortedBy { it.titulo }
                        "Z-A" -> filteredList.sortedByDescending { it.titulo }
                        else -> filteredList
                    }

                    // Aplicar filtro de tiempo
                    filteredList = when (appliedTimeFilter) {
                        "Más recientes" -> filteredList.sortedByDescending { it.fecha }
                        "Más antiguas" -> filteredList.sortedBy { it.fecha }
                        else -> filteredList
                    }

                    notificationsList = filteredList
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
                    text = "Historial de notificaciones",
                    style = TextStyle(
                        fontSize = 25.sp,
                        color = Color.Black
                    ),
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )

                // Botón de filtro
                Image(
                    painter = painterResource(id = RD.drawable.filter_list),
                    contentDescription = "Filtrar notificaciones",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "showFilterNotification",
                                true
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo con contador de notificaciones no leídas
            val unreadCount = notificationsList.count { !it.leido }
            if (unreadCount > 0) {
                Text(
                    text = "Tienes $unreadCount notificaciones no leídas.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black
                    ),
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                )
            }

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
                                    userNotificationManager.getUserNotifications(
                                        onSuccess = { notifications ->
                                            notificationsList = notifications
                                            isLoading = false
                                        },
                                        onFailure = { exception ->
                                            errorMessage = "Error al cargar notificaciones: ${exception.message}"
                                            isLoading = false
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
                notificationsList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tienes notificaciones.",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Gray
                            ),
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
                        )
                    }
                }
                else -> {
                    // Lista deslizable de notificaciones
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(notificationsList) { notification ->
                            NotificationItem(
                                notification = notification,
                                onMarkAsRead = {
                                    // Implementar marcado como leído
                                    userNotificationManager.markAsRead(notification.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Componente individual para mostrar cada notificación
@Composable
fun NotificationItem(
    notification: GetUserNotifications.UserNotification,
    onMarkAsRead: () -> Unit = {}
) {
    val verde = Color(0xFF78B153)

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
                color = verde,
                shape = RoundedCornerShape(size = 8.dp)
            )
            .clickable {
                if (!notification.leido) {
                    onMarkAsRead()
                }
            }
    ) {
        // Indicador verde de no leído
        if (!notification.leido) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color = verde, shape = RoundedCornerShape(6.dp))
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
            )
        }

        // Contenido de la notificación
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Título de la notificación
            Text(
                text = notification.titulo,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black
                ),
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Mensaje de la notificación
            Text(
                text = notification.mensaje,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Black
                ),
                fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha formateada
            val formattedDate = formatTimestamp(notification.fecha)
            Text(
                text = formattedDate,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Gray
                ),
                fontFamily = FontFamily(Font(R.font.sf_pro_display_medium))
            )
        }
    }
}

// Función para formatear timestamp a formato legible
fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("es", "ES"))
    return format.format(date)
}