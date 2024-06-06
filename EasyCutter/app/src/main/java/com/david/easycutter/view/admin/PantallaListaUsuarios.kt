package com.david.easycutter.view.admin

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
import com.david.easycutter.model.Usuario
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.services.ImageViewModel
import com.david.easycutter.services.UserViewModel
import com.david.easycutter.view.elements.AppBar


/**
 * Pantalla que muestra la lista de usuarios.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 */
@Composable
fun PantallaListaUsuarios(navController: NavHostController) {
    // Crea una barra de aplicaciones superior con el título "Lista de Usuarios"
    AppBar(title = "Lista de Usuarios", navController = navController) {
        // Muestra la lista de usuarios
        ListaUsuarios(navController = navController)
    }
}

/**
 * Componente que muestra la lista de usuarios.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param contexto Contexto actual de la aplicación.
 */
@Composable
fun ListaUsuarios(navController: NavHostController, contexto: Context = LocalContext.current) {
    // Instancia del ViewModel para manejar los datos de los usuarios
    val modeloVistaCompartida = UserViewModel()
    // Lista de usuarios, inicialmente vacía
    var listaUsuarios by remember { mutableStateOf(emptyList<Usuario>()) }

    // Efecto lanzado cuando se carga la composición, donde se obtiene todos los usuarios
    LaunchedEffect(key1 = true) {
        modeloVistaCompartida.getAllUser("users", contexto) { usuarios ->
            listaUsuarios = usuarios
        }
    }

    // Muestra la lista de usuarios en un LazyColumn
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(color = Color(0xFFCAF0F8)) // Color: 202, 240, 248
    ) {
        items(listaUsuarios) { usuario ->
            // Muestra el contenido de cada usuario
            ElementoUsuario(usuario = usuario, onBloquearUsuario = {
                // Bloquea al usuario y navega de vuelta a la pantalla de lista de usuarios
                modeloVistaCompartida.blockUser("users", usuario, contexto)
                navController.navigate(Screens.PantallaListaUsuarios.name)
            })
        }
    }
}

/**
 * Componente que muestra el contenido de un ítem de usuario.
 * @param usuario Datos del usuario.
 * @param onBloquearUsuario Acción a realizar cuando se bloquee al usuario.
 */
@Composable
fun ElementoUsuario(usuario: Usuario, onBloquearUsuario: () -> Unit) {
    // Tarjeta elevada para mostrar los datos del usuario
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(Color(0xFF90E0EF)), // Color: 144, 224, 239
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Fila para organizar el avatar y los datos del usuario
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar del usuario
                var uriImagen: Uri? by remember { mutableStateOf(null) }

                // Efecto lanzado para cargar la imagen del avatar del usuario
                LaunchedEffect(key1 = usuario.email) {
                    ImageViewModel().loadAvatarImage(
                        LogoName = usuario.email,
                        onSuccess = { uri -> uriImagen = uri },
                        onFailure = { Log.e("Error", "Error al obtener avatar") }
                    )
                }

                // Caja que contiene la imagen del avatar con un tamaño y forma específicos
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(128.dp) // Tamaño más grande
                        .clip(CircleShape) // Forma redonda
                        .border(2.dp, Color.Gray, CircleShape) // Borde opcional
                ) {
                    Image(
                        painter = rememberImagePainter(uriImagen),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape)
                            .background(Color.Gray) // Color de fondo del círculo
                            .border(2.dp, Color.Gray, CircleShape) // Borde opcional
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${usuario.nombre} ${usuario.apellidos}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF03045E) // Color: 3, 4, 94
                    )
                    Text(
                        text = usuario.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF03045E) // Color: 3, 4, 94
                    )
                    Text(
                        text = "Rol: ${usuario.rol.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para bloquear al usuario
            Button(
                onClick = onBloquearUsuario,
                colors = ButtonDefaults.buttonColors(Color.Red),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Bloquear Usuario",
                    color = Color.White
                )
            }
        }
    }
}