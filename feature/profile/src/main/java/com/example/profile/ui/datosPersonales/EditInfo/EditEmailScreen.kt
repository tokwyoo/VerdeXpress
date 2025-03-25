package com.example.profile.ui.datosPersonales.EditInfo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.design.SFProDisplayBold
import com.example.design.SFProDisplayMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmailScreen(navController: NavController) {
    var newEmail by remember { mutableStateOf("") }
    val currentEmail = "email@email.com"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar correo electrónico",
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
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Correo electrónico actual",
                fontFamily = SFProDisplayBold,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = currentEmail,
                fontFamily = SFProDisplayMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Nuevo correo electrónico",
                fontFamily = SFProDisplayBold,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = newEmail,
                onValueChange = { newEmail = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Correo electrónico",
                        fontFamily = SFProDisplayMedium
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedIndicatorColor = Color(0xFF78B153)
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // TODO: Implement save logic
                    navController.navigateUp()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF78B153)
                )
            ) {
                Text(
                    "Confirmar",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}