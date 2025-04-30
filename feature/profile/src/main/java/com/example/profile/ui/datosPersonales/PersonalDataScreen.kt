package com.example.profile.ui.datosPersonales

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.design.SFProDisplayBold
import com.example.design.SFProDisplayMedium
import com.example.profile.data.UserData
import com.example.profile.data.obtenerIDUsuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser? = auth.currentUser // Obtén el FirebaseUser

    var userData by remember { mutableStateOf<UserData?>(null) } // Estado para almacenar los datos del usuario

    LaunchedEffect(currentUser?.uid) { // Ejecutar solo cuando el ID del usuario cambie
        if (currentUser?.uid != null) {
            obtenerIDUsuario(
                userId = currentUser.uid,
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
        containerColor = Color.White,
        topBar = {
            MainAppBar()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }
                Text(
                    text = "Datos personales",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }



            // Main Content
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(8.dp)) // Add space below the title

                PersonalInfoItem(
                    icon = Icons.Default.Person,
                    title = "Nombre y apellido",
                    value = "${userData?.nombre ?: ""} ${userData?.apellidos ?: ""}",
                    onEdit = {
                        navController.navigate("editName")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PersonalInfoItem(
                    icon = Icons.Default.Phone,
                    title = "Teléfono",
                    value = userData?.numeroContacto ?: "Cargando...",
                    onEdit = {
                        navController.navigate("editPhone")
                    }
                )

            }
        }
    }
}

@Composable
fun PersonalInfoItem(
    icon: ImageVector,
    title: String,
    value: String,
    onEdit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF78B153),
                modifier = Modifier.size(32.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp)
            ) {
                Text(
                    text = title,
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = value,
                    fontFamily = SFProDisplayMedium,
                    color = Color.Gray,
                    fontSize = 15.sp
                )
            }

            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF78B153)
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text(
                    "Editar",
                    fontFamily = SFProDisplayBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}