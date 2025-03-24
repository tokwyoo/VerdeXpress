package com.example.donations.ui.donacionMonetaria

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.design.R
import com.example.design.SecondaryAppBar
import com.example.donations.data.GetParkNameAndLocation
import com.example.donations.data.ParkData
import com.example.donations.data.donacionMonetaria.DonacionMonetariaViewModel
import com.example.donations.data.donacionMonetaria.MonetariaFormState
import com.example.donations.data.donacionMonetaria.MonetariaValidationResult
import com.example.donations.data.donacionMonetaria.isValid
import com.example.donations.data.donacionMonetaria.rememberMonetariaFormValidator
import com.example.donations.ui.donacionEspecie.reu.CustomDropdown
import com.example.donations.ui.donacionMonetaria.reu.CustomOutlinedTextField

@Composable
fun FormScreen(navController: NavController, viewModel: DonacionMonetariaViewModel) {
    // Estados del formulario vinculados al ViewModel
    var nombre by remember { mutableStateOf(viewModel.nombre) }
    var correo by remember { mutableStateOf(viewModel.correo) }
    var numTel by remember { mutableStateOf(viewModel.numTel) }
    var cantidad by remember { mutableStateOf(viewModel.cantidad) }
    var metodoPago by remember { mutableStateOf(viewModel.metodoPago) }
    var parqueSeleccionado by remember { mutableStateOf(viewModel.parqueSeleccionado) }
    var ubicacionSeleccionado by remember { mutableStateOf(viewModel.ubicacionSeleccionado) }
    var quiereRecibo by remember { mutableStateOf(viewModel.quiereRecibo) }
    var rfc by remember { mutableStateOf(viewModel.rfc) }
    var razon by remember { mutableStateOf(viewModel.razon) }
    var domFiscal by remember { mutableStateOf(viewModel.domFiscal) }

    // Actualizar el ViewModel cuando los valores cambien
    LaunchedEffect(nombre) { viewModel.nombre = nombre }
    LaunchedEffect(correo) { viewModel.correo = correo }
    LaunchedEffect(numTel) { viewModel.numTel = numTel }
    LaunchedEffect(cantidad) { viewModel.cantidad = cantidad }
    LaunchedEffect(metodoPago) { viewModel.metodoPago = metodoPago }
    LaunchedEffect(parqueSeleccionado) { viewModel.parqueSeleccionado = parqueSeleccionado }
    LaunchedEffect(ubicacionSeleccionado) { viewModel.ubicacionSeleccionado = ubicacionSeleccionado }
    LaunchedEffect(quiereRecibo) { viewModel.quiereRecibo = quiereRecibo }
    LaunchedEffect(rfc) { viewModel.rfc = rfc }
    LaunchedEffect(razon) { viewModel.razon = razon }
    LaunchedEffect(domFiscal) { viewModel.domFiscal = domFiscal }

    // Validación del formulario
    var validationResult by remember { mutableStateOf<MonetariaValidationResult?>(null) }

    // Listas de opciones
    val metodosPago = listOf("Tarjeta de crédito/débito")
    val parques = remember { mutableStateListOf<String>() }
    val ubicaciones = remember { mutableStateListOf<String>() }

    // Datos de los parques
    var parksList by remember { mutableStateOf<List<ParkData>>(emptyList()) }
    val getParkNameAndLocation = GetParkNameAndLocation()

    // Colores y estilos
    val verdeBoton = Color(0xFF78B153)
    val roundedShape = RoundedCornerShape(12.dp)
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = verdeBoton,
        focusedLabelColor = verdeBoton,
        cursorColor = verdeBoton,
        errorBorderColor = Color.Red,
        errorLabelColor = Color.Red,
        errorCursorColor = Color.Red
    )

    // DisposableEffect para preservar el estado del formulario durante la navegación
    DisposableEffect(key1 = Unit) {
        onDispose {
            // Esto se ejecutará cuando se navegue fuera de FormScreen
            val currentRoute = navController.currentBackStackEntry?.destination?.route

            // Comprueba si estamos navegando a una pantalla de método de pago
            val navigatingToPaymentScreen = currentRoute?.contains("metodoPago") == true

            // Limpia el formulario si NO estamos navegando a pantallas de pago
            if (!navigatingToPaymentScreen) {
                viewModel.clear() // La próxima vez que se inicie el formulario estará vacío :)
            }
            // Cuando navegamos a pantallas de pago, mantiene los datos
        }
    }

    // Cargar datos de los parques al iniciar la pantalla
    LaunchedEffect(Unit) {
        getParkNameAndLocation.getParkNameAndLocation(
            onSuccess = { parks ->
                parksList = parks
                parques.clear()
                ubicaciones.clear()
                parks.forEach { park ->
                    parques.add(park.nombre)
                    ubicaciones.add(park.ubicacion)
                }
            },
            onFailure = { exception ->
                Log.e("MonetariaFormScreen", "Error al obtener parques: ${exception.message}")
            }
        )
    }

    // Validar el formulario
    val validator = rememberMonetariaFormValidator()
    fun validateForm() {
        val formState = MonetariaFormState(
            nombre = nombre,
            correo = correo,
            numTel = numTel,
            cantidad = cantidad,
            metodoPago = metodoPago,
            parqueSeleccionado = parqueSeleccionado,
            ubicacionSeleccionado = ubicacionSeleccionado,
            quiereRecibo = quiereRecibo,
            rfc = rfc,
            razon = razon,
            domFiscal = domFiscal
        )
        validationResult = validator(formState)

        // Si no hay errores, navegar a la pantalla de pago correspondiente
        if (validationResult!!.isValid()) {
            when (metodoPago) {
                "Tarjeta de crédito/débito" -> navController.navigate("metodoPagoTarjeta")
                "PayPal" -> navController.navigate("metodoPagoPaypal")
                else -> {
                    // Manejar caso en el que no se seleccione un método de pago válido
                    Log.e("FormScreen", "Método de pago no válido")
                }
            }
        }
    }
    // Estructura de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // AppBar
        SecondaryAppBar(
            showIcon = true,
            onIconClick = {
                viewModel.clear()
                navController.popBackStack("Donaciones", inclusive = false)
                navController.navigate("Donaciones")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido del formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            // Título del formulario
            Text(
                text = "Donación monetaria",
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Campo: Nombre del donante
            CustomOutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre del Donante",
                isError = validationResult?.nombreError != null,
                errorMessage = validationResult?.nombreError
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Campo: Correo electrónico
            CustomOutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = "Correo electrónico",
                isError = validationResult?.correoError != null,
                errorMessage = validationResult?.correoError
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Campo: Teléfono de contacto
            CustomOutlinedTextField(
                value = numTel,
                onValueChange = {
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        numTel = it
                    }
                },
                label = "Teléfono de contacto",
                isError = validationResult?.numTelError != null,
                errorMessage = validationResult?.numTelError,
                isNumberField = true
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Dropdown: Parque a donar
            CustomDropdown(
                fieldName = "Parque a donar",
                options = parques,
                selectedOption = parqueSeleccionado,
                onOptionSelected = { option ->
                    parqueSeleccionado = option
                    ubicacionSeleccionado = parksList.find { it.nombre == option }?.ubicacion ?: "Desconocido"
                },
                isError = validationResult?.parqueSeleccionadoError != null,
                errorMessage = validationResult?.parqueSeleccionadoError
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Campo: Ubicación (solo lectura)
            CustomOutlinedTextField(
                value = ubicacionSeleccionado,
                onValueChange = { },
                label = "Ubicación",
                readOnly = true,
                isError = false,
                errorMessage = null
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Dropdown: Método de pago
            CustomDropdown(
                fieldName = "Método de pago",
                options = metodosPago,
                selectedOption = metodoPago,
                onOptionSelected = { metodoPago = it },
                isError = validationResult?.metodoPagoError != null,
                errorMessage = validationResult?.metodoPagoError
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Campo: Cantidad monetaria a donar
            CustomOutlinedTextField(
                value = cantidad,
                onValueChange = {
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        cantidad = it
                    }
                },
                label = "Cantidad monetaria a donar",
                isError = validationResult?.cantidadError != null,
                errorMessage = validationResult?.cantidadError,
                isNumberField = true,
                showNumberControls = true
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Sección: ¿Desea recibo de donación?
            Text(
                text = "¿Desea recibo de donación?",
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
            )
            if (validationResult?.quiereReciboError != null) {
                Text(
                    text = validationResult!!.quiereReciboError!!,
                    fontSize = 12.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp).align(Alignment.CenterHorizontally)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadioButton(
                        selected = quiereRecibo == true,
                        onClick = { quiereRecibo = true },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = verdeBoton,
                            unselectedColor = Color.Gray
                        )
                    )
                    Text(text = "Sí", color = Color.Black)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadioButton(
                        selected = quiereRecibo == false,
                        onClick = { quiereRecibo = false },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = verdeBoton,
                            unselectedColor = Color.Gray
                        )
                    )
                    Text(text = "No", color = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Campos adicionales para el recibo (visible si quiereRecibo == true)
            AnimatedVisibility(visible = quiereRecibo == true) {
                Column {
                    CustomOutlinedTextField(
                        value = rfc,
                        onValueChange = {
                            if (it.length <= 13) {
                                rfc = it
                            }
                        },
                        label = "RFC",
                        isError = validationResult?.rfcError != null,
                        errorMessage = validationResult?.rfcError
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    CustomOutlinedTextField(
                        value = razon,
                        onValueChange = { razon = it },
                        label = "Razón social (para empresas)",
                        isError = validationResult?.razonError != null,
                        errorMessage = validationResult?.razonError
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    CustomOutlinedTextField(
                        value = domFiscal,
                        onValueChange = { domFiscal = it },
                        label = "Domicilio Fiscal",
                        isError = validationResult?.domFiscalError != null,
                        errorMessage = validationResult?.domFiscalError
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))

            // Botón: Validar
            Button(
                onClick = { validateForm() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 118.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                shape = roundedShape
            ) {
                Text(
                    text = "Validar",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                )
            }
        }
    }
}