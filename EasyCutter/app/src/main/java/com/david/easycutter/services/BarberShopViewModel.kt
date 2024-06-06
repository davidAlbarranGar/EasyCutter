package com.david.easycutter.services

import android.content.Context
import android.widget.Toast
import com.david.easycutter.model.Peluqueria
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona las operaciones relacionadas con las peluquerías.
 *
 * Proporciona métodos para obtener todas las peluquerías, guardar una nueva peluquería,
 * eliminar una peluquería, obtener peluquerías por el nombre del peluquero o por su identificador,
 * y actualizar una peluquería existente.
 *
 * @author David Albarrán García
 */
class BarberShopViewModel {
    /**
     * Obtiene todas las peluquerías de la base de datos.
     *
     * @param collection Nombre de la colección de Firestore que contiene las peluquerías.
     * @param context Contexto de la aplicación.
     * @param onDataReceived Función de retorno que se ejecuta cuando se obtienen las peluquerías.
     */
    fun getAllBarberShop(collection: String, context: Context, onDataReceived: (List<Peluqueria>) -> Unit) {
        val collectionRef = Firebase.firestore.collection(collection)

        collectionRef.get()
            .addOnSuccessListener { it ->
                val listBarberShop = mutableListOf<Peluqueria>()
                for (document in it) {
                    if (document.id != "khjOK2lNEbsF9PS7zlQj") {
                        listBarberShop.add(document.toObject<Peluqueria>())
                    }
                }
                onDataReceived(listBarberShop)
            }
            .addOnFailureListener { exception ->
                // Mostrar un mensaje en caso de error al obtener datos
                Toast.makeText(context, "Error al obtener peluquerías", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Guarda una nueva peluquería en la base de datos.
     *
     * @param collection Nombre de la colección de Firestore donde se guardará la peluquería.
     * @param barberShop Datos de la peluquería a guardar.
     * @param context Contexto de la aplicación.
     */
    fun saveBarberShop(
        collection: String?,
        barberShop: Peluqueria,
        context: Context
    ) = CoroutineScope(Dispatchers.IO).launch {
        val fireStoreRef = Firebase.firestore
            .collection(collection.toString())
            .document(barberShop.nombre+"-"+barberShop.peluquero)

        barberShop.idPeluqueria=fireStoreRef.id

        try {
            fireStoreRef.set(barberShop)
                .addOnSuccessListener {
                    // Mostrar un mensaje si la operación de guardado fue exitosa
                    if (fireStoreRef.id!=("khjOK2lNEbsF9PS7zlQj")) {
                        Toast.makeText(context, "Peluquería creada correctamente", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) {
            // Mostrar un mensaje en caso de error
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Elimina una peluquería de la base de datos.
     *
     * @param collection Nombre de la colección de Firestore donde se encuentra la peluquería a eliminar.
     * @param barberShop Datos de la peluquería a eliminar.
     * @param context Contexto de la aplicación.
     */
    fun deleteBarberShop(collection: String, barberShop: Peluqueria, context: Context) {
        val fireStoreRef = Firebase.firestore
            .collection(collection)
            .document(barberShop.nombre+"-"+barberShop.peluquero)

        try {
            fireStoreRef.delete().addOnSuccessListener {
                Toast.makeText(context, "Barbería borrada correctamente", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Mostrar un mensaje en caso de error
            Toast.makeText(context, "Error al borrar peluquería: $barberShop", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Obtiene las peluquerías asociadas a un determinado peluquero.
     *
     * @param collection Nombre de la colección de Firestore que contiene las peluquerías.
     * @param barber Nombre del peluquero.
     * @param context Contexto de la aplicación.
     * @param onDataReceived Función de retorno que se ejecuta cuando se obtienen las peluquerías.
     */
    fun getBarberShopsByBarber(collection: String, barber: String?, context: Context, onDataReceived: (List<Peluqueria>) -> Unit) {
        val collectionRef = Firebase.firestore.collection(collection)

        collectionRef.whereEqualTo("peluquero", barber)
            .get()
            .addOnSuccessListener { it ->
                val listBarberShop = mutableListOf<Peluqueria>()
                for (document in it) {
                    if (document.id != "khjOK2lNEbsF9PS7zlQj") {
                        listBarberShop.add(document.toObject<Peluqueria>())
                    }
                }
                onDataReceived(listBarberShop)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al obtener peluquerías del barbero: $barber", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Obtiene una peluquería por su identificador.
     *
     * @param idBarberShop Identificador de la peluquería a obtener.
     * @param context Contexto de la aplicación.
     * @param onDataReceived Función de retorno que se ejecuta cuando se obtiene la peluquería.
     */
    fun getBarberShopsById(idBarberShop: String?, context: Context, onDataReceived: (Peluqueria?) -> Unit) {
        val collectionRef = Firebase.firestore.collection("barbershop")

        collectionRef.whereEqualTo("idPeluqueria", idBarberShop)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.firstOrNull()
                    val barberShop = document?.toObject(Peluqueria::class.java)
                    onDataReceived(barberShop)
                } else {
                    onDataReceived(null)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al obtener peluquerías del barbero: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Actualiza los datos de una peluquería existente en la base de datos.
     *
     * @param barberShop Datos actualizados de la peluquería.
     * @param context Contexto de la aplicación.
     * @param onComplete Función de retorno que se ejecuta cuando se completa la actualización.
     */
    fun updateBarberShop(barberShop: Peluqueria, context: Context, onComplete: () -> Unit) {
        val collectionRef = Firebase.firestore.collection("barbershop")
        val docRef = collectionRef.document(barberShop.idPeluqueria.orEmpty())

        docRef.set(barberShop.toMap())
            .addOnSuccessListener {
                onComplete()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al actualizar la barbería: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}