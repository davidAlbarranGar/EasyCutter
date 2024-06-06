package com.david.easycutter.view.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

/**
 * Composable para mostrar la ubicación en un mapa de Google.
 *
 * @param latitude la latitud de la ubicación.
 * @param longitude la longitud de la ubicación.
 * @param navController el controlador de navegación para manejar las acciones de navegación.
 */
@Composable
fun ShowLocalitation(latitude: Double, longitude: Double, navController: NavHostController) {
    // Barra de aplicación simple con un título y un botón de navegación hacia atrás
    SimpleAppBar(title = "Ubicación", navController = navController) {
        Column(
            modifier = Modifier
                .background(color = Color(202, 240, 248))
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            val marker = LatLng(latitude, longitude) // Coordenadas del marcador en el mapa
            val properties = remember {
                mutableStateOf(MapProperties(mapType = MapType.HYBRID)) // Propiedades del mapa, incluido el tipo de mapa
            }
            val defaultCameraPosition = CameraPosition.fromLatLngZoom(marker,11f) // Posición de la cámara predeterminada
            val cameraPositionState = rememberCameraPositionState{
                position = defaultCameraPosition // Estado de la posición de la cámara
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    properties = properties.value,
                    cameraPositionState = cameraPositionState // Estado de la posición de la cámara
                ) {
                    Marker(state = rememberMarkerState(position = marker)) // Marcador en la ubicación especificada
                }
            }
        }
    }
}