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
fun EditPhoneScreen(navController: NavController) {
    var newPhone by remember { mutableStateOf("") }
    val currentPhone = "+52 662 XXX XXXX"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar número de contacto",
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
                text = "Teléfono actual",
                fontFamily = SFProDisplayBold,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = currentPhone,
                fontFamily = SFProDisplayMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Nuevo número de contacto",
                fontFamily = SFProDisplayBold,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextField(
                value = newPhone,
                onValueChange = { newPhone = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Teléfono de contacto",
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