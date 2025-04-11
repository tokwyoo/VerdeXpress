package com.example.parks.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class GetParks {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    fun getParks(onSuccess: (List<ParkData>) -> Unit, onFailure: (Exception) -> Unit) {
        listenerRegistration = firestore.collection("parques")
            .whereEqualTo("registro_estado", "aprobado") // Filtra solo los documentos con registro_estado == "aprobado"
            .addSnapshotListener { result, exception ->
                if (exception != null) {
                    onFailure(exception)
                    return@addSnapshotListener
                }

                val parks = mutableListOf<ParkData>()
                if (result != null) {
                    for (document in result) {
                        val nombre = document.getString("nombre") ?: "Desconocido"
                        val imagenes = document.get("imagenes") as? List<String> ?: emptyList()
                        val primeraImagen = imagenes.firstOrNull() ?: ""
                        val latitud = document.getString("latitud") ?: "Desconocida"
                        val longitud = document.getString("longitud") ?: "Desconocida"
                        val necesidades = document.get("necesidades") as? List<String> ?: emptyList()
                        val estado = document.getString("estado_actual") ?: "Desconocido"
                        val comentarios = document.get("comentarios") as? List<String> ?: emptyList()
                        val situacion = document.getString("situacion_actual") ?: "Desconocido"

                        parks.add(ParkData(nombre, primeraImagen, latitud, longitud, necesidades, estado, comentarios, situacion))
                    }
                }
                onSuccess(parks)
            }
    }

    fun removeListener() {
        listenerRegistration?.remove()
    }
}

data class ParkData(val nombre: String, val primeraImagen: String, val latitud: String, val longitud: String, val necesidades: List<String>, val estado: String, val comentarios: List<String>, val situacion: String)
