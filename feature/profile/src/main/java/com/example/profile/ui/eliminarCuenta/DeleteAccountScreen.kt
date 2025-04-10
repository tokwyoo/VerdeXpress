package com.example.profile.ui.eliminarCuenta

import androidx.compose.foundation.border
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(navController: NavController) {
    var checked by remember { mutableStateOf(false) }

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
                    text = "Eliminar cuenta",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // mensaje para eliminar cuenta
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFF78B153),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "¿Estás seguro de que desea eliminar su cuenta?",
                            fontFamily = SFProDisplayBold,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Recuerda que una vez que cierres tu cuenta, esta acción no se puede deshacer y la cuenta no se podrá recuperar. Si en el futuro deseas volver a realizar donaciones o nuevos registros que requieran una cuenta, deberás crear una cuenta nueva.",
                            fontFamily = SFProDisplayMedium,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF78B153),
                            uncheckedColor = Color.Gray
                        )
                    )

                    Text(
                        text = "Sí, quiero eliminar permanentemente mi cuenta",
                        fontFamily = SFProDisplayBold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // boton para confirmar
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            // Navigate to the DeleteUserLoading screen
                            navController.navigate("DeleteUserLoading")
                        },
                        modifier = Modifier
                            .width(205.dp)
                            .height(70.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF78B153)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = checked
                    ) {
                        Text(
                            "Eliminar cuenta",
                            fontFamily = SFProDisplayBold,
                            fontSize = 15.sp
                        )
                    }

                    Text(
                        text = "Esta acción no se puede deshacer",
                        modifier = Modifier
                            .padding(top = 4.dp),
                        fontFamily = SFProDisplayBold,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}