package com.david.easycutter.view.usuario

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.david.easycutter.model.Peluqueria
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.services.BarberShopViewModel
import com.david.easycutter.services.ImageViewModel
import com.david.easycutter.view.elements.AppBar

/**
 * Composable que muestra una lista de peluquerías disponibles para reservar citas.
 *
 * @param navControler El controlador de navegación para manejar la navegación entre pantallas.
 */
@Composable
fun PantallaListaReservarCita(navControler: NavHostController) {
    AppBar("Reservar Cita", navControler) {
        ListaReservarCita(navControler)
    }
}

/**
 * ListaReservarCita es una composable que muestra una lista de peluquerías disponibles para reservar citas.
 *
 * @param navControler El controlador de navegación para manejar la navegación entre pantallas.
 * @param context El contexto de la aplicación.
 */
@Composable
fun ListaReservarCita(navControler: NavHostController, context: Context = LocalContext.current) {
    val barberShopViewModel = BarberShopViewModel()

    Surface(Modifier.fillMaxSize()
        .background(color = Color(202, 240, 248))) {
        var listaBarberShop: List<Peluqueria> by remember {
            mutableStateOf(emptyList())
        }

        // Se inicia la recuperación de las peluquerías disponibles para reservar citas
        LaunchedEffect(key1 = true) {
            barberShopViewModel.getAllBarberShop("barbershop", context) { barberShops ->
                listaBarberShop = barberShops
            }
        }

        LazyColumn (Modifier.fillMaxSize()
            .background(color = Color(202, 240, 248))) {
            if (listaBarberShop.isNotEmpty()) {
                items(listaBarberShop) { barberShop ->
                    ContenidoItemBarberShop(barberShop, navControler)
                }
            }
        }
    }
}

/**
 * Composable que muestra la información de una peluquería en un elemento de lista.
 *
 * @param barberShop La peluquería que se va a mostrar.
 * @param navController El controlador de navegación para manejar la navegación entre pantallas.
 */
@Composable
fun ContenidoItemBarberShop(barberShop: Peluqueria, navController: NavHostController) {
    ElevatedCard(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(Color(0xFF90E0EF)), // Color: 144, 224, 239
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                var imageUri: Uri? by remember { mutableStateOf(null) }

                // Se carga la imagen de la peluquería
                LaunchedEffect(key1 = barberShop.logoUrl!!.toUri()) {
                    ImageViewModel().loadLogoImage(logoName = barberShop.idPeluqueria.toString(), onSuccess = { uri ->
                        imageUri = uri
                    }, onFailure = {
                        Log.e("Error", "Error al obtener avatar")
                    })
                }

                Image(
                    painter = rememberImagePainter(imageUri),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    // Se muestra el nombre y el peluquero de la peluquería
                    Text(
                        text = barberShop.nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(3, 4, 94)
                    )
                    Text(
                        text = barberShop.peluquero,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0, 119, 182)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            // Botón para ver la ubicación de la peluquería
            Button(
                onClick = {
                    navController.navigate("${Screens.ShowLocation.name}/${barberShop.latitud}:${barberShop.longitud}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                colors = ButtonDefaults.buttonColors(Color(0, 180, 216))
            ) {
                Text(text = "Ver Ubicación", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para reservar una cita en la peluquería
            Button(
                onClick = {
                    navController.navigate("${Screens.ReservarCita.name}/${barberShop.idPeluqueria}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                colors = ButtonDefaults.buttonColors(Color(3, 4, 94)),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(text = "Reservar Cita", color = Color.White)
            }
        }
    }
}