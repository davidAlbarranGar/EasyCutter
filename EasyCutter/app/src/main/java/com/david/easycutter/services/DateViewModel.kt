package com.david.easycutter.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.david.easycutter.model.Cita
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona las operaciones relacionadas con las citas y reservas.
 *
 * Proporciona métodos para guardar citas, obtener citas por peluquería o cliente,
 * cancelar citas y eliminar citas.
 *
 * @author David Albarrán García
 */
class DateViewModel: ViewModel() {
    /**
     * Flujo de estado que representa las fechas reservadas.
     */
    private val _reservedDates = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    /**
     * Obtiene el flujo de estado de las fechas reservadas.
     */
    val reservedDates: StateFlow<Map<String, Boolean>> get() = _reservedDates

    /**
     * Flujo de estado que representa las citas en agenda.
     */
    private val _appointments = MutableStateFlow<List<Cita>>(emptyList())

    /**
     * Obtiene el flujo de estado de las citas en agenda.
     */
    val appointments: StateFlow<List<Cita>> get() = _appointments

    /**
     * Inicializa el ViewModel y comienza a escuchar los cambios en las reservas.
     */
    init {
        listenToReservations()
    }

    /**
     * Escucha los cambios en las reservas de citas en Firestore.
     */
    private fun listenToReservations() {
        val db = FirebaseFirestore.getInstance()
        db.collection("dates")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val reservations = mutableMapOf<String, Boolean>()
                for (doc in snapshots!!) {
                    reservations[doc.id] = true
                }
                _reservedDates.value = reservations
            }
    }

    /**
     * Guarda una nueva cita en Firestore.
     *
     * @param collection Nombre de la colección donde se guardará la cita.
     * @param date Datos de la cita a guardar.
     * @param context Contexto de la aplicación.
     */
    fun saveDate(
        collection: String?,
        date: Cita,
        context: Context
    ) = CoroutineScope(Dispatchers.IO).launch {
        val fireStoreRef = Firebase.firestore
            .collection(collection.toString())
            .document(date.fecha.orEmpty() + date.idPeluqueria)

        BarberShopViewModel().getBarberShopsById(date.idPeluqueria, context) { barberShop ->
            if (barberShop != null) {
                date.latitud = barberShop.latitud
                date.longitud = barberShop.longitud

                try {
                    fireStoreRef.set(date)
                        .addOnSuccessListener {
                            // Mostrar un mensaje si la operación de guardado fue exitosa
                            if (fireStoreRef.id != "WzMjTXm25Un1qX9yFcjb") {
                                Toast.makeText(context, "Cita reservada correctamente", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            // Mostrar un mensaje en caso de error
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                } catch (e: Exception) {
                    // Mostrar un mensaje en caso de error
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("Error", "No se encontró la ubicación")
            }
        }
    }

    /**
     * Obtiene las citas de una peluquería específica desde Firestore.
     *
     * @param barberShopId ID de la peluquería.
     * @param context Contexto de la aplicación.
     */
    fun fetchAppointments(barberShopId: String, context: Context) {
        val db = FirebaseFirestore.getInstance()
        db.collection("dates")
            .whereEqualTo("idPeluqueria", barberShopId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(context, "Error al obtener citas de peluquería: $barberShopId", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val appointmentsList = snapshots.mapNotNull { it.toObject(Cita::class.java) }
                    _appointments.value = appointmentsList
                }
            }
    }

    /**
     * Obtiene las citas de un cliente específico desde Firestore.
     *
     * @param clientId ID del cliente.
     * @param context Contexto de la aplicación.
     */
    fun fetchUserAppointments(clientId: String, context: Context) {
        val db = FirebaseFirestore.getInstance()
        db.collection("dates")
            .whereEqualTo("idCliente", clientId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(context, "Error al obtener citas del cliente: $clientId", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val appointmentsList = snapshots.mapNotNull { it.toObject(Cita::class.java) }
                    _appointments.value = appointmentsList
                }
            }
    }

    /**
     * Cancela una cita en Firestore cambiando su estado a "Cancelada".
     *
     * @param dateId ID de la cita a cancelar.
     * @param context Contexto de la aplicación.
     */
    fun cancelDate(dateId: String, context: Context) {
        val db = FirebaseFirestore.getInstance()
        val dateRef = db.collection("dates").document(dateId)
        dateRef
            .update("state", "Cancelada")
            .addOnSuccessListener {
                Toast.makeText(context, "Cita cancelada exitosamente.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cancelar cita", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Elimina una cita de Firestore.
     *
     * @param dateId ID de la cita a eliminar.
     * @param context Contexto de la aplicación.
     */
    fun deleteDate(dateId: String, context: Context) {
        val db = FirebaseFirestore.getInstance()
        val dateRef = db.collection("dates").document(dateId)
        dateRef
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Cita cancelada exitosamente.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cancelar cita", Toast.LENGTH_SHORT).show()
            }
    }
}