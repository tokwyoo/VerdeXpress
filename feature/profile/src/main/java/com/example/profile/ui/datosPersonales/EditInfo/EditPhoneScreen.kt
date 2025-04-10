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
import com.example.profile.data.actualizarNumeroContacto
import com.example.profile.data.obtenerIDUsuario
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPhoneScreen(navController: NavController) {
    var newPhone by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf<String?>(null) }
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
                    newPhone = data?.numeroContacto ?: "" // Inicializar con el número actual
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
                    text = "Editar número de contacto",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {

                Text(
                    text = "Número de contacto actual",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = userData?.numeroContacto ?: "Cargando...",
                    modifier = Modifier.padding(bottom = 32.dp),
                    fontFamily = SFProDisplayMedium,
                    fontSize = 15.sp
                )

                Text(
                    text = "Nuevo número de contacto",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = newPhone,
                    onValueChange = {
                        newPhone = it
                        phoneError = null // Limpiar el error al cambiar el valor
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Número de contacto",
                            fontFamily = SFProDisplayMedium,
                            fontSize = 15.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = if (phoneError != null) Color.Red else Color.Gray.copy(alpha = 0.5f),
                        focusedBorderColor = if (phoneError != null) Color.Red else Color(0xFF78B153),
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
                if (phoneError != null) {
                    Text(
                        text = phoneError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 5.dp),
                        fontFamily = SFProDisplayMedium
                    )
                }

                Spacer(modifier = Modifier.height(44.dp))

                Button(
                    onClick = {
                        var isValid = true
                        if (newPhone.isBlank()) {
                            phoneError = "Se debe rellenar este campo"
                            isValid = false
                        } else if (!newPhone.all { it.isDigit() }) {
                            phoneError = "Solo se permiten números enteros"
                            isValid = false
                        } else if (newPhone.length != 10) {
                            phoneError = "El número debe tener 10 dígitos"
                            isValid = false
                        }

                        if (isValid && userId != null) {
                            actualizarNumeroContacto(
                                userId = userId,
                                nuevoNumero = newPhone,
                                onSuccess = {
                                    navController.navigateUp()
                                },
                                onFailure = { e ->
                                    Log.e("EditPhoneScreen", "Error al actualizar el número", e)
                                }
                            )
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
