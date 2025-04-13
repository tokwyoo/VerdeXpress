package com.example.parks.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

fun formatShortFirestoreDate(timestamp: Timestamp): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    return dateFormat.format(timestamp.toDate())
}

@Composable
fun rememberUserFullName(userId: String): String {
    var userFullName by remember { mutableStateOf("Usuario desconocido") }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val nombre = document.getString("nombre") ?: ""
                    val apellido = document.getString("apellidos") ?: ""
                    userFullName = if (nombre.isNotEmpty() && apellido.isNotEmpty()) {
                        "$nombre $apellido"
                    } else if (nombre.isNotEmpty()) {
                        nombre
                    } else {
                        "Usuario anónimo"
                    }
                }
                .addOnFailureListener {
                    userFullName = "Error al cargar usuario"
                }
        }
    }
    return userFullName
}

fun getParkDetails(parkName: String, onSuccess: (ParkDataA) -> Unit, onFailure: (Exception) -> Unit) {
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    firestore.collection("parques")
        .whereEqualTo("nombre", parkName)
        .get()
        .addOnSuccessListener { result ->
            if (!result.isEmpty) {
                val document = result.documents[0]
                val nombre = document.getString("nombre") ?: "Desconocido"
                val imagenes = document.get("imagenes") as? List<String> ?: emptyList()
                val imagenesAvance = document.get("imagenes_avances") as? List<String> ?: emptyList()
                val primeraImagen = imagenes.firstOrNull() ?: ""
                val necesidades = document.get("necesidades") as? List<String> ?: emptyList()
                val estado = document.getString("estado_actual") ?: "Desconocido"
                val comentarios = document.getString("comentarios") ?: "Sin comentarios"
                val latitud = document.getString("latitud") ?: "0.0" // Obtener latitud
                val longitud = document.getString("longitud") ?: "0.0" // Obtener longitud
                val situacion = document.getString("situacion_actual") ?: "Desconocido"
                val usuarioId = document.getString("registro_usuario") ?: "Desconocido"
                val fecha = document.getTimestamp("created_at") ?: Timestamp.now()
                var comNeed = document.getString("necesidades_com") ?: ""
                val ubi = document.getString("ubicacion") ?: ""
                var comAd = document.getString("comentarios_ad") ?: ""
                var razonCierre = document.getString("motivo_cierre") ?: ""

                if(latitud.toDoubleOrNull()== null || longitud.toDoubleOrNull() == null){
                    onFailure(Exception("Coordenadas no válidas"))
                } else {
                    val parkData = ParkDataA(nombre, imagenes, imagenesAvance, primeraImagen, necesidades, estado, comentarios, latitud, longitud, situacion, usuarioId, fecha, comNeed, ubi, comAd, razonCierre)
                    onSuccess(parkData)
                }
            } else {
                onFailure(Exception("Parque no encontrado"))
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}

data class ParkDataA(
    val nombre: String,
    val imagenes: List<String>,
    val imagenesAvance: List<String>,
    val primeraImagen: String,
    val necesidades: List<String>,
    val estado: String,
    val comentarios: String,
    val latitud: String,
    val longitud: String,
    val situacion: String,
    val usuarioId: String,
    val fecha: Timestamp,
    var comNeed: String,
    val ubi: String,
    var comAd: String,
    var razonCierre: String
)

val LocalBottomBarState = compositionLocalOf { mutableStateOf(true) }