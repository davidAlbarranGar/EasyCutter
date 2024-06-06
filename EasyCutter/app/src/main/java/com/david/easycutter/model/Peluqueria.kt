package com.david.easycutter.model

/**
 * Clase que representa una peluquería.
 *
 * Esta clase almacena información sobre una peluquería, incluyendo su identificador, nombre, URL del logo,
 * listado de servicios ofrecidos, nombre del peluquero, y ubicación geográfica (latitud y longitud).
 *
 * @author David Albarrán García
 */
class Peluqueria(
    var idPeluqueria: String? = "",
    val nombre: String = "",
    var logoUrl: String? = "",
    val listadoServicios: String = "",
    var peluquero: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0
) {
    /**
     * Convierte los atributos de la peluquería a un mapa mutable.
     *
     * @return Un mapa mutable que contiene los atributos de la peluquería.
     */
    fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "idPeluqueria" to this.idPeluqueria,
            "nombre" to this.nombre,
            "logoUrl" to this.logoUrl,
            "listadoServicios" to this.listadoServicios,
            "peluquero" to this.peluquero,
            "latitud" to this.latitud,
            "longitud" to this.longitud
        )
    }
}