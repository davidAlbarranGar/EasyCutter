package com.david.easycutter.model

/**
 * Clase que representa una cita en la gestión de peluquerías.
 *
 * Esta clase almacena información sobre una cita, incluyendo el identificador de la peluquería,
 * el identificador del cliente, la fecha de la cita, la ubicación geográfica y el estado de la cita.
 *
 * @author David Albarrán García
 */
class Cita (
    val idPeluqueria: String? = "",
    val idCliente: String? = "",
    val fecha: String? = "",
    var latitud: Double = 0.0,
    var longitud: Double = 0.0,
    val estado: String? = "Aceptada"
)