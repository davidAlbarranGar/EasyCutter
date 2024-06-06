package com.david.easycutter.model

/**
 * Clase que representa una solicitud de una acción sobre una peluquerías.
 *
 * Esta clase almacena información sobre una solicitud, incluyendo el tipo de solicitud y la peluquería asociada.
 *
 * @author David Albarrán García
 */
class Solicitud (
    val tipo: String? = "",
    val  peluqueria: Peluqueria = Peluqueria()
)