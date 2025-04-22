package com.example.donations.data.inicio

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
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

    fun getUserDonations(
        onSuccess: (List<UserDonation>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            onFailure(Exception("Usuario no autenticado"))
            return
        }

        val allDonations = mutableListOf<UserDonation>()
        var monetaryCompleted = false
        var inKindCompleted = false

        // Función para verificar si ambas consultas han finalizado
        fun checkCompletion() {
            if (monetaryCompleted && inKindCompleted) {
                // Ordenar por fecha, más reciente primero
                allDonations.sortByDescending { it.rawDate }
                onSuccess(allDonations)
            }
        }

        // Formato para mostrar la fecha
        val displayFormat = SimpleDateFormat("dd MMMM yyyy", Locale("es", "MX"))

        // Obtener donaciones monetarias
        monetaryListener = firestore.collection("donaciones_monetaria")
            .whereEqualTo("registro_usuario", currentUserId)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("UserDonations", "Error al obtener donaciones monetarias", exception)
                    monetaryCompleted = true
                    checkCompletion()
                    return@addSnapshotListener
                }

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
                checkCompletion()
            }

        // Obtener donaciones en especie
        inKindListener = firestore.collection("donaciones_especie")
            .whereEqualTo("registro_usuario", currentUserId)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("UserDonations", "Error al obtener donaciones en especie", exception)
                    inKindCompleted = true
                    checkCompletion()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    for (document in snapshot) {
                        try {
                            if (document.getString("registro_estado") == "Aprobada")
                            {
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
                checkCompletion()
            }
    }

    fun removeListeners() {
        monetaryListener?.remove()
        inKindListener?.remove()
    }
}