package com.example.donations.ui.donacionEspecie

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.design.R
import com.example.design.SecondaryAppBar
import com.example.donations.data.GetParkNameAndLocation
import com.example.donations.data.ParkData
import com.example.donations.data.donacionEspecie.DonationFormState
import com.example.donations.data.donacionEspecie.isValid
import com.example.donations.data.donacionEspecie.rememberDonationFormValidator
import com.example.donations.data.donacionEspecie.saveDonationToFirestore
import com.example.donations.ui.donacionEspecie.reu.CustomDropdown
import com.example.donations.ui.donacionEspecie.reu.CustomOutlinedTextField
import com.example.donations.ui.donacionEspecie.reu.DatePickerWithDialog
import com.example.parks.data.ImageUploadManager
import com.example.parks.ui.ParkImageDisplay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FormScreen(navController: NavController) {

    val verdeBoton = Color(0xFF78B153)
    val roundedShape = RoundedCornerShape(12.dp)
    var showSuccessDialog by remember { mutableStateOf(false) } //dialogo de exito
    var showFailedDialog by remember { mutableStateOf(false) } //dialogo de error
    var showDatePicker by remember { mutableStateOf(false) }

    var name by rememberSaveable { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var numeroValue by remember { mutableStateOf("") }
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedOption by remember { mutableStateOf("") }
    var estimatedDonationDate by remember { mutableStateOf("") }
    var imageError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var contactNumberError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var statusErrorP by remember { mutableStateOf<String?>(null) }
    var statusErrorT by remember { mutableStateOf<String?>(null) }
    var statusErrorR by remember { mutableStateOf<String?>(null) }
    var statusErrorC by remember { mutableStateOf<String?>(null) }
    var statusErrorN by remember { mutableStateOf<String?>(null) }
    var estimatedDonationDateError by remember { mutableStateOf<String?>(null) }
    var selectedResourceType by remember { mutableStateOf("") }
    var selectedResource by remember { mutableStateOf("") }
    var selectedCondition by remember { mutableStateOf("") }
    var optionsPark by remember { mutableStateOf<List<String>>(emptyList()) }
    var parksList by remember { mutableStateOf<List<ParkData>>(emptyList()) }
    val optionsType = remember { listOf("Mobiliario", "Jardinería") }
    val optionsResourceM = remember {
        listOf(
            "Bancas", "Mesas", "Juegos Infantiles", "Cestos de basura", "Cercas o Delimitaciones"
        )
    }
    val optionsResourceJ = remember {
        listOf(
            "Árboles",
            "Arbustos",
            "Plantas Ornamentales",
            "Césped o Pasto en rollo",
            "Tierra para rehabilitación de suelos"
        )
    }
    val optionsCondition = remember { listOf("Nuevo", "Semi-nuevo", "Usado") }
    val context = LocalContext.current
    val imageUploadManager = remember { ImageUploadManager(context) }
    var isUploading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showPermissionDialog by remember { mutableStateOf(false) }
    val resourceOptions = remember(selectedResourceType) {
        when (selectedResourceType) {
            "Mobiliario" -> optionsResourceM
            "Jardinería" -> optionsResourceJ
            else -> emptyList() // Si no se ha seleccionado nada
        }
    }
    val validator = rememberDonationFormValidator()

    val getParkNameAndLocation = GetParkNameAndLocation()

    LaunchedEffect(Unit) {
        getParkNameAndLocation.getParkNameAndLocation(onSuccess = { parks ->
            parksList = parks
            optionsPark = parks.map { it.nombre }
        }, onFailure = { exception ->
            println("Error al obtener parques")
        })
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            if (selectedImageUris.size + uris.size > 3) {
                imageError = "Solo puedes subir un máximo de 3 imágenes."
            } else {
                selectedImageUris = selectedImageUris + uris
                imageError = null
            }
        } else {
            imageError = "Debes seleccionar al menos una imagen."
        }
    }

    fun openImagePicker() {
        if (selectedImageUris.size >= 3) {
            imageError = "Solo puedes subir un máximo de 3 imágenes."
        } else {
            imagePickerLauncher.launch("image/*")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            showPermissionDialog = true
        }
    }

    fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                context, permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openImagePicker()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    val scrollState = rememberScrollState()

    fun validate() {
        val formState = DonationFormState(
            donorName = name,
            contactNumber = contactNumber,
            location = location,
            parkToDonate = selectedOption,
            resourceType = selectedResourceType,
            resource = selectedResource,
            quantity = numeroValue,
            condition = selectedCondition,
            selectedImageUris = selectedImageUris,
            estimatedDonationDate = estimatedDonationDate
        )
        val validationResult = validator(formState)

        nameError = validationResult.donorNameError
        contactNumberError = validationResult.contactNumberError
        locationError = validationResult.locationError
        statusErrorP = validationResult.parkToDonateError
        statusErrorT = validationResult.resourceTypeError
        statusErrorR = validationResult.resourceError
        statusErrorC = validationResult.conditionError
        statusErrorN = validationResult.quantityError
        imageError = validationResult.imageError
        estimatedDonationDateError = validationResult.estimatedDonationDateError

        if (validationResult.isValid()) {
            isUploading = true // Activar el estado de subida
            coroutineScope.launch {
                try {
                    val imageUrls = imageUploadManager.uploadImages(selectedImageUris)

                    // TODO: IMPLEMENTAR FLUJO DE GENERACIÓN DE COMPROBANTE
                    // ---- INICIO FLUJO DE GENERACIÓN DE COMPROBANTE ----
                    var urlComprobante = ""
                    // ---- FIN FLUJO DE GENERACIÓN DE COMPROBANTE ----

                    saveDonationToFirestore(
                        name = name,
                        contactNumber = contactNumber,
                        location = location,
                        parkToDonate = selectedOption,
                        resourceType = selectedResourceType,
                        resource = selectedResource,
                        quantity = numeroValue,
                        condition = selectedCondition,
                        imageUrls = imageUrls,
                        estimatedDonationDate = estimatedDonationDate,
                        urlComprobante = urlComprobante
                    )

                    withContext(Dispatchers.Main) {
                        isUploading = false // Desactivar el estado de subida
                        showSuccessDialog = true // Mostrar diálogo de éxito
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isUploading = false // Desactivar el estado de subida en caso de error
                        showFailedDialog = true // Mostrar diálogo de error
                        Log.e("FormScreen", "Error: ${e.message}", e)
                    }
                }
            }
        }
    }

    Box {
        Column {
            SecondaryAppBar(
                showIcon = true,
                onIconClick = {
                    if (navController.previousBackStackEntry?.destination?.route == "Donaciones") {
                        // Cuando venimos de DonationsScreen regresa a inicio de donaciones
                        navController.navigate("Donaciones") {
                            popUpTo("Donaciones") { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        // Cuando venimos directamente desde HomeScreen al hacer click en el icono regresa a HomeScreen
                        navController.popBackStack()
                    }
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Donacion en Especie",
                    fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                    fontSize = 25.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                CustomOutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = when {
                            it.isEmpty() -> "El nombre no puede estar vacío"
                            it.length > 100 -> "El nombre no puede exceder 100 caracteres"
                            else -> null
                        }
                    },
                    label = "Nombre del Donante/Razón Social",
                    isError = nameError != null,
                    errorMessage = nameError
                )

                Spacer(modifier = Modifier.height(10.dp))

                CustomOutlinedTextField(
                    value = contactNumber,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 10) {
                            contactNumber = newValue
                        }
                        contactNumberError = when {
                            newValue.isEmpty() -> "El número de contacto no puede estar vacío"
                            newValue.length < 10 -> "El número de contacto debe tener al menos 10 dígitos"
                            else -> null
                        }
                    },
                    label = "Número de contacto",
                    isError = contactNumberError != null,
                    errorMessage = contactNumberError,
                    modifier = Modifier.fillMaxWidth(),
                    isNumberField = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                CustomDropdown(
                    fieldName = "Parque a Donar",
                    options = optionsPark,
                    selectedOption = selectedOption,
                    onOptionSelected = { option ->
                        selectedOption = option
                        statusErrorP = if (option.isEmpty()) "Debes seleccionar un parque" else null

                        val selectedPark = parksList.find { it.nombre == option }
                        location = selectedPark?.ubicacion ?: "Desconocido"
                    },
                    isError = statusErrorP != null,
                    errorMessage = statusErrorP,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                CustomOutlinedTextField(
                    value = location,
                    onValueChange = { },
                    label = "Ubicación",
                    isError = locationError != null,
                    errorMessage = locationError,
                    readOnly = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                CustomDropdown(
                    fieldName = "Tipo de Recurso",
                    options = optionsType,
                    selectedOption = selectedResourceType,
                    onOptionSelected = { option ->
                        selectedResourceType = option
                        selectedResource = ""
                        selectedCondition = ""
                        statusErrorT =
                            if (option.isEmpty()) "Debes seleccioanr un tipo de recurso" else null
                    },
                    isError = statusErrorT != null,
                    errorMessage = statusErrorT,
                    modifier = Modifier.fillMaxWidth()
                )

                if (resourceOptions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomDropdown(
                        fieldName = "Recurso",
                        options = resourceOptions,
                        selectedOption = selectedResource,
                        onOptionSelected = { option ->
                            selectedResource = option
                            statusErrorR =
                                if (option.isEmpty()) "Debes seleccionar un recurso" else null
                        },
                        isError = statusErrorR != null,
                        errorMessage = statusErrorR,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    CustomOutlinedTextField(
                        value = numeroValue,
                        onValueChange = { newValue ->
                            numeroValue = newValue
                            val cantidad = newValue.toIntOrNull() ?: 1
                            statusErrorN = when {
                                cantidad < 1 -> "La cantidad mínima es 1"
                                cantidad > 10 -> "La cantidad máxima es 10"
                                else -> null
                            }
                        },
                        label = "Cantidad",
                        isError = statusErrorN != null,
                        errorMessage = statusErrorN,
                        readOnly = true,
                        isNumberField = true,
                        showNumberControls = true
                    )
                }

                if (selectedResourceType == "Mobiliario") {
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomDropdown(
                        fieldName = "Condiciones de Mobiliario",
                        options = optionsCondition,
                        selectedOption = selectedCondition,
                        onOptionSelected = { option ->
                            selectedCondition = option
                            statusErrorC =
                                if (option.isEmpty()) "Debes seleccionar una condición" else null
                        },
                        isError = statusErrorC != null,
                        errorMessage = statusErrorC,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                CustomOutlinedTextField(
                    value = selectedImageUris.joinToString(", ") { it.lastPathSegment ?: "" },
                    onValueChange = { },
                    label = "Imágenes",
                    isError = imageError != null,
                    trailingIcon = {
                        IconButton(onClick = { checkAndRequestPermission() }) {
                            Image(
                                painter = painterResource(id = R.drawable.image_add),
                                contentDescription = "Select location",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    errorMessage = imageError,
                    readOnly = true
                )
                fun removeImage(index: Int) {
                    selectedImageUris = selectedImageUris.toMutableList().apply {
                        removeAt(index)
                    }
                }

                ParkImageDisplay(imageUris = selectedImageUris,
                    onImageRemove = { index -> removeImage(index) })

                Spacer(modifier = Modifier.height(10.dp))

                CustomOutlinedTextField(
                    value = estimatedDonationDate,
                    onValueChange = { newDate -> estimatedDonationDate = newDate },
                    label = "Fecha estimada de donación",
                    isError = estimatedDonationDateError != null,
                    errorMessage = estimatedDonationDateError,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }){
                            Image(
                                painter = painterResource(id = R.drawable.ic_calendar),
                                contentDescription = "Select location",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { validate() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 118.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = verdeBoton),
                    shape = roundedShape,
                    enabled = !isUploading
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Validar",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                        )
                    }
                }
            }
        }

        if (showDatePicker) {
            DatePickerWithDialog(
                selectedDate = estimatedDonationDate,
                onDateSelected = {
                    estimatedDonationDate = it
                    estimatedDonationDateError = null
                },
                onDismiss = { showDatePicker = false }
            )
        }

        // Fondo oscuro semitransparente cuando se muestra un diálogo
        if (showSuccessDialog || showFailedDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))) // Fondo oscuro semitransparente
        }

        SuccessDialog(visible = showSuccessDialog, onDismiss = {
            showSuccessDialog = false
            navController.popBackStack("Donaciones", inclusive = false)
            navController.navigate("Donaciones")
        })

        FailedDialog(visible = showFailedDialog, onDismiss = {
            showFailedDialog = false
            navController.popBackStack("Donaciones", inclusive = false)
            navController.navigate("Donaciones")
        })
    }
}