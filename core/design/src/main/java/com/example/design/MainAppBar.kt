package com.example.design

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.design.R

val SFProDisplayBold = FontFamily(Font(R.font.sf_pro_display_bold))
val SFProDisplayMedium = FontFamily(Font(R.font.sf_pro_display_medium))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar() {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "VerdeXpress",
                    fontSize = 30.sp,
                    fontFamily = SFProDisplayBold
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF78B153),
            titleContentColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMyTopAppBar() {
    MainAppBar()
}