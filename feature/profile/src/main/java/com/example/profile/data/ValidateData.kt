package com.example.profile.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

suspend fun actualizarCorreoElectronicoConReautenticacion(nuevoCorreo: String, contrasena: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    //Con esta función, al dar clic en el botón de confirmar en EditEmailScreen, se le envía un correo al usuario (a su nueva dirección
    //de email) para que confirme que quiere que se realize este cambio, al dar clic en el enlace del correo enviado, se actualiza
    //el auth en Firebase y muestra la nueva dirección de correo
    val auth = FirebaseAuth.getInstance()
    val usuario = auth.currentUser

    try {
        if (usuario != null) {
            // 1. Reautenticar al usuario
            val credenciales = EmailAuthProvider.getCredential(usuario.email!!, contrasena)
            usuario.reauthenticate(credenciales).await()

            // 2. Solicitar verificación del nuevo correo
            usuario.verifyBeforeUpdateEmail(nuevoCorreo) //El cambio que hizo que funcionará, con esta función, el sistema espera a
                // que el usuario de clic en el enlace de confirmación, y ya después realiza el cambio
                .addOnCompleteListener { verificationTask ->
                    if (verificationTask.isSuccessful) {
                        Log.d("EmailVerification", "Se solicitó la verificación del nuevo correo: $nuevoCorreo")
                        onSuccess() // Informar éxito (el cambio se completará al verificar)
                        // El correo en Firebase Auth aún no ha cambiado.
                    } else { //Maneja si hay un error (Como una dirección de correo no válida, o errores de conexión que no dejarón que se envie el correo
                        Log.e("EmailVerification", "Error al solicitar la verificación del nuevo correo.", verificationTask.exception)
                        onFailure(Exception("Error al solicitar la verificación del nuevo correo electrónico: ${verificationTask.exception?.localizedMessage}"))
                    }
                }

        } else {
            onFailure(Exception("No hay un usuario autenticado actualmente"))
        }
    } catch (e: FirebaseAuthException) {
        Log.e("Auth", "Error al reautenticar al usuario: ${e.localizedMessage}")
        onFailure(e)
    } catch (e: Exception) {
        Log.e("Auth", "Error inesperado durante la actualización del correo: ${e.localizedMessage}")
        onFailure(e)
    }
}

fun verificarYActualizarCorreo(
    onSuccess: () -> Unit,
    onNotVerified: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser

    user?.reload()?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            if (user.isEmailVerified) {
                val db = Firebase.firestore
                val uid = user.uid
                val nuevoCorreo = user.email

                val userRef = db.collection("usuarios").document(uid)
                userRef.update("correoElectronico", nuevoCorreo)
                    .addOnSuccessListener {
                        Log.d("Correo", "Correo actualizado en Firestore")
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Correo", "Error al actualizar en Firestore", e)
                        onFailure(e)
                    }
            } else {
                Log.d("Correo", "El correo aún no ha sido verificado.")
                onNotVerified()
            }
        } else {
            Log.w("Correo", "Error al hacer reload del usuario", task.exception)
            onFailure(task.exception ?: Exception("Error desconocido al hacer reload del usuario."))
        }
    }
}




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

