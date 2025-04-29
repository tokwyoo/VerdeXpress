package com.example.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.design.R

val SFProDisplayMedium = FontFamily(Font(R.font.sf_pro_display_medium))

// Data class for FAB configuration
data class FabConfig(
    val visible: Boolean = false,
    val route: String = "",
    val icon: ImageVector = Icons.Default.Add,
    val contentDescription: String = "Add"
)

@Composable
fun BottomNavigationBar(
    navController: NavController, items: List<NavigationItem>, fabConfig: FabConfig? = null
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column {
            // Rounded top part with background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(color = Color(0xFFF5F6F7))
            )

            // Navigation menu with items
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFFFFFFF))
            ) {
                // Row for the navigation items with proper padding
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 25.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    items.forEach { item ->
                        val isSelected = currentRoute == item.route

                        Box(modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (currentRoute != item.route) {
                                    println("BOTTOM NAV - Intentando navegar a: ${item.route} desde: $currentRoute")
                                    navController.navigate(item.route) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        restoreState = true
                                    }
                                }
                            }
                            .background(Color.Transparent)) {
                            // Green indicator line at the very top for selected items
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .width(56.dp)
                                        .height(2.dp)
                                        .background(color = Color(0xFF78B153))
                                        .align(Alignment.TopCenter)
                                )
                            }

                            // Column for icon and text, centered in the box
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .align(Alignment.Center)
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.route,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (isSelected) Color(0xFF78B153) else Color(0xFF3F4946)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.route,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    fontFamily = SFProDisplayMedium,
                                    color = if (isSelected) Color(0xFF78B153) else Color(0xFF484C52)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Optional FAB positioned to overlap exactly with the top edge of the gray box
        // and aligned with the last icon horizontally
        fabConfig?.let {
            if (it.visible) {
                // Calculate the position of the last item
                val itemWidth = with(LocalDensity.current) {
                    (items.size).inv().dp.toPx()
                }

                val fabSize = 56.dp // Standard FAB size

                // Position the FAB at the right edge, aligned with the top of the gray box
                FloatingActionButton(
                    onClick = {
                        if (it.route == "donations") {
                            // Envía el estado a DonationsScreen para abrir el diálogo
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("showDonationDialog", true)
                        } else {
                            // Para otras rutas, navega normalmente
                            navController.navigate(it.route)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 25.dp)
                        .offset(y = -(fabSize / 2)) // Position to overlap exactly at the halfway point of the button
                        .zIndex(1f), // Ensure the FAB is drawn on top
                    containerColor = Color(0xFF78B153),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        focusedElevation = 0.dp,
                        hoveredElevation = 0.dp
                    )
                ) {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.contentDescription,
                        tint = Color.White
                    )
                }
            }
        }
    }
}