package com.david.easycutter.view.usuario

import android.content.Context
import android.widget.CalendarView
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.david.easycutter.model.Cita
import com.david.easycutter.services.AuthScreenViewModel
import com.david.easycutter.services.DateViewModel
import com.david.easycutter.view.elements.SimpleAppBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Composable para la pantalla de reserva de citas. Permite a los usuarios seleccionar una fecha y hora disponibles
 * para reservar una cita en una peluquería específica.
 *
 * @param idPeluqueria El ID de la peluquería para la cual se realizará la reserva de la cita.
 * @param navControler El controlador de navegación para manejar la navegación entre pantallas.
 */
@Composable
fun ReservarCita(idPeluqueria: String, navControler: NavHostController) {
    SimpleAppBar(title = "Reservar Cita", navController = navControler) {
        val context = LocalContext.current
        val dateViewModel: DateViewModel = viewModel()
        VistaCalendario(idPeluqueria, context, dateViewModel)
    }
}

/**
 * Composable para mostrar un calendario donde los usuarios pueden seleccionar una fecha y hora disponibles para
 * reservar una cita en una peluquería.
 *
 * @param idPeluquero El ID de la peluquería para la cual se buscarán las fechas disponibles.
 * @param context El contexto de la aplicación.
 * @param dateViewModel El ViewModel para manejar las fechas y citas.
 */
@Composable
fun VistaCalendario(idPeluquero: String, context: Context, dateViewModel: DateViewModel) {
    val hoy = Calendar.getInstance()
    val fechaSeleccionada = remember { mutableStateOf(hoy) }

    val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val fechaSeleccionadaTexto = formatoFecha.format(fechaSeleccionada.value.time)

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Vista del calendario
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    minDate = hoy.timeInMillis // Establecer la fecha mínima como el día actual
                    setOnDateChangeListener { _, año, mes, díaDelMes ->
                        val nuevaFechaSeleccionada = Calendar.getInstance().apply {
                            set(Calendar.YEAR, año)
                            set(Calendar.MONTH, mes)
                            set(Calendar.DAY_OF_MONTH, díaDelMes)
                        }
                        if (!nuevaFechaSeleccionada.before(hoy)) {
                            fechaSeleccionada.value = nuevaFechaSeleccionada
                        } else {
                            // Si la fecha seleccionada es anterior a la fecha actual, mantener la fecha actual seleccionada
                            fechaSeleccionada.value = hoy
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar la fecha seleccionada
        Text(
            text = "Horarios: $fechaSeleccionadaTexto",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color(3, 4, 94)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Generar y mostrar los horarios disponibles
        val horarios = generarHorarios()

        LazyColumn {
            items(horarios) { horario ->
                FilaHorario(
                    horario = horario,
                    idPeluqueria = idPeluquero,
                    fechaSeleccionada = fechaSeleccionadaTexto,
                    context = context,
                    dateViewModel = dateViewModel
                )
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = Color.LightGray // Color de divisor estático
                )
            }
        }
    }
}

/**
 * Composable para mostrar una fila de horarios disponibles y permitir a los usuarios reservar citas en esos horarios.
 *
 * @param horario El horario a mostrar en la fila.
 * @param idPeluqueria El ID de la peluquería para la cual se realizará la reserva de la cita.
 * @param fechaSeleccionada La fecha seleccionada para la reserva de la cita.
 * @param context El contexto de la aplicación.
 * @param dateViewModel El ViewModel para manejar las fechas y citas.
 */
@Composable
fun FilaHorario(
    horario: String,
    idPeluqueria: String,
    fechaSeleccionada: String,
    context: Context,
    dateViewModel: DateViewModel
) {
    val fechasReservadas by dateViewModel.reservedDates.collectAsState()

    val claveHorario = "$fechaSeleccionada-$horario"
    val fechaReservada = fechasReservadas[claveHorario + idPeluqueria] ?: false

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(240, 240, 240)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = horario,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(3, 4, 94)
            )
            if (fechaReservada) {
                // Si el horario está reservado, mostrar un botón deshabilitado
                Button(
                    onClick = {
                        Toast.makeText(context, "La cita ya está reservada", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Reservado")
                }
            } else {
                // Si el horario está disponible, mostrar un botón para reservar la cita
                Button(
                    onClick = {
                        val cita = Cita(
                            idPeluqueria = idPeluqueria,
                            fecha = claveHorario,
                            idCliente = AuthScreenViewModel().getCurrentUser()?.email.toString(),
                        )
                        dateViewModel.saveDate("dates", cita, context)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(3, 4, 94), // Color de botón estático
                        contentColor = Color.White
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Reservar")
                }
            }
        }
    }
}

/**
 * Función para generar una lista de horarios disponibles para las citas.
 *
 * @return Lista de horarios generados.
 */
fun generarHorarios(): List<String> {
    val horarios = mutableListOf<String>()
    val calendario = Calendar.getInstance()

    calendario.set(Calendar.HOUR_OF_DAY, 10)
    calendario.set(Calendar.MINUTE, 0)

    while (calendario.get(Calendar.HOUR_OF_DAY) < 20 || (calendario.get(Calendar.HOUR_OF_DAY) == 20 && calendario.get(Calendar.MINUTE) == 0)) {
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minuto = calendario.get(Calendar.MINUTE)

        // Excluir el intervalo de 2:00 p.m. a 3:00 p.m.
        if (hora == 14) {
            calendario.add(Calendar.HOUR_OF_DAY, 1)
            continue
        }

        val horaFormateada = String.format("%02d:%02d", hora, minuto)
        horarios.add(horaFormateada)

        calendario.add(Calendar.MINUTE, 30)
    }

    return horarios
}