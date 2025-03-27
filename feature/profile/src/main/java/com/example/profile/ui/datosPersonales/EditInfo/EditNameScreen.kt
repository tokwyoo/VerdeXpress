package com.example.profile.ui.datosPersonales.EditInfo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.SFProDisplayBold
import com.example.design.SFProDisplayMedium
import com.example.design.MainAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNameScreen(navController: NavController) {
    var newName by remember { mutableStateOf("") }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
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
                    text = "Editar nombre del usuario",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                )
            }

            Text(
                text = "Nombre del usuario actual",
                fontFamily = SFProDisplayBold,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Emilio Pérez",
                modifier = Modifier.padding(bottom = 32.dp),
                fontFamily = SFProDisplayMedium,
                fontSize = 15.sp
            )

            Text(
                text = "Nuevo nombre del usuario",
                fontFamily = SFProDisplayBold,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Nombre del usuario",
                        fontFamily = SFProDisplayMedium,
                        fontSize = 15.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    // Color gris para el estado base del borde
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    // Color verde para el estado de enfoque
                    focusedBorderColor = Color(0xFF78B153),
                    // Color de fondo blanco en todos los estados
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                singleLine = true,
                // Aumentar el radio de redondeo para un borde más suave
                shape = RoundedCornerShape(8.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = SFProDisplayMedium,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // TODO: Implement name update logic
                    navController.navigateUp()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF78B153)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Confirmar",
                    fontFamily = SFProDisplayBold,
                    fontSize = 18.sp
                )
            }
        }
    }
}