package com.example.profile.ui.datosCuenta

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDataScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        TopAppBar(
            title = { Text("Datos de la cuenta") },
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
            AccountInfoItem(
                icon = Icons.Default.Email,
                title = "Contraseña",
                description = "Contraseña",
                onEdit = { /* Acción para editar contraseña */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AccountInfoItem(
                icon = Icons.Default.Info,
                title = "Datos 2",
                description = "Información de datos 2.",
                onEdit = { /* Acción para editar datos 2 */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AccountInfoItem(
                icon = Icons.Default.Settings,
                title = "Datos 3",
                description = "Información de datos 3.",
                onEdit = { /* Acción para editar datos 3 */ }
            )
        }

        // Spacer to push bottom navigation to the bottom
        Spacer(modifier = Modifier.weight(1f))

    }
}

@Composable
fun AccountInfoItem(
    icon: ImageVector,
    title: String,
    description: String,
    onEdit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF78B153)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = Color.Gray
                )
            }

            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF78B153)
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Editar")
            }
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
            fontSize = 12.sp,
            color = if (selected) Color(0xFF78B153) else Color.Gray
        )
    }
}