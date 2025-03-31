package com.example.parks.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.parks.data.GetParksApproved
import com.example.parks.data.GetParksNew
import com.example.parks.data.ParkData
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class ParkViewModel : ViewModel() {

    private val getParksApproved = GetParksApproved()
    private val getParksNew = GetParksNew()
    private var originalApprovedParks = listOf<ParkData>()
    private var originalNewParks = listOf<ParkData>()

    val parksList = mutableStateOf<List<ParkData>>(emptyList())
    val newParksList = mutableStateOf<List<ParkData>>(emptyList())

    init {
        fetchApprovedParks()
        fetchNewParks()
    }

    // En ParkViewModel.kt
    private val _currentFilters = mutableStateOf(emptyMap<String, String>())
    val currentFilters: State<Map<String, String>> = _currentFilters

    // Función para establecer nuevos filtros
    fun setFilters(filters: Map<String, String>) {
        _currentFilters.value = filters
        applyFilters(filters)
    }

    fun fetchApprovedParks() {
        getParksApproved.getParks(
            onSuccess = { parks ->
                originalApprovedParks = parks
                parksList.value = parks
                applyFilters(_currentFilters.value)
            },
            onFailure = { exception ->
                println("Error getting approved parks: $exception")
            }
        )
    }

    fun fetchNewParks() {
        getParksNew.getParksN(
            onSuccess = { parks ->
                originalNewParks = parks
                newParksList.value = parks
                applyFilters(_currentFilters.value)
            },
            onFailure = { exception ->
                println("Error getting new parks: $exception")
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        getParksApproved.removeListener()
        getParksNew.removeListener()
    }

    fun applyFilters(filters: Map<String, String>) {
        if (filters.isEmpty()) {
            clearFilters()
            return
        }

        val sortOrder = filters["sort"] ?: "A-Z"
        val statusFilter = filters["status"] ?: ""

        // Filtrar parques aprobados
        val filteredApproved = originalApprovedParks
            .filter { park ->
                statusFilter.isEmpty() || park.situacion.equals(statusFilter, ignoreCase = true)
            }
            .sortedBy { it.nombre.trim().lowercase() } // Ordena siempre A-Z primero
            .let { sortedList ->
                if (sortOrder == "Z-A") sortedList.reversed() else sortedList
            }

        // Filtrar nuevos parques (solo ordenamiento, sin filtro por estado)
        val filteredNew = originalNewParks
            .sortedBy { it.nombre.trim().lowercase() } // Ordena siempre A-Z primero
            .let { sortedList ->
                if (sortOrder == "Z-A") sortedList.reversed() else sortedList
            }

        parksList.value = filteredApproved
        newParksList.value = filteredNew
    }

    fun clearFilters() {
        parksList.value = originalApprovedParks
        newParksList.value = originalNewParks
    }
}