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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.SFProDisplayBold
import com.example.design.SFProDisplayMedium
import com.example.design.MainAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPhoneScreen(navController: NavController) {
    var newPhone by remember { mutableStateOf("") }
    val currentPhone = "+52 662 XXX XXXX"

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
                    text = "Editar número de contacto",
                    fontFamily = SFProDisplayBold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                )
            }

            Text(
                text = "Número de contacto actual",
                fontFamily = SFProDisplayBold,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = currentPhone,
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
                onValueChange = { newPhone = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Número de contacto",
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

            Spacer(modifier = Modifier.weight(1f))

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

@Composable
fun BottomNavigationItem(
    icon: ImageVector,
    label: String,
    selected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color(0xFF78B153) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontFamily = SFProDisplayBold,
            fontSize = 12.sp,
            color = if (selected) Color(0xFF78B153) else Color.Gray
        )
    }
}