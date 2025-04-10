package com.example.profile.data

import android.util.Log
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

suspend fun enviarCorreoVerificacionNuevoCorreo(nuevoCorreo: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    try {
        currentUser?.updateEmail(nuevoCorreo)?.await()
        currentUser?.sendEmailVerification()?.await()
        Log.d("Auth", "Solicitud de verificación enviada a $nuevoCorreo.")
        onSuccess()
    } catch (e: FirebaseAuthException) {
        Log.e("EditEmail", "Error al solicitar verificación del nuevo correo (Auth)", e)
        if (e.errorCode == "ERROR_REQUIRES_RECENT_LOGIN") {
            onFailure(Exception("Esta operación requiere que vuelvas a iniciar sesión por seguridad."))
        } else {
            onFailure(e)
        }
    } catch (e: Exception) {
        Log.e("EditEmail", "Error al solicitar verificación del nuevo correo (General)", e)
        onFailure(e)
    }
}

// **Función para actualizar el correo electrónico en Firestore DESPUÉS de la verificación**
suspend fun actualizarCorreoFirestore(userId: String, nuevoCorreo: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val usuarioRef = db.collection("usuarios").document(userId)

    try {
        usuarioRef.update("correoElectronico", nuevoCorreo).await()
        Log.d("Firestore", "Campo correoElectronico actualizado a $nuevoCorreo para el usuario $userId.")
        onSuccess()
    } catch (e: Exception) {
        Log.e("EditEmail", "Error al actualizar el correo electrónico en Firestore", e)
        onFailure(e)
    }
}