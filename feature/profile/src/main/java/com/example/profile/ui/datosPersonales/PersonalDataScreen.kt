package com.example.profile.ui.datosPersonales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.SFProDisplayBold
import com.example.design.SFProDisplayMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    "Datos personales",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF78B153),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            PersonalInfoItem(
                icon = Icons.Default.Person,
                title = "Nombre y apellido",
                value = "Emilio Pérez",
                onEdit = {
                    navController.navigate("editName")  // Ruta actualizada
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PersonalInfoItem(
                icon = Icons.Default.Phone,
                title = "Teléfono",
                value = "+52 662 XXX XXXX",
                onEdit = {
                    navController.navigate("editPhone")  // Ruta actualizada
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PersonalInfoItem(
                icon = Icons.Default.Email,
                title = "E-mail",
                value = "email@email.com",
                onEdit = {
                    navController.navigate("editEmail")  // Ruta actualizada
                }
            )
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