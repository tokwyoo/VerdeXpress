package com.example.profile.ui.datosCuenta.EditInfo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.design.SFProDisplayBold
import com.example.design.SFProDisplayMedium
import com.example.design.MainAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPasswordScreen(navController: NavController) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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
                    text = "Editar contraseña",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp,
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
                    text = "Contraseña actual",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it.replace("\n", "") },
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

                Text(
                    text = "Nueva contraseña",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it.replace("\n", "") },
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
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { newPasswordVisible = !newPasswordVisible },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color(0xFF9E9E9E)
                            )
                        ) {
                            Icon(
                                imageVector = if (newPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = if (newPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )

                Text(
                    text = "Confirmar nueva contraseña",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it.replace("\n", "") },
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
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color(0xFF9E9E9E)
                            )
                        ) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(44.dp)) // Agrega un Spacer con una altura fija

                Button(
                    onClick = {
                    // TODO: Implement save logic
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
}