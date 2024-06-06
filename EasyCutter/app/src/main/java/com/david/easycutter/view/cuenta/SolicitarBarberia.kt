package com.david.easycutter.view.cuenta

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.david.easycutter.model.Peluqueria
import com.david.easycutter.model.Solicitud
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.model.enums.TypeRequest
import com.david.easycutter.services.AuthScreenViewModel
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
 * Composable que representa la pantalla de "Solicitar Barbería".
 * Utiliza una barra de aplicación simple y muestra el contenido de la solicitud.
 *
 * @param navControler Controlador de navegación para manejar la navegación entre pantallas.
 */
@Composable
fun SolicitarBarberia(navControler: NavHostController) {
    SimpleAppBar("Enviar Solicitud", navControler) {
        EnviarSolicitud(navControler)
    }
}

/**
 * Composable que maneja el flujo de la solicitud de creación de barbería.
 * Gestiona los pasos del formulario y los estados correspondientes.
 *
 * @param navController Controlador de navegación.
 * @param contexto Contexto de la aplicación, por defecto utiliza el contexto local.
 */
@Composable
fun EnviarSolicitud(navController: NavHostController, contexto: Context = LocalContext.current) {
    val nombre = rememberSaveable { mutableStateOf("") }
    val logoUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    var listadoServicios = rememberSaveable { mutableStateOf("") }
    val pasoActual = rememberSaveable { mutableStateOf(1) }

    Column(
        Modifier
            .fillMaxSize()
            .background(color = Color(202, 240, 248))
    ) {
        when (pasoActual.value) {
            1 -> PasoUno(nombre, logoUri, pasoActual)
            2 -> PasoDos(listadoServicios, pasoActual)
            3 -> PasoTres(nombre, logoUri, listadoServicios, navController, contexto)
        }
    }
}

/**
 * Primer paso del formulario de solicitud de barbería.
 * Solicita el nombre de la barbería y permite seleccionar un logo desde la galería.
 *
 * @param nombre Estado mutable para almacenar el nombre de la barbería.
 * @param logoUri Estado mutable para almacenar la URI del logo seleccionado.
 * @param pasoActual Estado mutable para controlar el paso actual del formulario.
 */
@Composable
fun PasoUno(
    nombre: MutableState<String>,
    logoUri: MutableState<Uri?>,
    pasoActual: MutableState<Int>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(202, 240, 248))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                logoUri.value = uri
            }
        }

        // Entrada de texto para el nombre
        NormalInput(
            variableState = nombre,
            labelId = "Nombre",
            keyBoardType = KeyboardType.Text
        )

        // Botón para seleccionar avatar desde la galería
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color(0, 180, 216))
        ) {
            Text("Seleccionar Logo")
        }

        // Muestra la imagen seleccionada, si hay alguna
        logoUri.value?.let { uri ->
            Text("Logo:", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            val imageUri = uri

            var expanded by remember { mutableStateOf(false) }

            val clickModifier = Modifier.clickable { expanded = true }

            if (expanded) {
                // Si está expandida, muestra la imagen en una nueva pantalla
                Image(
                    painter = rememberImagePainter(imageUri),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { expanded = false }
                )
            } else {
                // Si no está expandida, muestra la miniatura
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp) // Tamaño del logo
                        .clip(CircleShape) // Forma redonda
                        .padding(8.dp)
                        .then(clickModifier) // Aplica el modificador clickable
                ) {
                    Image(
                        painter = rememberImagePainter(imageUri),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp) // Tamaño del logo
                            .clip(CircleShape)
                            .background(Color.Gray) // Color de fondo del círculo
                            .border(2.dp, Color.Gray, CircleShape) // Borde opcional
                    )
                }
            }
        }

        // Botón para avanzar al siguiente paso
        Button(
            onClick = { pasoActual.value = 2 },
            enabled = nombre.value.isNotBlank() && logoUri.value.toString().isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color(0, 119, 182))
        ) {
            Text("Siguiente", color = Color.White)
        }
    }
}

/**
 * Segundo paso del formulario de solicitud de barbería.
 * Solicita la lista de servicios ofrecidos por la barbería.
 *
 * @param listadoServicios Estado mutable para almacenar la lista de servicios.
 * @param pasoActual Estado mutable para controlar el paso actual del formulario.
 */
@Composable
fun PasoDos(listadoServicios: MutableState<String>, pasoActual: MutableState<Int>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(202, 240, 248)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NormalInput(
            variableState = listadoServicios,
            labelId = "Ejemplo:\nPelado Normal -> 5€\nDegradado -> 8€",
            singleLine = false,
            keyBoardType = KeyboardType.Text
        )

        Button(
            onClick = {
                pasoActual.value = 3
            },
            enabled = listadoServicios.value.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color(0, 119, 182))
        ) {
            Text("Siguiente", color = Color.White)
        }
    }
}

/**
 * Tercer paso del formulario de solicitud de barbería.
 * Solicita la ubicación de la barbería y envía la solicitud.
 *
 * @param nombre Estado mutable para almacenar el nombre de la barbería.
 * @param logoUri Estado mutable para almacenar la URI del logo seleccionado.
 * @param listadoServicios Estado mutable para almacenar la lista de servicios.
 * @param navController Controlador de navegación.
 * @param contexto Contexto de la aplicación.
 */
@Composable
fun PasoTres(
    nombre: MutableState<String>,
    logoUri: MutableState<Uri?>,
    listadoServicios: MutableState<String>,
    navController: NavHostController,
    contexto: Context
) {
    val posicionMarcador = remember { mutableStateOf<LatLng?>(null) }
    val propiedades = remember { mutableStateOf(MapProperties(mapType = MapType.HYBRID)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(202, 240, 248)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Selecciona tu ubicación:")

        // Mapa interactivo para seleccionar la ubicación
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            properties = propiedades.value,
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

        // Mostrar ubicación seleccionada
        Text("Ubicación seleccionada: ${
            posicionMarcador.value?.let {
                "(${it.latitude}, ${it.longitude})"
            } ?: "No seleccionada"
        }")

        // Botón para enviar la solicitud
        Button(
            enabled = posicionMarcador.value != null,
            onClick = {
                // Crear una nueva instancia de Peluquería
                val barberia = Peluqueria(
                    idPeluqueria = nombre.value + "-" + AuthScreenViewModel().getCurrentUser()?.email.toString(),
                    nombre = nombre.value,
                    logoUrl = logoUri.value.toString(),
                    listadoServicios = listadoServicios.value,
                    latitud = posicionMarcador.value!!.latitude,
                    longitud = posicionMarcador.value!!.longitude,
                    peluquero = AuthScreenViewModel().getCurrentUser()?.email.toString()
                )

                // Crear una nueva instancia de Solicitud
                val solicitud = Solicitud(
                    tipo  = TypeRequest.Save.name,
                    peluqueria = barberia
                )

                // Enviar la solicitud
                RequestViewModel().sendRequest("request", solicitud, contexto)

                // Navegar de vuelta a la pantalla de contacto
                navController.navigate(Screens.Contactanos.name)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color(0, 119, 182)) // Color azul claro
        ) {
            Text("Enviar Solicitud", color = Color.White) // Color blanco
        }
    }
}