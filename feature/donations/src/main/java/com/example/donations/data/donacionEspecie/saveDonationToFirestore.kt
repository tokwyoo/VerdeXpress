package com.example.donations.data.donacionEspecie

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import java.util.Calendar
import java.util.TimeZone

fun saveDonationToFirestore(
    name: String,
    contactNumber: String,
    location: String,
    parkToDonate: String,
    resourceType: String,
    resource: String,
    quantity: String,
    condition: String,
    imageUrls: List<String>,
    estimatedDonationDate: String,
    urlComprobante: String,
) {
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()

    // Configuramos la zona horaria de Hermosillo
    val timeZone = TimeZone.getTimeZone("America/Hermosillo")
    val calendar = Calendar.getInstance(timeZone)

    // Obtenemos el timestamp ajustado a la zona horaria de Hermosillo
    val createdAt = calendar.time

    // Datos de la donación
    val donationData = hashMapOf(
        "donante_nombre" to name,
        "donante_contacto" to contactNumber,
        "ubicacion" to location,
        "parque_donado" to parkToDonate,
        "tipo_recurso" to resourceType,
        "recurso" to resource,
        "cantidad" to quantity,
        "condicion" to condition,
        "imagenes" to imageUrls,
        "fecha_estimada_donacion" to estimatedDonationDate,
        "registro_usuario" to auth.currentUser?.uid, // ID del usuario que realiza la donación
        "registro_estado" to "Pendiente", // Estado inicial de la donación
        "created_at" to Timestamp(createdAt), // Timestamp ajustado a Hermosillo
        "url_comprobante" to urlComprobante // URL del comprobante de donación
    )

    // Guardar la donación en Firestore
    db.collection("donaciones_especie")
        .add(donationData)
        .addOnSuccessListener {
            // Éxito al guardar los datos
            Log.d("Firestore", "Donación guardada correctamente con id: ${it.id}")
        }
        .addOnFailureListener { e ->
            // Error al guardar los datos
            Log.e("Firestore", "Error al guardar la donación: ${e.message}")
        }

    // ----- INICIO FLUJO "NOTIFICACION PARA EL ADMINISTRADOR"
    val notificationData = hashMapOf(
        "titulo" to "Nueva solicitud de donación en especie",
        "mensaje" to "El usuario $name ha enviado una solicitud para donar $resource al parque $parkToDonate. Puedes revisarla en la sección de Donaciones.",
        "fecha" to FieldValue.serverTimestamp(),
        "leido_por" to emptyList<String>()
    )

    db.collection("notificaciones_admin")
        .add(notificationData)
        .addOnSuccessListener {
            Log.d("Firestore", "Notificación guardada correctamente con id: ${it.id}")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error al guardar notificación: ${e.message}")
        }
    // ----- FIN FLUJO "NOTIFICACION PARA EL ADMINISTRADOR"
}