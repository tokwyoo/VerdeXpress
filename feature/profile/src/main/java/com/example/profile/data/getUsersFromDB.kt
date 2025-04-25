package com.example.profile.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


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


data class UserData(
    val id: String = "",
    val nombre: String? = null,
    val apellidos: String? = null,
    val correoElectronico: String? = null,
    val numeroContacto: String? = null
)