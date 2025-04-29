package com.example.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

data class FabConfig(
    val visible: Boolean = false,
    val route: String = "",
    val icon: ImageVector = Icons.Default.Add,
    val contentDescription: String = "Add"
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<NavigationItem>,
    fabConfig: FabConfig? = null
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isInParkDetails = currentRoute?.startsWith("parkDetails/") ?: false
    val selectedNavItem = when {
        isInParkDetails -> NavigationItem.Parks.route
        else -> currentRoute
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(color = Color(0xFFF5F6F7))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFFFFFFF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 25.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    items.forEach { item ->
                        val isSelected = selectedNavItem == item.route

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    if (item.route == NavigationItem.Parks.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(item.route) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    } else {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = false
                                            }
                                            launchSingleTop = true
                                            restoreState = false
                                        }
                                    }
                                }
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .width(56.dp)
                                        .height(2.dp)
                                        .background(color = Color(0xFF78B153))
                                        .align(Alignment.TopCenter)
                                )
                            }

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

        fabConfig?.let {
            if (it.visible) {
                FloatingActionButton(
                    onClick = {
                        if (it.route == "donations") {
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("showDonationDialog", true)
                        } else {
                            navController.navigate(it.route)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 25.dp)
                        .offset(y = (-28).dp)
                        .zIndex(1f),
                    containerColor = Color(0xFF78B153),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
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