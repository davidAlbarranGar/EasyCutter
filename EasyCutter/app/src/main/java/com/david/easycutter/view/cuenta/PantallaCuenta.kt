package com.david.easycutter.view.cuenta

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.david.easycutter.model.Usuario
import com.david.easycutter.services.ImageViewModel
import com.david.easycutter.services.UserViewModel
import com.david.easycutter.view.elements.SimpleAppBar

/**
 * Composable que representa la pantalla de "Cuenta".
 * Obtiene los datos del usuario actual y los muestra en la pantalla.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas.
 */
@Composable
fun PantallaCuenta(navController: NavController) {
    var usuario by remember {
        mutableStateOf(Usuario())
    }

    // Obtener el usuario actual desde el ViewModel y actualizar el estado del usuario
    UserViewModel().getCurrentUser("users", LocalContext.current) {
        if (it != null) {
            usuario = it
        }
    }

    // Barra de aplicación simple con título y controlador de navegación
    SimpleAppBar(title = "Cuenta", navController = navController) {
        // Composable que muestra los datos del usuario
        Cuenta(
            navController = navController,
            usuario = usuario,
            onDelete = { /*TODO*/ },
            onEdit = { /*TODO*/ })
    }
}

/**
 * Composable que muestra la información de la cuenta del usuario.
 * Incluye el avatar, nombre, email, rol y botones para editar y eliminar la cuenta.
 *
 * @param navController Controlador de navegación.
 * @param usuario Objeto Usuario que contiene la información del usuario.
 * @param onDelete Lambda que se ejecuta cuando se presiona el botón de eliminar.
 * @param onEdit Lambda que se ejecuta cuando se presiona el botón de editar.
 */
@Composable
fun Cuenta(navController: NavController, usuario: Usuario, onDelete: () -> Unit, onEdit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(202, 240, 248)), // Color de fondo azul claro
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Avatar del usuario
        var imageUri: Uri? by remember {
            mutableStateOf(null)
        }

        // Cargar la imagen del avatar desde el ViewModel
        LaunchedEffect(key1 = usuario.email) {
            ImageViewModel().loadAvatarImage(LogoName = usuario.email, onSuccess = { uri ->
                // Cargar la imagen utilizando Coil
                imageUri = uri
            }, onFailure = {
                Log.e("Error", "Error al obtener avatar")
            })
        }

        var expanded by remember { mutableStateOf(false) }

        val clickModifier = Modifier.clickable { expanded = true }

        if (expanded) {
            // Si está expandida, muestra la imagen en una nueva pantalla
            Image(
                painter = rememberImagePainter(imageUri),
                contentDescription = "Avatar",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { expanded = false }
            )
        } else {
            // Si no está expandida, muestra la miniatura
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
                    .then(clickModifier) // Aplica el modificador clickable
            ) {
                Image(
                    painter = rememberImagePainter(imageUri),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .border(2.dp, Color.Gray, CircleShape),
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${usuario.nombre} ${usuario.apellidos}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(3, 4, 94)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = usuario.email,
            fontSize = 18.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Rol: ${usuario.rol.name}",
            fontSize = 18.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onEdit() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color(0, 119, 182))
        ) {
            Text("Editar", color = Color.White) // Color blanco
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onDelete() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(Color.Red)
        ) {
            Text("Eliminar", color = Color.White)
        }
    }
}