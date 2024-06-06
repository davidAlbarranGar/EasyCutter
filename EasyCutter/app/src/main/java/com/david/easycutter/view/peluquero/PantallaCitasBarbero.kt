package com.david.easycutter.view.peluquero

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.david.easycutter.model.Cita
import com.david.easycutter.services.DateViewModel
import com.david.easycutter.view.elements.SimpleAppBar

/**
 * Composable para mostrar la pantalla de citas de un barbero.
 *
 * @param barberoId el ID del barbero cuyas citas se mostrarán.
 * @param navController el controlador de navegación para manejar las acciones de navegación.
 */
@Composable
fun PantallaCitasBarbero(barberoId: String, navController: NavController) {
    // Composable de la barra de aplicación simple con título y opciones de navegación
    SimpleAppBar(title = "Horario de peluquería", navController = navController) {
        // Composable que muestra las citas del barbero
        CitasBarbero(barberoId, navController)
    }
}

/**
 * Composable para mostrar las citas de un barbero.
 *
 * @param barberoId el ID del barbero cuyas citas se mostrarán.
 * @param navController el controlador de navegación para manejar las acciones de navegación.
 * @param citaViewModel el ViewModel que contiene la lógica de negocio relacionada con las citas.
 */
@Composable
fun CitasBarbero(
    barberoId: String,
    navController: NavController,
    citaViewModel: DateViewModel = viewModel()
) {
    // Estado que almacena la lista de citas del barbero
    val citas by citaViewModel.appointments.collectAsState()

    // Contexto actual
    val context = LocalContext.current

    // Se lanza un efecto cuando cambia el ID del barbero para obtener las citas correspondientes
    LaunchedEffect(barberoId) {
        citaViewModel.fetchAppointments(barberoId, context)
    }

    // Columna que contiene las citas del barbero
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(202, 240, 248))
    ) {
        Text(
            text = "Citas Programadas",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Comprobación de si hay citas disponibles para mostrar
        if (citas.isEmpty()) {
            Text(
                text = "No hay citas programadas.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            // Lista de citas del barbero
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(citas) { cita ->
                    // Composable para mostrar una tarjeta de cita
                    TarjetaCita(
                        cita = cita,
                        onCancelarClic = {
                            citaViewModel.cancelDate(cita.fecha.toString() + cita.idPeluqueria, context)
                        },
                        onRechazarClic = {
                            citaViewModel.deleteDate(cita.fecha.toString() + cita.idPeluqueria, context)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Composable para mostrar una tarjeta de cita.
 *
 * @param cita la cita que se mostrará en la tarjeta.
 * @param onCancelarClic la acción que se realizará al hacer clic en el botón de cancelar.
 * @param onRechazarClic la acción que se realizará al hacer clic en el botón de rechazar.
 */
@Composable
fun TarjetaCita(
    cita: Cita,
    onCancelarClic: () -> Unit,
    onRechazarClic: () -> Unit
) {
    // Tarjeta elevada que contiene la información de la cita y los botones de acción
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Información de la cita
            Text(
                text = "Fecha: ${cita.fecha}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF424242),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Cliente: ${cita.idCliente}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF424242),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Botones de acción para cancelar o rechazar la cita
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onRechazarClic,
                    enabled = cita.estado == "Aceptada",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFF44336))
                ) {
                    Text(text = "Rechazar", color = Color.White)
                }
                Button(
                    onClick = onCancelarClic,
                    enabled = cita.estado == "Aceptada",
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(if (cita.estado == "Aceptada") Color(0xFF1976D2) else Color(0xFFB0BEC5))
                ) {
                    Text(text = if (cita.estado == "Aceptada") "Cancelar" else "Cita Cancelada", color = Color.White)
                }
            }
        }
    }
}