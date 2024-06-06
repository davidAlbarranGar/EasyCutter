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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.david.easycutter.model.Solicitud
import com.david.easycutter.model.enums.Role
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.model.enums.TypeRequest
import com.david.easycutter.services.BarberShopViewModel
import com.david.easycutter.services.ImageViewModel
import com.david.easycutter.services.RequestViewModel
import com.david.easycutter.services.UserViewModel
import com.david.easycutter.view.elements.AppBar

/**
 * Pantalla que muestra la lista de peticiones.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 */
@Composable
fun PantallaPeticiones(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize().background(color = Color(0xFFCAF0F8)) // Color: 202, 240, 248
    ) {
        // Barra de aplicaciones superior con el título "Peticiones"
        AppBar(title = "Peticiones", navController = navController) {
            // Muestra la lista de peticiones
            ListaPeticiones(navController)
        }
    }
}

/**
 * Componente que muestra la lista de peticiones.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param context Contexto actual de la aplicación.
 */
@Composable
fun ListaPeticiones(navController: NavHostController, context: Context = LocalContext.current) {
    // Instancia del ViewModel para manejar los datos de las peticiones
    val modeloVistaPeticion = RequestViewModel()

    // Lista de peticiones, inicialmente vacía
    var listaPeticiones: List<Solicitud> by remember {
        mutableStateOf(emptyList())
    }

    // Efecto lanzado cuando se carga la composición, obtiene todas las peticiones
    LaunchedEffect(key1 = true) {
        modeloVistaPeticion.getAllRequest("request", context) { peticiones ->
            listaPeticiones = peticiones
        }
    }

    // Muestra la lista de peticiones en un LazyColumn
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(color = Color(0xFFCAF0F8)) // Color: 202, 240, 248
    ) {
        items(listaPeticiones) { peticion ->
            // Muestra el contenido de cada petición
            ContenidoItemPeticion(peticion, context, navController)
        }
    }
}

/**
 * Componente que muestra el contenido de una petición.
 * @param peticion Datos de la petición.
 * @param context Contexto actual de la aplicación.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 */
@Composable
fun ContenidoItemPeticion(
    peticion: Solicitud,
    context: Context,
    navController: NavHostController,
) {
    // Estado para controlar si se deben mostrar más detalles
    var mostrarMas by remember { mutableStateOf(false) }

    // Estado para almacenar la URI de la imagen
    var uriImagen by remember { mutableStateOf<Uri?>(null) }

    // Cargar la imagen de la peluquería
    LaunchedEffect(key1 = peticion.peluqueria.logoUrl!!.toUri()) {
        ImageViewModel().loadLogoImage(
            logoName = peticion.peluqueria.idPeluqueria.toString(),
            onSuccess = { uri ->
                uriImagen = uri
            },
            onFailure = {
                Log.e("Error", "Error al obtener avatar")
            }
        )
    }

    ElevatedCard(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(Color(0xFF90E0EF)) // Color: 144, 224, 239
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally), // Centrar contenido verticalmente
            horizontalAlignment = Alignment.CenterHorizontally // Centrar contenido horizontalmente
        ) {
            // Imagen de la peluquería
            Image(
                painter = rememberImagePainter(uriImagen),
                contentDescription = "Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar detalles adicionales si mostrarMas es verdadero
            if (mostrarMas) {
                Text(
                    text = "Tipo: ${peticion.tipo}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF0077B6), // Color: 0, 119, 182
                    textAlign = TextAlign.Left
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Barbería: ${peticion.peluqueria.nombre}",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color(0xFF03045E), // Color: 3, 4, 94
                    textAlign = TextAlign.Left
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Barbero: ${peticion.peluqueria.peluquero}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF03045E), // Color: 3, 4, 94
                    textAlign = TextAlign.Left
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ID Barbería: ${peticion.peluqueria.idPeluqueria ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF03045E), // Color: 3, 4, 94
                    textAlign = TextAlign.Left
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lista Servicios: \n${peticion.peluqueria.listadoServicios}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF0077B6), // Color: 0, 119, 182
                    textAlign = TextAlign.Left
                )
                Button(
                    onClick = {
                        navController.navigate(
                            Screens.ShowLocation.name + "/${peticion.peluqueria.latitud}:${peticion.peluqueria.longitud}"
                        )
                    },
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF00B4D8)) // Color: 0, 180, 216
                ) {
                    Text(text = "Ver Ubicación", color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            when (peticion.tipo.orEmpty()) {
                                //Acciones para cuando se acepta una solicitud de apertura de peluquería
                                TypeRequest.Save.name -> {
                                    UserViewModel().editRoleUser(
                                        peticion.peluqueria.peluquero,
                                        Role.Barber, context
                                    )
                                    BarberShopViewModel().saveBarberShop(
                                        "barbershop",
                                        peticion.peluqueria,
                                        context
                                    )
                                }
                                //Acciones para cuando se acepta una solicitud de edición de peluquería
                                TypeRequest.Edit.name -> {
                                    BarberShopViewModel().updateBarberShop(peticion.peluqueria, context) {
                                        Toast.makeText(
                                            context,
                                            "Barbería actualizada exitosamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                //Acciones para cuando se acepta una solicitud de cierre de peluquería
                                TypeRequest.Delete.name -> {
                                    BarberShopViewModel().deleteBarberShop(
                                        "barbershop",
                                        peticion.peluqueria,
                                        context
                                    )
                                    ImageViewModel().deleteLogoImage(
                                        peticion.peluqueria.idPeluqueria.toString(),
                                        onSuccess = {
                                            Log.d("Borrado", "Imagen logo borrada correctamente")
                                        },
                                        onFailure = {
                                            Log.d("Borrado", "Error al borrar imagen logo")
                                        }
                                    )
                                    BarberShopViewModel().getBarberShopsByBarber(
                                        "barbershop",
                                        peticion.peluqueria.peluquero,
                                        context
                                    ) { barberShops ->
                                        if (barberShops.isEmpty()) {
                                            UserViewModel().editRoleUser(
                                                peticion.peluqueria.peluquero,
                                                Role.User,
                                                context
                                            )
                                        }
                                    }
                                }
                            }
                            // Acción común a todas las solicitudes
                            RequestViewModel().deleteRequest("request", peticion, context)
                            navController.navigate(Screens.PantallaPeticiones.name)
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF03045E)) // Color: 3, 4, 94
                    ) {
                        Text(text = "Aceptar", color = Color.White)
                    }
                    //Este botón rechaza la solicitud
                    Button(
                        onClick = {
                            RequestViewModel().deleteRequest("request", peticion, context)
                            navController.navigate(Screens.PantallaPeticiones.name)
                        },
                        colors = ButtonDefaults.buttonColors(Color.Red)
                    ) {
                        Text(text = "Rechazar", color = Color.White)
                    }
                }
            } else {
                // Mostrar solo los detalles básicos si mostrarMas es falso
                Text(
                    text = "Tipo: ${peticion.tipo}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF0077B6) // Color: 0, 119, 182
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Barbero: ${peticion.peluqueria.peluquero}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF03045E) // Color: 3, 4, 94
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para mostrar o ocultar más detalles
            Button(
                onClick = { mostrarMas = !mostrarMas },
                colors = ButtonDefaults.buttonColors(Color(0xFF00B4D8)), // Color: 0, 180, 216
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (!mostrarMas) "Mostrar más" else "Mostrar menos",
                    color = Color.White
                )
            }
        }
    }
}