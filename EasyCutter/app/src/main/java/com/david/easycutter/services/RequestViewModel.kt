package com.david.easycutter.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.david.easycutter.model.Solicitud
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar operaciones relacionadas con solicitudes  de usuarios
 * y peluquerías en Firebase Firestore.
 *
 * @author David Albarrán García
 */
class RequestViewModel {
    /**
     * Guarda una solicitud a Firebase Firestore.
     *
     * @param collection Nombre de la colección en Firestore donde se almacenarán las solicitudes.
     * @param request Objeto Solicitud a enviar.
     * @param context Contexto de la aplicación.
     */
    fun sendRequest(
        collection: String?,
        request: Solicitud,
        context: Context
    ) = CoroutineScope(Dispatchers.IO).launch {
        val fireStoreRef = Firebase.firestore
            .collection(collection.toString())
            .document(request.tipo+"-"+request.peluqueria.peluquero)

        ImageViewModel().saveLogoImage(request.peluqueria.logoUrl!!.toUri(),request.peluqueria.idPeluqueria.toString(), onSuccess = {
            Log.e("Logo","Logo guardado correctamente")
        }, onFailure = {
            Log.e("Logo","Error al guardar logo")
        })

        try {
            fireStoreRef.set(request)
                .addOnSuccessListener {
                    // Mostrar un mensaje si la operación de guardado fue exitosa
                    if (fireStoreRef.id!=("bcaRcS2pAptLLTIHSE1z")) {
                        Toast.makeText(context, "Solicitud enviada correctamente", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) {
            // Mostrar un mensaje en caso de error
            Toast.makeText(context, "Error al enviar solicitud", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Obtiene todas las solicitudes de Firebase Firestore.
     *
     * @param collection Nombre de la colección en Firestore donde se encuentran las solicitudes.
     * @param context Contexto de la aplicación.
     * @param onDataReceived Callback que se llama cuando se reciben las solicitudes, proporciona una lista de Solicitud.
     */
    fun getAllRequest(collection: String, context: Context, onDataReceived: (List<Solicitud>) -> Unit) {
        val collectionRef = Firebase.firestore.collection(collection)

        collectionRef.get()
            .addOnSuccessListener { it ->
                val listRequest = mutableListOf<Solicitud>()
                for (document in it) {
                    if (document.id != "bcaRcS2pAptLLTIHSE1z") {
                        listRequest.add(document.toObject<Solicitud>())
                    }
                }
                onDataReceived(listRequest)
            }
            .addOnFailureListener { exception ->
                // Mostrar un mensaje en caso de error al obtener datos
                Toast.makeText(context, "Error al obtener solicitudes", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Elimina una solicitud de Firebase Firestore.
     *
     * @param collection Nombre de la colección en Firestore donde se encuentra la solicitud a eliminar.
     * @param request Objeto Solicitud a eliminar.
     * @param context Contexto de la aplicación.
     */
    fun deleteRequest(collection: String, request: Solicitud, context: Context) {
        val fireStoreRef = Firebase.firestore
            .collection(collection)
            .document(request.tipo+"-"+request.peluqueria.peluquero)

        try {
            fireStoreRef.delete().addOnSuccessListener {
                Log.d("Info","Solicitud borrada correctamente")
            }
        } catch (e: Exception) {
            // Mostrar un mensaje en caso de error
            Toast.makeText(context, "Error al eliminar solicitud", Toast.LENGTH_SHORT).show()
        }
    }
}