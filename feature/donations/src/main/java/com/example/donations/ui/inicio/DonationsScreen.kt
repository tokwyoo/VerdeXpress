package com.example.donations.ui.inicio

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.MainAppBar
import com.example.donations.R

@Composable
fun RoundedBoxModifier(
    parkName: String, donationDetails: String, date: String, showDetails: String
) {
    Column(
        modifier = Modifier
            .shadow(
                elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000)
            )
            .border(
                width = 1.dp, color = Color(0xFF78B153), shape = RoundedCornerShape(size = 5.dp)
            )
            .padding(1.dp)
            .width(354.dp)
            .height(74.dp)
            .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 5.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp) // Ajustamos el padding aquí
    ) {
        Text(
            text = "Parque \"$parkName\"", style = TextStyle(
                fontSize = 14.sp, fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = donationDetails, style = TextStyle(fontSize = 12.sp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = date, style = TextStyle(fontSize = 12.sp)
            )
            Text(
                text = showDetails, style = TextStyle(
                    fontSize = 12.sp, color = Color(0xFF000000)
                )
            )
        }
    }
}

@Composable
fun DonationsScreen(navController: NavController, showDialog: Boolean = false) {
    // Estado para controlar la visibilidad del diálogo
    var isDialogVisible by rememberSaveable { mutableStateOf(showDialog) }

    // Observa si se debe mostrar el diálogo desde el savedStateHandle
    LaunchedEffect(navController) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("showDonationDialog")
            ?.observeForever { shouldShow ->
                isDialogVisible = shouldShow == true
            }
    }

    // Mostrar el Dialog cuando `isDialogVisible` sea `true`
    if (isDialogVisible) {
        DonationTypeDialog(
            onDismiss = {
                isDialogVisible = false
                // Guardar en savedStateHandle para persistencia
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "showDonationDialog", false
                )
            }, navController = navController
        )
    }

    // Contenido de la pantalla
    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            MainAppBar()
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Historial de donaciones", style = TextStyle(
                            fontSize = 25.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily(Font(com.example.design.R.font.sf_pro_display_bold)),
                            fontWeight = FontWeight(700),
                            color = Color(0xFF000000),
                            letterSpacing = 0.25.sp,
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.filter_list),
                        contentDescription = "Notificaciones donaciones",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Últimas donaciones hechas", style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily(Font(com.example.design.R.font.sf_pro_display_medium)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFF000000),
                        letterSpacing = 0.25.sp,
                    )
                )
                Spacer(modifier = Modifier.height(14.dp))
                // Usando el recuadro reutilizable
                RoundedBoxModifier(
                    parkName = "Nombre del parque",
                    donationDetails = "Monto $ XX,XXX",
                    date = "09 Marzo 2025",
                    showDetails = "Ver detalles"
                )

                Spacer(modifier = Modifier.height(8.dp))

                RoundedBoxModifier(
                    parkName = "Nombre del parque",
                    donationDetails = "Recurso donado \"nombre\"",
                    date = "09 Marzo 2025",
                    showDetails = "Ver detalles"
                )

                Spacer(modifier = Modifier.height(8.dp))

                RoundedBoxModifier(
                    parkName = "Nombre del parque",
                    donationDetails = "Recurso donado \"nombre\"",
                    date = "01 Marzo 2025",
                    showDetails = "Ver detalles"
                )

                Spacer(modifier = Modifier.height(8.dp))

                RoundedBoxModifier(
                    parkName = "Nombre del parque",
                    donationDetails = "Monto $ XX,XXX",
                    date = "26 Febrero 2025",
                    showDetails = "Ver detalles"
                )
            }
        }
    }
}
