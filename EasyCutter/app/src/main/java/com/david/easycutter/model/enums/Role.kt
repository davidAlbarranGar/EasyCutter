package com.david.easycutter.model.enums

/**
 * Enumeración que define los diferentes roles dentro del sistema.
 *
 * @autor David Albarrán García
 */
enum class Role {
    /**
     * Rol de administrador. Este rol tiene permisos completos dentro del sistema.
     */
    Admin,

    /**
     * Rol de usuario. Este rol tiene permisos limitados y es el rol más común.
     */
    User,

    /**
     * Rol de barbero. Este rol tiene permisos específicos para la gestión de citas y servicios de barbería.
     */
    Barber
}