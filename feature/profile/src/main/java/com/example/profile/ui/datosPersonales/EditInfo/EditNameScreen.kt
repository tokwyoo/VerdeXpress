package com.example.profile.ui.datosPersonales.EditInfo

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.design.SFProDisplayBold
import com.example.design.SFProDisplayMedium
import com.example.profile.data.UserData
import com.example.profile.data.actualizarNombreApellidoUsuario
import com.example.profile.data.obtenerIDUsuario
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNameScreen(navController: NavController) {
    var newName by remember { mutableStateOf("") }
    var newLastName by remember { mutableStateOf("") }

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
                    newName = data?.nombre ?: "" // Inicializar los campos con los valores actuales
                    newLastName = data?.apellidos ?: ""
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
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Reduje el padding inferior del encabezado
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
                    text = "Editar nombre de usuario",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Nombre del usuario actual",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "${userData?.nombre ?: ""} ${userData?.apellidos ?: ""}",
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontFamily = SFProDisplayMedium,
                    fontSize = 18.sp
                )

                Text(
                    text = "Nuevo nombre del usuario",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
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
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFF78B153),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = SFProDisplayMedium,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nuevo apellido del usuario",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = newLastName,
                    onValueChange = { newLastName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Apellido del usuario",
                            fontFamily = SFProDisplayMedium,
                            fontSize = 15.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFF78B153),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = SFProDisplayMedium,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(44.dp))

                Button(
                    onClick = {
                        if (userId != null) {
                            actualizarNombreApellidoUsuario(
                                userId = userId,
                                nuevoNombre = newName.takeIf { it != userData?.nombre }, // Solo actualizar si cambió
                                nuevoApellido = newLastName.takeIf { it != userData?.apellidos }, // Solo actualizar si cambió
                                onSuccess = {
                                    navController.navigateUp() // Regresar a la pantalla anterior al confirmar
                                },
                                onFailure = { e ->
                                    Log.e("EditNameScreen", "Error al actualizar nombre/apellido", e)
                                }
                            )
                        } else {
                            Log.e("EditNameScreen", "Error: userId es nulo")
                        }
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
}