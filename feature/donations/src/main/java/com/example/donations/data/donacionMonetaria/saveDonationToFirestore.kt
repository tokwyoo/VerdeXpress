package com.example.donations.data.donacionMonetaria

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import java.util.Calendar
import java.util.TimeZone

fun saveDonationToFirestore(
    nombre: String,
    correo: String,
    numTel: String,
    cantidad: String,
    metodoPago: String,
    parqueSeleccionado: String,
    UbicacionSeleccionado: String,
    quiereRecibo: Boolean,
    rfc: String,
    razon: String,
    domFiscal: String,
    cardBrand: String,
    lastCardDigits: String,
    transactionId: String,
    transactionDate: String
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
        "donante_nombre" to nombre,
        "donante_contacto" to numTel,
        "donante_correo" to correo,
        "cantidad" to cantidad,
        "metodo_pago" to metodoPago,
        "parque_seleccionado" to parqueSeleccionado,
        "ubicacion_seleccionada" to UbicacionSeleccionado,
        "quiere_recibo" to quiereRecibo,
        "rfc" to rfc,
        "razon_social" to razon,
        "domicilio_fiscal" to domFiscal,
        "card_brand" to cardBrand,
        "last_card_digits" to lastCardDigits,
        "transaction_id" to transactionId,
        "transaction_date" to transactionDate,
        "registro_usuario" to auth.currentUser?.uid, // ID del usuario que realiza la donación
        "created_at" to Timestamp(createdAt) // Timestamp ajustado a Hermosillo
    )

    // Guardar la donación en Firestore
    db.collection("donaciones_monetaria")
        .add(donationData)
        .addOnSuccessListener {
            // Éxito al guardar los datos
            Log.d("Firestore", "Donación guardada correctamente con id: ${it.id}")
        }
        .addOnFailureListener { e ->
            // Error al guardar los datos
            Log.e("Firestore", "Error al guardar la donación: ${e.message}")
        }

    // Agregar notificación al usuario
    val notificationData = hashMapOf(
        "titulo" to "¡Tu donación monetaria ha sido procesada!",
        "mensaje" to "Tu donación al parque $parqueSeleccionado ha sido procesada y registrada por el sistema. Puedes ver los detalles y consultar tu comprobante en la sección Donaciones de la app. Gracias por confiar en VerdeXpress para cuidar Hermosillo.",
        "fecha" to FieldValue.serverTimestamp(),
        "leido" to false,
        "destinatario" to auth.currentUser?.uid,
    )

    db.collection("notificaciones_user")
        .add(notificationData)
        .addOnSuccessListener {
            // Éxito al agregar la notificación
            Log.d("Firestore", "Notificación agregada correctamente con id: ${it.id}")
        }
        .addOnFailureListener { e ->
            // Error al agregar la notificación
            Log.e("Firestore", "Error al agregar la notificación: ${e.message}")
        }

}