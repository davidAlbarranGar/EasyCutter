package com.david.easycutter.view.admin

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.david.easycutter.R
import com.david.easycutter.model.Peluqueria
import com.david.easycutter.model.enums.Role
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.model.enums.TypeRequest
import com.david.easycutter.services.BarberShopViewModel
import com.david.easycutter.services.ImageViewModel
import com.david.easycutter.services.RequestViewModel
import com.david.easycutter.services.UserViewModel
import com.david.easycutter.view.elements.AppBar

/**
 * Pantalla que muestra la lista de peluquerías dentro de una barra superior.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 */
@Composable
fun PantallaListaPeluquerias(navController: NavHostController) {
    // Crea una barra de aplicaciones superior con el título "Lista Peluquerías"
    AppBar(title = "Lista Peluquerías", navController = navController) {
        // Muestra la lista de peluquerías
        ListaPeluquerias(navController)
    }
}

/**
 * Componente que muestra la lista de peluquerías.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param contexto Contexto actual de la aplicación.
 */
@Composable
fun ListaPeluquerias(navController: NavHostController, contexto: Context = LocalContext.current) {
    // Instancia del ViewModel para manejar los datos de las peluquerías
    val modeloVistaPeluqueria = BarberShopViewModel()

    Surface(
        modifier = Modifier.fillMaxSize().background(color = Color(0xFFCAF0F8)) // Color: 202, 240, 248
    ) {
        // Lista de peluquerías, inicialmente vacía
        var listaPeluquerias: List<Peluqueria> by remember {
            mutableStateOf(emptyList())
        }

        // Efecto lanzado cuando se carga la composición, donde se obtiene todas las peluquerías
        LaunchedEffect(key1 = true) {
            modeloVistaPeluqueria.getAllBarberShop("barbershop", contexto) { peluquerias ->
                listaPeluquerias = peluquerias
            }
        }

        // Verifica si hay peluquerías disponibles
        if (listaPeluquerias.isNotEmpty()) {
            // Muestra la lista de peluquerías en un LazyColumn
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().background(color = Color(0xFFCAF0F8)) // Color: 202, 240, 248
            ) {
                items(listaPeluquerias) { peluqueria ->
                    // Muestra el contenido de cada peluquería
                    contenidoItemPeluqueria(peluqueria, contexto, modeloVistaPeluqueria, navController)
                }
            }
        } else {
            // Muestra un mensaje si no hay peluquerías disponibles
            Text(
                text = "No hay peluquerías disponibles",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Componente que muestra el contenido de cada de peluquería.
 * @param peluqueria Datos de la peluquería.
 * @param contexto Contexto actual de la aplicación.
 * @param modeloVistaPeluqueria ViewModel para manejar los datos de las peluquerías.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 */
@Composable
fun contenidoItemPeluqueria(peluqueria: Peluqueria, contexto: Context, modeloVistaPeluqueria: BarberShopViewModel, navController: NavHostController) {
    // Tarjeta elevada para mostrar los datos de la peluquería
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color(0xFF90E0EF)), // Color: 144, 224, 239
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Estado para controlar si se muestran más detalles
            var mostrarMas by remember { mutableStateOf(false) }

            // Estado para almacenar la URI de la imagen
            var imageUri: Uri? by remember {
                mutableStateOf(null)
            }
            // Efecto lanzado para cargar la imagen del logo de la peluquería
            LaunchedEffect(key1 = peluqueria.logoUrl!!.toUri()) {
                ImageViewModel().loadLogoImage(logoName = peluqueria.idPeluqueria.toString(), onSuccess = { uri ->
                    // Cargar la imagen utilizando Coil
                    imageUri = uri
                }, onFailure = {
                    Log.e("Error","Error al obtener avatar")
                })
            }
            // Imagen del logo de la peluquería
            Image(
                painter = rememberImagePainter(imageUri),
                contentDescription = "Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Muestra más detalles si 'mostrarMas' es verdadero
            if (mostrarMas){
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Barbería: ${peluqueria.nombre}",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color(0xFF03045E) // Color: 3, 4, 94
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Barbero: ${peluqueria.peluquero}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF03045E) // Color: 3, 4, 94
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ID Barbería: ${peluqueria.idPeluqueria ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF03045E) // Color: 3, 4, 94
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Servicios: \n${peluqueria.listadoServicios}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF0077B6) // Color: 0, 119, 182
                )
                // Botón para ver la ubicación de la peluquería
                Button(onClick = {
                    navController.navigate(
                        Screens.ShowLocation.name+"/${peluqueria.latitud}:${peluqueria.longitud}"
                    )
                }) {
                    Text(text = "Ver Ubicación")
                }

            } else {
                // Muestra los detalles básicos si 'mostrarMas' es falso
                Text(
                    text = peluqueria.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF03045E) // Color: 3, 4, 94
                )
                Text(
                    text = peluqueria.peluquero,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF03045E) // Color: 3, 4, 94
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Fila con botones para mostrar más detalles o borrar la peluquería
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        mostrarMas = !mostrarMas
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF00B4D8)), // Color: 0, 180, 216
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Detalles",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Detalles", color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { modeloVistaPeluqueria.deleteBarberShop("barbershop", peluqueria, contexto) },
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Borrar",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Borrar", color = Color.White)
                }
            }
        }
    }
}