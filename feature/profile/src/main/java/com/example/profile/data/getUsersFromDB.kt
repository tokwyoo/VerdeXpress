package com.example.profile.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

fun obtenerDatosUsuario(onSuccess: (List<UserData>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("usuarios").get()
        .addOnSuccessListener { result ->
            val usuariosList = result.map { document ->
                UserData(
                    id = document.id,
                    nombre = document.getString("nombre"),
                    apellidos = document.getString("apellidos"),
                    correoElectronico = document.getString("correoElectronico"),
                    numeroContacto = document.getString("numeroContacto")
                )
            }
            onSuccess(usuariosList)
        }
        .addOnFailureListener { exception ->
            Log.e("Usuarios", "Error al obtener datos de usuarios de Firebase", exception)
            onSuccess(emptyList())
        }
}

fun obtenerIDUsuario(userId: String, onSuccess: (UserData?) -> Unit, onFailure: (Exception?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("usuarios").document(userId).get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val userData = document.toObject(UserData::class.java)
                onSuccess(userData)
            } else {
                onSuccess(null) // Usuario no encontrado
            }
        }
        .addOnFailureListener { exception ->
            Log.e("Usuarios", "Error al obtener datos del usuario con ID: $userId", exception)
            onFailure(exception)
        }
}

fun actualizarNumeroContacto(
    userId: String,
    newPhone: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val usuarioRef = db.collection("usuarios").document(userId)

    usuarioRef.update("numeroContacto", newPhone)
        .addOnSuccessListener {
            Log.d("Firestore", "Número de contacto del usuario actualizado con éxito.")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error al actualizar el número de contacto del usuario", e)
            onFailure(e)
        }
}

data class UserData(
    val id: String = "",
    val nombre: String? = null,
    val apellidos: String? = null,
    val correoElectronico: String? = null,
    val numeroContacto: String? = null
)