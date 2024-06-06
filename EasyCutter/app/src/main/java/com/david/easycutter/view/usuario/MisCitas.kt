package com.david.easycutter.view.usuario

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.david.easycutter.model.Cita
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.services.AuthScreenViewModel
import com.david.easycutter.services.DateViewModel
import com.david.easycutter.view.elements.AppBar

/**
 * Composable que muestra la lista de citas del usuario.
 *
 * @param navControler El controlador de navegación para manejar la navegación entre pantallas.
 */
@Composable
fun MisCitas(navControler: NavHostController) {
    AppBar("Mis Citas", navControler) {
        PantallaCitasUsuario(navControler, LocalContext.current)
    }
}

/**
 * Composable que muestra la lista de citas del usuario.
 *
 * @param navController El controlador de navegación para manejar la navegación entre pantallas.
 * @param context El contexto de la aplicación.
 * @param dateViewModel El ViewModel para manejar las operaciones relacionadas con las citas.
 */
@Composable
fun PantallaCitasUsuario(navController: NavHostController, context: Context, dateViewModel: DateViewModel = viewModel()) {
    val citas by dateViewModel.appointments.collectAsState()

    // Se inicia la recuperación de las citas del usuario
    LaunchedEffect(Unit) {
        dateViewModel.fetchUserAppointments(AuthScreenViewModel().getCurrentUser()?.email.orEmpty(), context)
    }

    LazyColumn(Modifier.fillMaxSize()
        .background(color = Color(202, 240, 248))) {
        items(citas) { cita ->
            TarjetaCita(
                navController = navController,
                cita = cita,
                onCancelarClickeado = {
                    dateViewModel.deleteDate(cita.fecha.toString() + cita.idPeluqueria, context)
                }
            )
        }
    }
}

/**
 * Composable que muestra la información de una cita en una tarjeta.
 *
 * @param cita La cita que se va a mostrar.
 * @param onCancelarClickeado La acción a realizar cuando se hace clic en el botón de cancelar cita.
 * @param navController El controlador de navegación para manejar la navegación entre pantallas.
 */
@Composable
fun TarjetaCita(
    cita: Cita,
    onCancelarClickeado: () -> Unit,
    navController: NavHostController
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(202, 240, 248) // Color de contenedor estático
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Muestra la fecha y el ID de la peluquería de la cita
                    Text(
                        text = "Fecha: ${cita.fecha}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(3, 4, 94)
                    )
                    Text(
                        text = "Peluquería: ${cita.idPeluqueria}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(3, 4, 94)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Botón para mostrar la ubicación de la cita
                    Button(
                        onClick = {
                            navController.navigate(Screens.ShowLocation.name + "/${cita.latitud}:${cita.longitud}")
                        },
                        colors = ButtonDefaults.buttonColors(
                            Color(0, 119, 182) // Color de botón estático
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Icono de Ubicación")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mostrar Ubicación",
                            color = Color.White // Color de texto estático
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Botón para cancelar la cita
            Button(
                onClick = onCancelarClickeado,
                enabled = cita.estado == "Aceptada",
                colors = ButtonDefaults.buttonColors(
                    if (cita.estado == "Aceptada") Color(0, 180, 216) else Color(144, 224, 239), // Color de fondo del botón estático
                    contentColor = Color.White
                ),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = if (cita.estado == "Aceptada") "Cancelar Cita" else "Cita Cancelada")
            }
        }
    }
}