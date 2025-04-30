package com.example.donations.data.inicio

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GetUserDonations {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var monetaryListener: ListenerRegistration? = null
    private var inKindListener: ListenerRegistration? = null

    data class UserDonation(
        val id: String,
        val type: String, // "monetaria" o "especie"
        val parkName: String,
        val details: String,
        val date: String,
        val rawDate: Date, // Para ordenar por fecha
        val estado: String? = null // Para obtener el estado de la donacion
    )

    // Versión mejorada que usa Flow para manejar streams de datos reactivos
    fun getUserDonationsFlow(): Flow<List<UserDonation>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            close(Exception("Usuario no autenticado"))
            return@callbackFlow
        }

        val allDonations = mutableListOf<UserDonation>()
        var monetaryCompleted = false
        var inKindCompleted = false

        // Formato para mostrar la fecha
        val displayFormat = SimpleDateFormat("dd MMMM yyyy", Locale("es", "MX"))

        // Función para verificar si ambas consultas han finalizado y enviar datos
        fun checkAndSendData() {
            val donationsCopy = allDonations.toList().sortedByDescending { it.rawDate }
            trySend(donationsCopy)
        }

        // Obtener donaciones monetarias
        monetaryListener = firestore.collection("donaciones_monetaria")
            .whereEqualTo("registro_usuario", currentUserId)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("UserDonations", "Error al obtener donaciones monetarias", exception)
                    return@addSnapshotListener
                }

                // Limpiar donaciones monetarias previas para evitar duplicados
                allDonations.removeAll { it.type == "monetaria" }

                if (snapshot != null) {
                    for (document in snapshot) {
                        try {
                            val id = document.id
                            val parkName = document.getString("parque_seleccionado") ?: "Desconocido"
                            val amount = document.getString("cantidad") ?: "0"
                            val createdAt = document.getTimestamp("created_at")?.toDate() ?: Date()
                            val formattedDate = displayFormat.format(createdAt)

                            allDonations.add(
                                UserDonation(
                                    id = id,
                                    type = "monetaria",
                                    parkName = parkName,
                                    details = "Monto \$$amount",
                                    date = formattedDate,
                                    rawDate = createdAt
                                )
                            )
                        } catch (e: Exception) {
                            Log.e("UserDonations", "Error al procesar donación monetaria", e)
                        }
                    }
                }

                monetaryCompleted = true
                if (inKindCompleted) {
                    checkAndSendData()
                }
            }

        // Obtener donaciones en especie
        inKindListener = firestore.collection("donaciones_especie")
            .whereEqualTo("registro_usuario", currentUserId)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("UserDonations", "Error al obtener donaciones en especie", exception)
                    return@addSnapshotListener
                }

                // Limpiar donaciones en especie previas para evitar duplicados
                allDonations.removeAll { it.type == "especie" }

                if (snapshot != null) {
                    for (document in snapshot) {
                        try {
                            if (document.getString("registro_estado") == "Aprobada") {
                                val id = document.id
                                val parkName = document.getString("parque_donado") ?: "Desconocido"
                                val resource = document.getString("recurso") ?: "Desconocido"
                                val createdAt = document.getTimestamp("created_at")?.toDate() ?: Date()
                                val formattedDate = displayFormat.format(createdAt)

                                allDonations.add(
                                    UserDonation(
                                        id = id,
                                        type = "especie",
                                        parkName = parkName,
                                        details = "Recurso donado: $resource",
                                        date = formattedDate,
                                        rawDate = createdAt,
                                        estado = document.getString("registro_estado")
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("UserDonations", "Error al procesar donación en especie", e)
                        }
                    }
                }

                inKindCompleted = true
                if (monetaryCompleted) {
                    checkAndSendData()
                }
            }

        // Cuando el Flow se cancela, cerramos los listeners
        awaitClose {
            monetaryListener?.remove()
            inKindListener?.remove()
        }
    }

    // Versión que obtiene datos una sola vez sin mantener listeners activos
    fun getUserDonations(
        onSuccess: (List<UserDonation>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            onFailure(Exception("Usuario no autenticado"))
            return
        }

        // Primero eliminamos cualquier listener previo
        removeListeners()

        val allDonations = mutableListOf<UserDonation>()
        var monetaryCompleted = false
        var inKindCompleted = false

        // Formato para mostrar la fecha
        val displayFormat = SimpleDateFormat("dd MMMM yyyy", Locale("es", "MX"))

        // Obtener donaciones monetarias una sola vez sin listener
        firestore.collection("donaciones_monetaria")
            .whereEqualTo("registro_usuario", currentUserId)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot) {
                    try {
                        val id = document.id
                        val parkName = document.getString("parque_seleccionado") ?: "Desconocido"
                        val amount = document.getString("cantidad") ?: "0"
                        val createdAt = document.getTimestamp("created_at")?.toDate() ?: Date()
                        val formattedDate = displayFormat.format(createdAt)

                        allDonations.add(
                            UserDonation(
                                id = id,
                                type = "monetaria",
                                parkName = parkName,
                                details = "Monto \$$amount",
                                date = formattedDate,
                                rawDate = createdAt
                            )
                        )
                    } catch (e: Exception) {
                        Log.e("UserDonations", "Error al procesar donación monetaria", e)
                    }
                }

                monetaryCompleted = true
                if (inKindCompleted) {
                    // Ordenar por fecha, más reciente primero
                    allDonations.sortByDescending { it.rawDate }
                    onSuccess(allDonations)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UserDonations", "Error al obtener donaciones monetarias", exception)
                onFailure(exception)
            }

        // Obtener donaciones en especie una sola vez sin listener
        firestore.collection("donaciones_especie")
            .whereEqualTo("registro_usuario", currentUserId)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot) {
                    try {
                        if (document.getString("registro_estado") == "Aprobada") {
                            val id = document.id
                            val parkName = document.getString("parque_donado") ?: "Desconocido"
                            val resource = document.getString("recurso") ?: "Desconocido"
                            val createdAt = document.getTimestamp("created_at")?.toDate() ?: Date()
                            val formattedDate = displayFormat.format(createdAt)

                            allDonations.add(
                                UserDonation(
                                    id = id,
                                    type = "especie",
                                    parkName = parkName,
                                    details = "Recurso donado: $resource",
                                    date = formattedDate,
                                    rawDate = createdAt,
                                    estado = document.getString("registro_estado")
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("UserDonations", "Error al procesar donación en especie", e)
                    }
                }

                inKindCompleted = true
                if (monetaryCompleted) {
                    // Ordenar por fecha, más reciente primero
                    allDonations.sortByDescending { it.rawDate }
                    onSuccess(allDonations)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UserDonations", "Error al obtener donaciones en especie", exception)
                onFailure(exception)
            }
    }

    fun removeListeners() {
        monetaryListener?.remove()
        inKindListener?.remove()
        monetaryListener = null
        inKindListener = null
    }
}