package com.example.profile.ui.inicio

import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.design.SFProDisplayBold
import com.example.design.SFProDisplayMedium
import com.example.profile.data.UserData
import com.example.profile.data.obtenerIDUsuario
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid

    var userData by remember { mutableStateOf<UserData?>(null) } // Estado para almacenar los datos del usuario

    LaunchedEffect(userId) { // Ejecutar solo cuando userId cambie
        if (userId != null) {
            obtenerIDUsuario(
                userId = userId,
                onSuccess = { data ->
                    userData = data
                },
                onFailure = { exception ->
                    // Manejar el error
                    Log.e("ProfileScreen", "Error al obtener datos del usuario", exception)
                }
            )
        }
    }

    Scaffold(
        containerColor = Color.White,  // Agregado para establecer fondo blanco
        topBar = { MainAppBar() },
        bottomBar = {
            // botón de cerrar sesión, cambiar tamaño y posición
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        auth.signOut()
                        navController.navigate("signIn") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                            restoreState = false
                        }
                    },
                    modifier = Modifier
                        .width(205.dp)
                        .height(70.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF78B153)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Cerrar sesión",
                        fontFamily = SFProDisplayBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Titulo
                Text(
                    text = "Perfil",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 26.dp)
                )

                // recuadro de info del perfil
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // icono de la foto de perfil
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = Color.LightGray
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        // info del user
                        Column(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = userData?.nombre ?: "Cargando...", // Muestra el nombre y mientras carga los datos "Cargando..."
                                fontFamily = SFProDisplayBold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = userData?.correoElectronico ?: "Cargando...", // Muestra el correo y mientras carga los datos "Cargando..."
                                fontFamily = SFProDisplayMedium,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // opciones del perfil
                MenuOption(
                    title = "Datos personales",
                    description = "Información del usuario.",
                    onClick = { navController.navigate("datosPersonales") }
                )

                MenuOption(
                    title = "Datos de la cuenta",
                    description = "Información de la cuenta del usuario.",
                    onClick = { navController.navigate("datosCuenta") }
                )

                MenuOption(
                    title = "Eliminar cuenta",
                    description = "Elimina la cuenta de manera definitiva.",
                    onClick = { navController.navigate("eliminarCuenta") }
                )
            }
        }
    }
}

@Composable
fun MenuOption(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    fontFamily = SFProDisplayMedium,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Ver más",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun BottomNavigationItem(
    icon: ImageVector,
    label: String,
    selected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color(0xFF78B153) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontFamily = SFProDisplayBold,
            fontSize = 12.sp,
            color = if (selected) Color(0xFF78B153) else Color.Gray
        )
    }
}