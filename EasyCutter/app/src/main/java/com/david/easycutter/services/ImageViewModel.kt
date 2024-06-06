package com.david.easycutter.services

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage


/**
 * ViewModel para gestionar operaciones relacionadas con imágenes en Firebase Storage.
 * @author David Albarrán García
 */
class ImageViewModel {

    private val storage = Firebase.storage

    /**
     * Guarda una imagen de avatar en Firebase Storage.
     *
     * @param imageUri URI de la imagen a guardar.
     * @param userEmail Correo electrónico del usuario para generar el nombre único de la imagen.
     * @param onSuccess Callback llamado cuando la imagen se guarda exitosamente, proporciona la URL de descarga.
     * @param onFailure Callback llamado en caso de fallo durante la operación de guardado.
     */
    fun saveAvatarImage(imageUri: Uri, userEmail: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        // Generar un nombre único para la imagen
        val imageName = userEmail

        // Crear referencia al almacenamiento de Firebase
        val storageRef = storage.reference.child("Avatar/$imageName")

        // Subir la imagen al almacenamiento
        val uploadTask = storageRef.putFile(imageUri)

        // Manejar éxito o fallo de la carga
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                onSuccess(downloadUri.toString())
            } else {
                task.exception?.let {
                    onFailure(it)
                }
            }
        }
    }

    /**
     * Carga una imagen de avatar desde Firebase Storage.
     *
     * @param LogoName Nombre del archivo de imagen en Firebase Storage.
     * @param onSuccess Callback llamado cuando la imagen se carga exitosamente, proporciona la URI de la imagen.
     * @param onFailure Callback llamado en caso de fallo durante la operación de carga.
     */
    fun loadAvatarImage(LogoName: String, onSuccess: (Uri) -> Unit, onFailure: (Exception) -> Unit) {
        // Obtener referencia al archivo en el almacenamiento de Firebase
        val storageRef = storage.reference.child("Avatar/$LogoName")

        // Descargar la imagen del almacenamiento
        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Llamar a la función de éxito con la URI de la imagen
                onSuccess(uri)
            }
            .addOnFailureListener { exception ->
                // Llamar a la función de fallo si ocurre algún error
                onFailure(exception)
            }
    }

    /**
     * Guarda una imagen de logo en Firebase Storage.
     *
     * @param imageUri URI de la imagen a guardar.
     * @param logoName Nombre del logo para generar el nombre único de la imagen.
     * @param onSuccess Callback llamado cuando el logo se guarda exitosamente, proporciona la URL de descarga.
     * @param onFailure Callback llamado en caso de fallo durante la operación de guardado.
     */
    fun saveLogoImage(imageUri: Uri, logoName: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        // Generar un nombre único para el logo
        val logoName = logoName

        // Crear referencia al almacenamiento de Firebase
        val storageRef = storage.reference.child("Logo/$logoName")

        // Subir el logo al almacenamiento
        val uploadTask = storageRef.putFile(imageUri)

        // Manejar éxito o fallo de la carga
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                onSuccess(downloadUri.toString())
            } else {
                task.exception?.let {
                    onFailure(it)
                }
            }
        }
    }

    /**
     * Carga una imagen de logo desde Firebase Storage.
     *
     * @param logoName Nombre del archivo de imagen en Firebase Storage.
     * @param onSuccess Callback llamado cuando el logo se carga exitosamente, proporciona la URI del logo.
     * @param onFailure Callback llamado en caso de fallo durante la operación de carga.
     */
    fun loadLogoImage(logoName: String, onSuccess: (Uri) -> Unit, onFailure: (Exception) -> Unit) {
        // Obtener referencia al archivo en el almacenamiento de Firebase
        val storageRef = storage.reference.child("Logo/$logoName")

        // Descargar el logo del almacenamiento
        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Llamar a la función de éxito con la URI del logo
                onSuccess(uri)
            }
            .addOnFailureListener { exception ->
                // Llamar a la función de fallo si ocurre algún error
                onFailure(exception)
            }
    }

    /**
     * Elimina una imagen de avatar de Firebase Storage.
     *
     * @param userEmail Correo electrónico del usuario para identificar la imagen a eliminar.
     * @param onSuccess Callback llamado cuando la imagen se elimina exitosamente.
     * @param onFailure Callback llamado en caso de fallo durante la operación de eliminación.
     */
    fun deleteAvatarImage(userEmail: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = storage.reference.child("Avatar/$userEmail")

        storageRef.delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    /**
     * Elimina una imagen de logo de Firebase Storage.
     *
     * @param logoName Nombre del logo para identificar la imagen a eliminar.
     * @param onSuccess Callback llamado cuando el logo se elimina exitosamente.
     * @param onFailure Callback llamado en caso de fallo durante la operación de eliminación.
     */
    fun deleteLogoImage(logoName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = storage.reference.child("Logo/$logoName")

        storageRef.delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}