package com.david.easycutter.model

import com.david.easycutter.model.enums.Role

/**
 * Clase que representa un usuario.
 *
 * Esta clase almacena información sobre un usuario, incluyendo su correo electrónico, nombre, apellidos,
 * URL del avatar y rol dentro de la aplicación.
 *
 * @author David Albarrán García
 */
class Usuario(
    val email: String ="",
    val nombre: String ="",
    val apellidos: String ="",
    val avatarUrl: String ="",
    var rol: Role = Role.User
) {
    fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "email" to this.email,
            "nombre" to this.nombre,
            "apellidos" to this.apellidos,
            "avatarUrl" to this.avatarUrl,
            "rol" to this.rol,
        )
    }
}
