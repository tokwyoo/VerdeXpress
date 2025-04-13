package com.example.donations.data.notificaciones

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.Timestamp

/**
 * Clase para gestionar la obtención de notificaciones del usuario desde Firestore.
 */
class GetUserNotifications {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    /**
     * Clase de datos para representar una notificación de usuario.
     */
    data class UserNotification(
        val id: String = "",
        val titulo: String = "",
        val mensaje: String = "",
        val fecha: Long = 0,
        val leido: Boolean = false,
        val tipo: String = "" // Tipo de notificación: "Donaciones" o "Avance de parques"
    )

    /**
     * Obtiene las notificaciones del usuario actual.
     *
     * @param onSuccess Callback que se ejecuta cuando se obtienen las notificaciones correctamente.
     * @param onFailure Callback que se ejecuta cuando ocurre un error al obtener las notificaciones.
     */
    fun getUserNotifications(
        onSuccess: (List<UserNotification>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onFailure(Exception("Usuario no autenticado"))
            return
        }

        val uid = currentUser.uid

        // Consulta las notificaciones en Firestore donde el destinatario es el uid del usuario actual
        listenerRegistration = db.collection("notificaciones_user")
            .whereEqualTo("destinatario", uid)
            .orderBy("fecha", Query.Direction.DESCENDING) // Ordenar por fecha, más recientes primero
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onFailure(e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val notificationsList = mutableListOf<UserNotification>()

                    for (document in snapshot.documents) {
                        try {
                            // Manejar correctamente el campo fecha que es un Timestamp
                            val fechaValue = document.get("fecha")
                            val fechaLong = when (fechaValue) {
                                is Timestamp -> fechaValue.seconds * 1000 // Convertir segundos a milisegundos
                                is Long -> fechaValue
                                is Double -> fechaValue.toLong()
                                null -> 0L
                                else -> 0L
                            }

                            val notification = UserNotification(
                                id = document.id,
                                titulo = document.getString("titulo") ?: "",
                                mensaje = document.getString("mensaje") ?: "",
                                fecha = fechaLong,
                                leido = document.getBoolean("leido") ?: false,
                                tipo = document.getString("tipo") ?: "Otros" // Tipo de notificación
                            )
                            notificationsList.add(notification)
                        } catch (ex: Exception) {
                            // Log the error but continue processing other notifications
                            println("Error procesando notificación ${document.id}: ${ex.message}")
                        }
                    }

                    onSuccess(notificationsList)
                } else {
                    onSuccess(emptyList())
                }
            }
    }

    /**
     * Marca una notificación como leída en Firestore.
     *
     * @param notificationId ID de la notificación a marcar como leída.
     */
    fun markAsRead(notificationId: String) {
        db.collection("notificaciones_user")
            .document(notificationId)
            .update("leido", true)
    }

    /**
     * Elimina los listeners para evitar fugas de memoria.
     */
    fun removeListeners() {
        listenerRegistration?.remove()
    }
}