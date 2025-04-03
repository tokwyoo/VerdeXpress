package com.example.donations.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class GetParkNameAndLocation {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    fun getParkNameAndLocation(onSuccess: (List<ParkData>) -> Unit, onFailure: (Exception) -> Unit) {
        listenerRegistration = firestore.collection("parques")
            .whereEqualTo("registro_estado", "aprobado") // Filtra solo los documentos con registro_estado == "aprobado"
            .whereNotEqualTo("situacion_actual", "Financiación completada") // Filtra donde situacion_actual no sea "Financiación completada"
            .addSnapshotListener { result, exception ->
                if (exception != null) {
                    onFailure(exception)
                    return@addSnapshotListener
                }

                val parks = mutableListOf<ParkData>()
                if (result != null) {
                    for (document in result) {
                        val nombre = document.getString("nombre") ?: "Desconocido"
                        val ubicacion = document.getString("ubicacion") ?: "Desconocido"
                        parks.add(ParkData(nombre, ubicacion))
                    }
                }
                onSuccess(parks)
            }
    }

    fun removeListener() {
        listenerRegistration?.remove()
    }
}

data class ParkData(val nombre: String, val ubicacion: String)