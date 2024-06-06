package com.david.easycutter.view.peluquero

import android.content.Context
import android.net.Uri
import android.util.Log
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.david.easycutter.model.Peluqueria
import com.david.easycutter.model.Solicitud
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.model.enums.TypeRequest
import com.david.easycutter.services.AuthScreenViewModel
import com.david.easycutter.services.BarberShopViewModel
import com.david.easycutter.services.ImageViewModel
import com.david.easycutter.services.RequestViewModel
import com.david.easycutter.view.elements.AppBar

/**
 * Composable que muestra la lista de peluquerías asociadas a un barbero.
 *
 * @param navController El controlador de navegación para manejar la navegación entre pantallas.
 */
@Composable
fun PantallaListaBarberiasBarbero(navController: NavHostController) {
    val contexto = LocalContext.current
    val barbero = AuthScreenViewModel().getCurrentUser()?.email
    val modeloVistaPeluqueria = BarberShopViewModel()

    AppBar(title = "Mis Peluquerías", navController = navController) {
        ListaBarberiasBarbero(
            navController = navController,
            contexto = contexto,
            barbero = barbero,
            modeloVistaPeluqueria = modeloVistaPeluqueria
        )
    }
}

/**
 * Composable que muestra una lista de peluquerías asociadas a un barbero.
 *
 * @param navController El controlador de navegación para manejar la navegación entre pantallas.
 * @param contexto El contexto de la aplicación.
 * @param barbero El correo electrónico del barbero actual.
 * @param modeloVistaPeluqueria El ViewModel para manejar las operaciones relacionadas con las peluquerías.
 */
@Composable
fun ListaBarberiasBarbero(
    navController: NavHostController,
    contexto: Context,
    barbero: String?,
    modeloVistaPeluqueria: BarberShopViewModel
) {
    var listaPeluquerias by remember { mutableStateOf(emptyList<Peluqueria>()) }

    // LaunchedEffect para cargar la lista de peluquerías cuando la pantalla se muestra por primera vez
    LaunchedEffect(key1 = true) {
        modeloVistaPeluqueria.getBarberShopsByBarber("barbershop", barbero, contexto) { peluquerias ->
            listaPeluquerias = peluquerias
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(202, 240, 248))
    ) {
        items(listaPeluquerias) { peluqueria ->
            TarjetaPeluqueria(peluqueria = peluqueria, navController = navController)
        }
    }
}

/**
 * Composable que muestra la información de una peluquería en una tarjeta.
 *
 * @param peluqueria La peluquería que se va a mostrar.
 * @param navController El controlador de navegación para manejar la navegación entre pantallas.
 */
@Composable
fun TarjetaPeluqueria(peluqueria: Peluqueria, navController: NavHostController) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    val contexto = LocalContext.current
    val urlLogo = remember { mutableStateOf<Uri?>(null) }

    // LaunchedEffect para cargar el logo de la peluquería
    LaunchedEffect(peluqueria.idPeluqueria) {
        ImageViewModel().loadLogoImage(
            peluqueria.idPeluqueria.toString(),
            onSuccess = { uriLogo ->
                urlLogo.value = uriLogo
            },
            onFailure = { excepcion ->
                Log.d("Logo", "Error al obtener logo: ${excepcion.message}")
            }
        )
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Muestra el logo de la peluquería
            Row {
                Column {
                    Row {
                        urlLogo.value?.let { uri ->
                            Image(
                                painter = rememberImagePainter(uri),
                                contentDescription = "Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(5.dp))
                Column (Modifier.padding(top = 10.dp)){
                    // Muestra el nombre y el peluquero de la peluquería
                    Text(
                        text = peluqueria.nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Peluquero: " + peluqueria.peluquero,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones para ver citas, editar y eliminar la peluquería
            Row(
                Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    navController.navigate(Screens.PantallaCitasBarbero.name + "/${peluqueria.idPeluqueria}")
                }, Modifier.padding(2.dp)) {
                    Text("Ver Citas")
                }
                Button(onClick = {
                    navController.navigate(Screens.PantallaEditarPeluqueria.name + "/${peluqueria.idPeluqueria}")
                }, Modifier.padding(2.dp)) {
                    Text("Editar")
                }
                Button(onClick = {
                    mostrarDialogoEliminar = true
                }, Modifier.padding(2.dp)) {
                    Text("Eliminar")
                }
            }
        }
    }

    // Dialogo de confirmación para eliminar la peluquería
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Confirmar Eliminación") },
            text = {
                Text("¿Seguro que quieres enviar petición para eliminar la peluquería ${peluqueria.nombre}? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoEliminar = false
                        val solicitud = Solicitud(
                            tipo = TypeRequest.Delete.name,
                            peluqueria = peluqueria
                        )
                        RequestViewModel().sendRequest("request", solicitud, contexto)
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}