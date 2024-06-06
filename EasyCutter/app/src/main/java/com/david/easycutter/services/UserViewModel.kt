package com.david.easycutter.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.david.easycutter.model.Usuario
import com.david.easycutter.model.enums.Role
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

/**
 * Clase que proporciona funcionalidades relacionadas con la gestión de usuarios.
 *
 * @author David Albarrán García
 */
class UserViewModel {
    /**
     * Recupera todos los usuarios y ejecuta una acción cuando se obtienen los datos devolviendo los usuarios.
     *
     * @param collection     Colección de la base de datos Firestore.
     * @param context        Contexto de la aplicación.
     * @param onDataReceived Acción a realizar cuando se reciben los datos.
     */
    fun getAllUser(collection: String,context: Context, onDataReceived: (List<Usuario>) -> Unit) {
        val collectionRef = Firebase.firestore.collection(collection)

        collectionRef.get()
            .addOnSuccessListener { it ->
                val listUsers = mutableListOf<Usuario>()
                for (document in it) {
                    if (document.id != "1WIctcU7NPu8glORi9R") {
                        listUsers.add(document.toObject<Usuario>())
                    }
                }
                onDataReceived(listUsers)
            }
            .addOnFailureListener { exception ->
                // Mostrar un mensaje en caso de error al obtener datos
                Toast.makeText(context, "Error al obtener usuarios", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Recupera el usuario actual de la base de datos y ejecuta una acción cuando se obtiene el usuario
     * devolviendo el usuario obtenido.
     *
     * @param collection     Colección de la base de datos Firestore.
     * @param context        Contexto de la aplicación.
     * @param onDataReceived Acción a realizar cuando se recibe el usuario.
     */
    fun getCurrentUser(collection: String, context: Context, onDataReceived: (Usuario?) -> Unit) {
        val userEmail = AuthScreenViewModel().getCurrentUser()?.email
        Log.e("Hola",userEmail.toString())
        val fireStoreRef = Firebase.firestore
            .collection("users")
            .document(userEmail.toString())
        fireStoreRef.get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = it.toObject<Usuario>()
                    onDataReceived(user)
                }
            }
            .addOnFailureListener { exception ->
                // Mostrar un mensaje en caso de error al obtener datos
                Toast.makeText(context, "Error al obtener usuario actual", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Recupera un usuario específico de la base de datos y ejecuta una acción cuando se obtiene el usuario
     * devolviendo el usuario obtenido.
     *
     * @param emailUser      Correo electrónico del usuario a recuperar.
     * @param context        Contexto de la aplicación.
     * @param onDataReceived Acción a realizar cuando se recibe el usuario.
     */
    fun getUser(emailUser: String, context: Context, onDataReceived: (Usuario?) -> Unit) {
        val fireStoreRef = Firebase.firestore
            .collection("users")
            .document(emailUser)
        Log.e("HolaGetUser",fireStoreRef.id)
        fireStoreRef.get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = it.toObject<Usuario>()
                    onDataReceived(user)
                }else{
                    val user = it.toObject<Usuario>()
                    onDataReceived(user)
                }
            }
            .addOnFailureListener { exception ->
                // Mostrar un mensaje en caso de error al obtener datos
                Toast.makeText(context, "Error al obtener usuario: $emailUser", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param userSend Usuario a guardar.
     * @param context  Contexto de la aplicación.
     */
    fun saveUser(userSend: Usuario, context:Context) {
        Log.e("HolaRegisterUser","Estoy en save User")
        ImageViewModel().saveAvatarImage(userSend.avatarUrl.toUri(), userSend.email, onSuccess = {
            Log.e("Imagen","GuardadaCorrectamente")
        }, onFailure = {
            Log.e("Imagen","ErrorGuardandoImagen")
        })
        val user = Usuario(
            email = userSend.email.lowercase(),
            avatarUrl = userSend.avatarUrl,
            nombre = userSend.nombre,
            apellidos = userSend.apellidos,
            rol = userSend.rol
        ).toMap()
        FirebaseFirestore.getInstance().collection("users").document(userSend.email.lowercase()).set(user)
            .addOnSuccessListener {
                Toast.makeText(context, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al crear usuario, vuelve a intentarlo más tarde", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Actualiza la información de un usuario en la base de datos.
     *
     * @param userSend Usuario con la información actualizada.
     * @param context  Contexto de la aplicación.
     */
    fun updateUser(userSend: Usuario, context: Context) {
        val emailLowercase = userSend.email.lowercase()
        val userUpdates = Usuario(
            emailLowercase,
            userSend.nombre,
            userSend.apellidos,
            userSend.avatarUrl,
            userSend.rol
        ).toMap()

        FirebaseFirestore.getInstance().collection("users").document(emailLowercase)
            .update(userUpdates)
            .addOnSuccessListener {
                Toast.makeText(context, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al actualizar usuario, vuelve a intentarlo más tarde", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Bloquea a un usuario, guardando su información en una colección separada y luego eliminándolo de la colección principal.
     *
     * @param collection Colección principal de usuarios.
     * @param user       Usuario a bloquear.
     * @param context    Contexto de la aplicación.
     */
    fun blockUser(
        collection: String,
        user: Usuario,
        context: android.content.Context,
    ) {
        saveBlockUser(user,context)
        deleteUser(collection,user,context)
    }

    private fun saveBlockUser(userSend: Usuario, context: Context) {
        val user = Usuario(
            email = userSend.email,
            avatarUrl = userSend.avatarUrl,
            nombre = userSend.nombre,
            apellidos = userSend.apellidos,
            rol = userSend.rol
        ).toMap()

        FirebaseFirestore.getInstance().collection("userBlock").document(userSend.email).set(user)
            .addOnSuccessListener {
                Toast.makeText(context, "Usuario bloqueado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al bloquear usuario, vuelve a intentarlo más tarde", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param collection Colección de la base de datos Firestore.
     * @param user       Usuario a eliminar.
     * @param context    Contexto de la aplicación.
     */
    fun deleteUser(
        collection: String,
        user: Usuario,
        context: android.content.Context,
    ) {
        val fireStoreRef = Firebase.firestore
            .collection(collection)
            .document(user.email)

        try {
            fireStoreRef.delete().addOnSuccessListener {
                Toast.makeText(context, "Usuario borrado correctamente", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Mostrar un mensaje en caso de error
            Toast.makeText(context, "Error al borrar usuario, vuelve a intentarlo más tarde", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Obtiene la información de un usuario bloqueado de la base de datos.
     *
     * @param email        Correo electrónico del usuario bloqueado.
     * @param context      Contexto de la aplicación.
     * @param onDataReceived Acción a realizar cuando se recibe el usuario bloqueado.
     */
    fun getUserBlock(email: String, context: Context, onDataReceived: (Usuario?) -> Unit){
        val fireStoreRef = Firebase.firestore
            .collection("userBlock")
            .document(email)

        try {
            fireStoreRef.get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val user = it.toObject<Usuario>()
                        onDataReceived(user)
                    }else{
                        val user = it.toObject<Usuario>()
                        onDataReceived(user)
                    }
                }
        } catch (e: Exception) {
            // Mostrar un mensaje en caso de error
            Toast.makeText(context, "Error al obtener usuarios bloqueados", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Edita el rol de un usuario en la base de datos.
     *
     * @param emailUser Correo electrónico del usuario cuyo rol se va a editar.
     * @param newRole   Nuevo rol del usuario.
     * @param context   Contexto de la aplicación.
     */
    fun editRoleUser(emailUser: String, newRole: Role, context: Context) {
        getUser(emailUser,context){
            if (it != null){
                var user=it
                user.rol=newRole
                updateUser(user,context)
            }
        }
    }

}