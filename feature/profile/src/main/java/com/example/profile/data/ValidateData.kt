package com.example.profile.data

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

fun actualizarNombreApellidoUsuario(userId: String, nuevoNombre: String? = null, nuevoApellido: String? = null, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val usuarioRef = db.collection("usuarios").document(userId)

    val updates = mutableMapOf<String, Any>()
    if (!nuevoNombre.isNullOrBlank()) {
        updates["nombre"] = nuevoNombre
    }
    if (!nuevoApellido.isNullOrBlank()) {
        updates["apellidos"] = nuevoApellido
    }

    if (updates.isNotEmpty()) {
        usuarioRef.update(updates)
            .addOnSuccessListener {
                Log.d("Firestore", "Nombre y/o apellido del usuario actualizado con éxito.")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al actualizar el nombre y/o apellido del usuario", e)
                onFailure(e)
            }
    } else {
        // No hay cambios para actualizar
        onSuccess() // Consideramos esto como un éxito, no hubo error
    }
}

fun actualizarNumeroContacto(userId: String, nuevoNumero: String? = null, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val usuarioRef = db.collection("usuarios").document(userId)

    usuarioRef.update("numeroContacto", nuevoNumero)
        .addOnSuccessListener {
            Log.d("Firestore", "Número de contacto del usuario actualizado con éxito.")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error al actualizar el número de contacto del usuario", e)
            onFailure(e)
        }
}



private suspend fun verifyUserPassword(password: String): Boolean {
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        return false
    }
    val credential = EmailAuthProvider.getCredential(user.email ?: "", password)
    return try {
        user.reauthenticate(credential).await()
        true
    } catch (e: FirebaseAuthException) {
        Log.e("Auth", "Error al reautenticar", e)
        false
    }
}

