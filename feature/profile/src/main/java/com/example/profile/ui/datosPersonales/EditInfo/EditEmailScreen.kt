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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.profile.data.actualizarCorreoFirestore
import com.example.profile.data.enviarCorreoVerificacionNuevoCorreo
import com.example.profile.data.obtenerIDUsuario
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmailScreen(navController: NavController) {
    var newEmail by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid

    var userData by remember { mutableStateOf<UserData?>(null) }
    var verificationRequested by remember { mutableStateOf(false) } // Estado para indicar si se solicitó la verificación

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId != null) {
            obtenerIDUsuario(
                userId = userId,
                onSuccess = { data ->
                    userData = data
                    newEmail = data?.correoElectronico ?: ""
                },
                onFailure = { exception ->
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
                    text = userData?.correoElectronico ?: "Cargando...",
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

                Spacer(modifier = Modifier.height(44.dp))

                Button(
                    onClick = {
                        if (!newEmail.isNullOrBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                            if (userId != null) {
                                coroutineScope.launch {
                                    Log.d("EditEmailScreen", "Intentando enviar verificación a: $newEmail")
                                    enviarCorreoVerificacionNuevoCorreo(
                                        nuevoCorreo = newEmail,
                                        onSuccess = {
                                            verificationRequested = true
                                            emailError = "Se ha enviado un correo de verificación a $newEmail. Por favor, revisa tu bandeja de entrada y sigue las instrucciones."
                                        },
                                        onFailure = { e ->
                                            Log.e("EditEmailScreen", "Error al solicitar verificación", e)
                                            emailError = e.localizedMessage ?: "Error al solicitar la verificación del correo electrónico."
                                            if (e.message?.contains("requires recent login", ignoreCase = true) == true) {
                                                // **Aquí podrías navegar al flujo de reautenticación**
                                                emailError = "Esta operación requiere que vuelvas a iniciar sesión por seguridad."
                                            }
                                        }
                                    )
                                }
                            } else {
                                Log.e("EditEmailScreen", "Error: userId es nulo")
                            }
                        } else {
                            emailError = "Por favor, introduce un correo electrónico válido."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF78B153)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !verificationRequested // Deshabilitar el botón después de solicitar la verificación
                ) {
                    Text(
                        text = if (verificationRequested) "Verificación Enviada" else "Confirmar Nuevo Correo",
                        fontFamily = SFProDisplayBold,
                        fontSize = 18.sp
                    )
                }

                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (verificationRequested) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val currentUser = auth.currentUser
                                // **Importante:** Después de que el usuario haga clic en el enlace de verificación
                                // y regresa a la app, necesitamos comprobar si el *nuevo* correo
                                // (que está ahora en currentUser.email) ha sido verificado.
                                currentUser?.reload()
                                    ?.await() // Recargar la información del usuario

                                if (currentUser?.isEmailVerified == true && currentUser.email == newEmail && userId != null) {
                                    actualizarCorreoFirestore(
                                        userId = userId,
                                        nuevoCorreo = newEmail,
                                        onSuccess = {
                                            navController.navigateUp()
                                        },
                                        onFailure = { e ->
                                            Log.e(
                                                "EditEmailScreen",
                                                "Error al actualizar correo en Firestore",
                                                e
                                            )
                                            emailError =
                                                "Error al actualizar el correo en la base de datos."
                                        }
                                    )
                                } else {
                                    emailError =
                                        "El nuevo correo electrónico aún no ha sido verificado. Por favor, revisa tu bandeja de entrada y haz clic en el enlace."
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Confirmar Correo Verificado",
                            fontFamily = SFProDisplayBold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}