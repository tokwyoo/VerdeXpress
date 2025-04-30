package com.example.profile.ui.datosCuenta.EditInfo

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
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.design.SFProDisplayBold
import com.example.design.SFProDisplayMedium
import com.example.profile.data.actualizarCorreoElectronicoConReautenticacion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmailScreen(navController: NavController) {
    var newEmail by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser? = auth.currentUser // Obtén el FirebaseUser
    val coroutineScope = rememberCoroutineScope()
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var updateEmailSuccess by remember { mutableStateOf(false) }
    var updateEmailError by remember { mutableStateOf<String?>(null) }


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
                    text = "Editar correo electrónico",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
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
                    text = "Correo electrónico actual",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = currentUser?.email ?: "Cargando...", // Muestra el correo de Firebase Auth
                    modifier = Modifier.padding(bottom = 32.dp),
                    fontFamily = SFProDisplayMedium,
                    fontSize = 15.sp
                )

                Text(
                    text = "Nuevo correo electrónico",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Correo electrónico",
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

                Text(
                    text = "Ingresa tu contraseña",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Ingresar contraseña",
                            fontFamily = SFProDisplayMedium,
                            fontSize = 15.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFF78B153),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        cursorColor = Color(0xFF78B153)
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = SFProDisplayMedium,
                        fontSize = 15.sp
                    ),
                    visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { currentPasswordVisible = !currentPasswordVisible },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color(0xFF9E9E9E)
                            )
                        ) {
                            Icon(
                                imageVector = if (currentPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = if (currentPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(44.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            actualizarCorreoElectronicoConReautenticacion(
                                nuevoCorreo = newEmail,
                                contrasena = currentPassword,
                                onSuccess = {
                                    updateEmailSuccess = true
                                    updateEmailError = null
                                    Log.d(
                                        "EditEmailScreen",
                                        "Se solicitó la verificación del nuevo correo electrónico."
                                    )
                                },
                                onFailure = { e ->
                                    updateEmailSuccess = false
                                    updateEmailError = e.localizedMessage
                                        ?: "Error al solicitar la verificación del correo electrónico."
                                    Log.e(
                                        "EditEmailScreen",
                                        "Error al solicitar la verificación del correo electrónico.",
                                        e
                                    )
                                }
                            )

                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF78B153)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = newEmail.isNotBlank() && currentPassword.isNotBlank()
                ) {
                    Text(
                        text = "Verificar nuevo correo",
                        fontFamily = SFProDisplayBold,
                        fontSize = 18.sp
                    )
                }

                // Se muestra un mensaje de éxito (se logró mandar el correo de verificación)
                if (updateEmailSuccess) {
                    AlertDialog(
                        onDismissRequest = { updateEmailSuccess = false
                            val auth = FirebaseAuth.getInstance()
                            auth.signOut()
                            navController.navigate("signIn") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                                restoreState = false
                            }
                        },
                        title = { Text(
                            text = "Éxito",
                            fontFamily = SFProDisplayBold,
                            fontSize = 18.sp
                        ) },
                        text = { Text(
                            text = "Se ha enviado un correo de verificación a: $newEmail. " +
                                    "Por favor, revisa tu bandeja de entrada y haz clic en el enlace para confirmar " +
                                    "tu nuevo correo electrónico. Una vez confirmado, se cerrará tu sesión y deberás iniciar sesión nuevamente con tu nuevo correo.",
                            fontFamily = SFProDisplayBold,
                            fontSize = 16.sp) },
                        confirmButton = {
                            TextButton(onClick = {
                                updateEmailSuccess = false
                                val auth = FirebaseAuth.getInstance()
                                auth.signOut()
                                navController.navigate("signIn") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF78B153)
                                ),
                                shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "Aceptar",
                                    fontFamily = SFProDisplayBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    )
                }

                // Mostrar mensaje en caso de error error
                if (updateEmailError != null) {
                    AlertDialog(
                        onDismissRequest = { updateEmailError = null },
                        title = { Text("Error") },
                        text = { Text(
                            text = updateEmailError!!,
                            fontFamily = SFProDisplayBold,
                            fontSize = 18.sp) },
                        confirmButton = {
                            TextButton(onClick = { updateEmailError = null }) {
                                Text("Aceptar")
                            }
                        }
                    )
                }
            }
        }
    }
}