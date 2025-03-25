package com.example.navigation

import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.auth.ui.ResetPasswordEmailSentScreen
import com.example.auth.ui.ResetPasswordScreen
import com.example.auth.ui.SignInScreen
import com.example.auth.ui.SignUpScreen
import com.example.auth.ui.SignUpSuccessScreen
import com.example.home.HomeScreen
import com.example.parks.ui.ParksScreen
import com.example.donations.ui.inicio.DonationsScreen
import com.example.notifications.NotificationsScreen
import com.example.parks.ui.MapScreen
import com.example.parks.ui.RegisterParkScreen
import com.example.parks.ui.RegisterParkSuccessScreen
import com.example.parks.ui.SharedViewModel
import java.net.URLDecoder
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.data.SignUpValidator
import com.example.donations.data.donacionMonetaria.DonacionMonetariaViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.donations.ui.donacionEspecie.FormScreen as EspecieFormScreen
import com.example.donations.ui.donacionMonetaria.FormScreen as MonetariaFormScreen
import com.example.donations.ui.donacionMonetaria.MetodoPagoTarjetaScreen
import com.example.donations.ui.donacionMonetaria.MetodoPagoPaypalScreen
import com.example.profile.ui.datosCuenta.AccountDataScreen
import com.example.profile.ui.datosPersonales.PersonalDataScreen
import com.example.profile.ui.eliminarCuenta.DeleteAccountScreen
import com.example.profile.ui.datosPersonales.EditInfo.EditNameScreen
import com.example.profile.ui.datosPersonales.EditInfo.EditPhoneScreen
import com.example.profile.ui.datosPersonales.EditInfo.EditEmailScreen
import com.example.profile.ui.inicio.ProfileScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    // Initialize the shared view models
    val sharedViewModel: SharedViewModel = viewModel() // Este es de Parks, debería tener un nombre más descriptivo...
    val donacionMonetariaViewModel: DonacionMonetariaViewModel = viewModel()

    // Get an instance of FirebaseAuth
    val auth = FirebaseAuth.getInstance()

    // List of routes that do not require authentication
    val publicRoutes = listOf("signIn", "signUp", "resetPassword", "resetPasswordEmailSent", "signUpSuccess")

    // State to track if the authentication listener is active
    var isAuthListenerActive by remember { mutableStateOf(true) }

    // State to track if the user is authenticated
    var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

    // Effect to manage the authentication state listener
    DisposableEffect(isAuthListenerActive) {
        if (isAuthListenerActive) {
            // Create and add the authentication state listener
            val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                isAuthenticated = firebaseAuth.currentUser != null
            }
            auth.addAuthStateListener(authStateListener)
            // Remove the listener when the effect is disposed
            onDispose { auth.removeAuthStateListener(authStateListener) }
        } else {
            onDispose { }
        }
    }

    // Effect to handle navigation changes
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val currentRoute = destination.route ?: return@addOnDestinationChangedListener
            // Update the authentication listener state based on the current route
            isAuthListenerActive = currentRoute !in listOf("signUp", "signUpSuccess")

            // Check if the current route requires authentication
            val routeRequiresAuth = !publicRoutes.any { route ->
                if (route.contains("?")) currentRoute.startsWith(route.split("?")[0]) else currentRoute == route
            }

            // Navigate to the sign-in screen if the route requires authentication and the user is not authenticated
            if (routeRequiresAuth && !isAuthenticated) {
                navController.navigate("signIn") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    // Effect to handle changes in authentication state
    LaunchedEffect(isAuthenticated) {
        val currentRoute = navController.currentDestination?.route
        if (!isAuthenticated && currentRoute != null) {
            // Check if the current route requires authentication
            val routeRequiresAuth = !publicRoutes.any { route ->
                if (route.contains("?")) currentRoute.startsWith(route.split("?")[0]) else currentRoute == route
            }

            // Navigate to the sign-in screen if the route requires authentication and the user is not authenticated
            if (routeRequiresAuth) {
                navController.navigate("signIn") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    // Decidir destino inicial basado en el estado de autenticación
    val startDestination = if (isAuthenticated) NavigationItem.Home.route else "signIn"

    // AQUÍ SE DEFINEN LAS RUTAS DE LA APLICACIÓN ⬇️

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {

        // ----------------------------------------------------------------

        // RUTAS PRINCIPALES

        composable(NavigationItem.Home.route) { HomeScreen() }
        composable(NavigationItem.Parks.route) { ParksScreen(navController = navController) }
        composable(NavigationItem.Donations.route) { DonationsScreen(navController) }
        composable(NavigationItem.Notifications.route) { NotificationsScreen() }
        composable(NavigationItem.Profile.route) { ProfileScreen(navController) }

        // ----------------------------------------------------------------

        // RUTAS DEL MÓDULO "AUTH"

        composable("signIn") { SignInScreen(navController) }
        composable("signUp") { SignUpScreen(navController, SignUpValidator) }
        composable("resetPassword") { ResetPasswordScreen(navController) }
        composable("resetPasswordEmailSent") { ResetPasswordEmailSentScreen(navController) }
        composable("signUpSuccess") { SignUpSuccessScreen(navController) }

        // ----------------------------------------------------------------

        // RUTAS DEL MÓDULO "PARKS"

        // Ruta sin parámetros para RegisterPark
        composable("registerPark") {
            RegisterParkScreen(navController = navController,  sharedViewModel = sharedViewModel)
        }

        // Ruta con parámetros para RegisterPark y decodificación automática
        composable(
            route = "registerPark?lat={lat}&lon={lon}&address={address}&name={name}&desc={desc}&status={status}&imageUris={imageUris}&needs={needs}&comments={comments}",
            arguments = listOf(
                navArgument("lat") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("lon") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("address") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("name") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("desc") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("status") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("imageUris") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("needs") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("comments") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("lat")
            val longitude = backStackEntry.arguments?.getString("lon")
            val address = backStackEntry.arguments?.getString("address")
            val name = backStackEntry.arguments?.getString("name")?.let { URLDecoder.decode(it, "UTF-8") }
            val desc = backStackEntry.arguments?.getString("desc")?.let { URLDecoder.decode(it, "UTF-8") }
            val status = backStackEntry.arguments?.getString("status")?.let { URLDecoder.decode(it, "UTF-8") }
            val imageUris = backStackEntry.arguments?.getString("imageUris")?.split(",")?.map { Uri.parse(it) } ?: emptyList()
            val needs = backStackEntry.arguments?.getString("needs")?.split(",") ?: emptyList()
            val comments = backStackEntry.arguments?.getString("comments")?.let { URLDecoder.decode(it, "UTF-8") }

            // Decodificar la dirección automáticamente
            val decodedAddress = address?.let {
                try {
                    URLDecoder.decode(it, "UTF-8")
                } catch (e: Exception) {
                    it
                }
            }

            RegisterParkScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                latitude = latitude,
                longitude = longitude,
                address = decodedAddress,
                parkNameArg = name,
                descriptionArg = desc,
                statusArg = status,
                imageUrisArg = imageUris,
                needsArg = needs,
                commentsArg = comments
            )
        }

        // Ruta con parámetros para MapScreen y decodificación automática
        composable("map?name={name}&desc={desc}&status={status}&needs={needs}&comments={comments}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("desc") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("status") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("imageUris") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("needs") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("comments") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")?.let { URLDecoder.decode(it, "UTF-8") }
            val desc = backStackEntry.arguments?.getString("desc")?.let { URLDecoder.decode(it, "UTF-8") }
            val status = backStackEntry.arguments?.getString("status")?.let { URLDecoder.decode(it, "UTF-8") }
            val needs = backStackEntry.arguments?.getString("needs")?.split(",") ?: emptyList()
            val comments = backStackEntry.arguments?.getString("comments")?.let { URLDecoder.decode(it, "UTF-8") }

            MapScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                name = name,
                desc = desc,
                status = status,
                needs = needs,
                comments = comments
            )
        }

        // Ruta de pantalla de éxito de registro de parque
        composable("registerParkSuccess") { RegisterParkSuccessScreen(navController = navController) }

        // ----------------------------------------------------------------

        // RUTAS DEL MÓDULO "DONATIONS"

        composable("donationsWithDialog") { DonationsScreen(navController = navController, showDialog = true) }
        composable("donacionEspecie") { EspecieFormScreen(navController) }
        composable("donacionMonetaria") { MonetariaFormScreen(navController, donacionMonetariaViewModel) }
        composable("metodoPagoTarjeta") { MetodoPagoTarjetaScreen(navController, donacionMonetariaViewModel) }
        composable("metodoPagoPaypal") { MetodoPagoPaypalScreen(navController, donacionMonetariaViewModel) }

        // ----------------------------------------------------------------

        // RUTAS DEL MÓDULO "HOME"

        // ...

        // ----------------------------------------------------------------

        // RUTAS DEL MÓDULO "NOTIFICATIONS"

        // ...

        // ----------------------------------------------------------------

        // RUTAS DEL MÓDULO "PROFILE"
        composable("datosPersonales") { PersonalDataScreen(navController) }
        composable("datosCuenta") { AccountDataScreen(navController) }
        composable("eliminarCuenta") { DeleteAccountScreen(navController) }
        // rutas de edición de datos personales
        composable("editName") { EditNameScreen(navController) }
        composable("editPhone") { EditPhoneScreen(navController) }
        composable("editEmail") { EditEmailScreen(navController) }
        // ...

        // ----------------------------------------------------------------
    }
}