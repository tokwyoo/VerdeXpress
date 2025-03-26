package com.example.donations.ui.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController

@Composable
fun FilterDonationScreen(navController: NavController, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f) // Ajusta el ancho según la imagen
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Filtro de notificaciones",
                        style = MaterialTheme.typography.h6
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Nombre")

                    Row {
                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "A-Z")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "Z-A")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Tiempo")

                    Row {
                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "Más recientes")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "Más antiguas")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {  navController.navigate("Donaciones") }) {
                            Text(text = "Reestablecer")
                        }
                        Button(onClick = { /*TODO*/ }) {
                            Text(text = "Aplicar")
                        }
                    }
                }
            }
        }
    }
}