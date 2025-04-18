package com.example.profile.ui.eliminarCuenta

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.design.R
import com.example.design.SFProDisplayBold
import com.example.design.SFProDisplayMedium

@Composable
fun DeleteUserConfirmationScreen(navController: NavController) {
    val verdeBoton = Color(0xFF78B153)
    val roundedShape = RoundedCornerShape(12.dp)

    // Composición de Lottie para la animación de done
    val doneComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.done_animation)
    )


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = doneComposition,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Text(
            text = "¡Usuario eliminado!",
            color = Color(0xFF78B153),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontFamily = SFProDisplayBold,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Te hemos enviado un correo electrónico acerca de la eliminación de tu cuenta",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontFamily = SFProDisplayMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 48.dp)
        )

        Button(
            onClick = {
                navController.navigate("SingInScreen") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .width(175.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
            shape = roundedShape
        ) {
            Text(
                text = "Aceptar",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = SFProDisplayBold,
                fontWeight = FontWeight.Bold
            )
        }
    }
}