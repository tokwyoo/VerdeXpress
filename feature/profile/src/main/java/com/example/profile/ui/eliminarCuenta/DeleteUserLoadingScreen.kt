package com.example.profile.ui.eliminarCuenta

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import kotlinx.coroutines.delay

@Composable
fun DeleteUserLoadingScreen(navController: NavController) {
    // Estados para controlar la secuencia de animaciones
    var animationStage by remember { mutableStateOf(AnimationStage.LOADING) }

    // Composiciones de Lottie para las animaciones
    val loadingComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))

    // Efecto de lanzamiento para controlar la secuencia de animaciones
    LaunchedEffect(Unit) {
        // Secuencia de animaciones
        delay(2000)
        animationStage = AnimationStage.DONE
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animación con crossfade suave
        Crossfade(
            targetState = animationStage,
            animationSpec = tween(durationMillis = 500)
        ) { stage ->
            when (stage) {
                AnimationStage.LOADING -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Eliminando",
                            color = Color(0xFF78B153),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = SFProDisplayBold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LottieAnimation(
                            composition = loadingComposition,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }

                AnimationStage.DONE -> {
                    // Navigate to the next screen when animation is done
                    LaunchedEffect(Unit) {
                        navController.navigate("DeleteUser") {
                            popUpTo("eliminarCuenta") { inclusive = true }
                        }
                    }
                    Box {} // Empty composable to satisfy the when block
                }
            }
        }
    }
}

// Enum para manejar los estados de animación
enum class AnimationStage {
    LOADING, DONE
}