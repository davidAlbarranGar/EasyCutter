package com.david.easycutter.view.peluquero

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.david.easycutter.model.Peluqueria
import com.david.easycutter.model.Solicitud
import com.david.easycutter.model.enums.TypeRequest
import com.david.easycutter.services.BarberShopViewModel
import com.david.easycutter.services.ImageViewModel
import com.david.easycutter.services.RequestViewModel
import com.david.easycutter.view.elements.NormalInput
import com.david.easycutter.view.elements.SimpleAppBar
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

/**
 * Composable para mostrar la pantalla de edición de una peluquería.
 *
 * @param idPeluqueria el ID de la peluquería que se está editando.
 * @param navController el controlador de navegación para manejar las acciones de navegación.
 */
@Composable
fun PantallaEditarPeluqueria(idPeluqueria: String, navController: NavHostController) {
    SimpleAppBar(title = "Editar peluquería", navController = navController) {
        EditarPeluqueria(idPeluqueria = idPeluqueria, navController = navController)
    }
}

/**
 * Composable para editar la información de una peluquería.
 *
 * @param idPeluqueria el ID de la peluquería que se está editando.
 * @param navController el controlador de navegación para manejar las acciones de navegación.
 */
@Composable
fun EditarPeluqueria(idPeluqueria: String, navController: NavHostController) {
    // Variables para almacenar la información de la peluquería
    val contexto = LocalContext.current
    val peluqueria = remember { mutableStateOf<Peluqueria?>(null) }
    val nombre = remember { mutableStateOf("") }
    val logoUrl = remember { mutableStateOf("") }
    val barbero = remember { mutableStateOf("") }
    val latitud = remember { mutableStateOf(0.0) }
    val longitud = remember { mutableStateOf(0.0) }
    val listadoServicios = remember { mutableStateOf("") }
    val editandoUbicacion = remember { mutableStateOf(false) }
    val posicionMarcador = remember { mutableStateOf<LatLng?>(null) }
    val propiedadesMapa = remember { mutableStateOf(MapProperties(mapType = MapType.HYBRID)) }

    // Obtener información de la peluquería y cargar el logo
    LaunchedEffect(idPeluqueria) {
        BarberShopViewModel().getBarberShopsById(idPeluqueria, contexto) { peluqueriaObtenida ->
            if (peluqueriaObtenida != null) {
                peluqueria.value = peluqueriaObtenida
                nombre.value = peluqueriaObtenida.nombre
                barbero.value = peluqueriaObtenida.peluquero
                latitud.value = peluqueriaObtenida.latitud
                longitud.value = peluqueriaObtenida.longitud
                listadoServicios.value = peluqueriaObtenida.listadoServicios
            }
            posicionMarcador.value = LatLng(latitud.value, longitud.value)
        }
    }

    // Cargar el logo de la peluquería
    LaunchedEffect(idPeluqueria) {
        ImageViewModel().loadLogoImage(idPeluqueria,
            onSuccess = {
                logoUrl.value = it.toString()
                Log.d("Logo", "Logo obtenido correctamente")
            },
            onFailure = {
                Log.d("Logo", "Error al obtener logo")
                Log.d("Logo", it.message.toString())
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(202, 240, 248))
            .padding(16.dp)
    ) {
        if (editandoUbicacion.value) {
            Text(text = "Selecciona tu ubicación:")
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                properties = propiedadesMapa.value,
                onMapClick = { latLng ->
                    posicionMarcador.value = latLng
                }
            ) {
                posicionMarcador.value?.let {
                    Marker(
                        state = MarkerState(position = it)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Ubicación seleccionada: ${
                posicionMarcador.value?.let {
                    "(${it.latitude}, ${it.longitude})"
                } ?: "No seleccionada"
            }")
            Button(
                onClick = {
                    posicionMarcador.value?.let {
                        latitud.value = it.latitude
                        longitud.value = it.longitude
                    }
                    editandoUbicacion.value = false
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(3, 4, 94))
            ) {
                Text("Confirmar Ubicación", color = Color.White)
            }
        } else {
            NormalInput(
                variableState = nombre,
                labelId = "Nombre",
                keyBoardType = KeyboardType.Text
            )

            NormalInput(
                variableState = barbero,
                labelId = "Barbero",
                keyBoardType = KeyboardType.Text
            )

            NormalInput(
                variableState = listadoServicios,
                labelId = "Listado Precios",
                singleLine = false,
                keyBoardType = KeyboardType.Text
            )

            val selectorImagen = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    logoUrl.value = uri.toString()
                }
            }
            Button(
                onClick = { selectorImagen.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0, 180, 216))
            ) {
                Text("Seleccionar Logo")
            }

            logoUrl.value.toUri().let { uri ->
                val uriImagen = uri
                var expandido by remember { mutableStateOf(false) }
                val modificadorClick = Modifier.clickable { expandido = true }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .then(modificadorClick)
                ) {
                    if (expandido) {
                        Image(
                            painter = rememberImagePainter(uriImagen),
                            contentDescription = "Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { expandido = false }
                        )
                    } else {
                        Image(
                            painter = rememberImagePainter(uriImagen),
                            contentDescription = "Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                                .border(2.dp, Color.Gray, CircleShape)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    editandoUbicacion.value = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0, 119, 182))
            ) {
                Text("Editar Ubicación", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val peluqueriaActualizada = Peluqueria(
                        idPeluqueria = peluqueria.value!!.idPeluqueria,
                        nombre = nombre.value,
                        logoUrl = logoUrl.value,
                        peluquero = barbero.value,
                        latitud = latitud.value,
                        longitud = longitud.value,
                        listadoServicios = listadoServicios.value
                    )

                    val solicitud = Solicitud(
                        tipo = TypeRequest.Edit.name,
                        peluqueria = peluqueriaActualizada
                    )

                    ImageViewModel().saveLogoImage(logoUrl.value!!.toUri(), peluqueriaActualizada.idPeluqueria!!,
                        onSuccess = {
                            Log.d("Logo", "Logo actualizado correctamente")
                        },
                        onFailure = {
                            Log.d("Logo", "Error al actualizar logo")
                        })

                    RequestViewModel().sendRequest("request", solicitud, contexto)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0, 119, 182))
            ) {
                Text("Guardar Cambios", color = Color.White)
            }
        }
    }
}