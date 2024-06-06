package com.david.easycutter.view.cuenta

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.view.elements.SimpleAppBar

/**
 * Composable que representa la pantalla de "Contáctanos".
 * Contiene secciones para crear una peluquería y solicitar el bloqueo de una cuenta.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas.
 */
@Composable
fun Contactanos(navController: NavController) {
    // Barra de aplicación simple con título y controlador de navegación
    SimpleAppBar(title = "Contáctanos", navController = navController) {
        MaterialTheme {
            // LazyColumn para disposición vertical de los elementos
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(202, 240, 248)), // Fondo color: #CAF0F8
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Elementos de la lista que contienen las secciones de contacto
                items(listOf(
                    "Crea tu propia peluquería",
                    "Solicita el bloqueo de una cuenta"
                )) { item ->
                    when (item) {
                        "Crea tu propia peluquería" -> CreateSalonSection(navController)
                        "Solicita el bloqueo de una cuenta" -> RequestAccountBlockSection(navController,
                            LocalContext.current)
                    }
                }
            }
        }
    }
}

/**
 * Composable que representa la sección "Crea tu propia peluquería".
 * Proporciona información y un botón para navegar a la pantalla de solicitud de barbería.
 *
 * @param navController Controlador de navegación para manejar la navegación a la pantalla de solicitud de barbería.
 */
@Composable
fun CreateSalonSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(color = Color(0xFFFFFFFF)) // Fondo blanco
            .clip(RoundedCornerShape(20.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crea tu propia peluquería",
            fontSize = 24.sp,
            color = Color(3, 4, 94), // Color: #03045E
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Envíanos una solicitud de apertura y comienza tu propio negocio de peluquería con nuestras herramientas fáciles de usar.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate(Screens.SolicitarBarberia.name) },
            colors = ButtonDefaults.buttonColors(Color(0, 119, 182)), // Color: #0077B6
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Crear peluquería", color = Color.White)
        }
    }
}

/**
 * Composable que representa la sección "Solicita el bloqueo de una cuenta".
 * Proporciona información y un botón para solicitar el bloqueo de una cuenta.
 *
 * @param navController Controlador de navegación.
 * @param context Contexto actual de la aplicación
 */
@Composable
fun RequestAccountBlockSection(navController: NavController, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(color = Color(0xFFFFFFFF)) // Fondo blanco
            .clip(RoundedCornerShape(20.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Solicita el bloqueo de una cuenta",
            fontSize = 24.sp,
            color = Color(3, 4, 94), // Color: #03045E
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Si necesitas bloquear una cuenta, puedes solicitarlo aquí.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { Toast.makeText(context, "Futura implementación", Toast.LENGTH_SHORT).show() },
            colors = ButtonDefaults.buttonColors(Color(0, 119, 182)), // Color: #0077B6
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Solicitar bloqueo", color = Color.White)
        }
    }
}