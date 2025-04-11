package com.example.parks.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parks.data.GetParks
import com.example.parks.data.ParkData
import kotlinx.coroutines.launch

class ParkViewModel : ViewModel() {
    private val getParksRepository = GetParks()

    // Estados observables
    val parksList = mutableStateOf<List<ParkData>>(emptyList())
    val newParksList = mutableStateOf<List<ParkData>>(emptyList())
    val parkDetails = mutableStateOf<ParkData?>(null)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    private var originalApprovedParks = listOf<ParkData>()
    private var originalNewParks = listOf<ParkData>()

    // Función para obtener la lista de parques
    fun fetchParks() {
        isLoading.value = true
        errorMessage.value = null

        getParksRepository.getParks(
            onSuccess = { parks ->
                parksList.value = parks
                isLoading.value = false
            },
            onFailure = { exception ->
                errorMessage.value = "Error al cargar los parques: ${exception.message}"
                isLoading.value = false
            }
        )
    }

    // Función para obtener detalles de un parque específico
    fun fetchParkDetails(parkName: String) {
        viewModelScope.launch {
            parkDetails.value = null // Resetear antes de cargar nuevos datos
            isLoading.value = true
            errorMessage.value = null

            try {
                val park = parksList.value.find { it.nombre == parkName }
                parkDetails.value = park
                if (park == null) {
                    errorMessage.value = "Parque no encontrado"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error al cargar detalles: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // Limpiar los listeners cuando el ViewModel se destruya
    override fun onCleared() {
        super.onCleared()
        getParksRepository.removeListener()
    }
}