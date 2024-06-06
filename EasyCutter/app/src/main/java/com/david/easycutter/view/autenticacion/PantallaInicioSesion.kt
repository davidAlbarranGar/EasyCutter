package com.david.easycutter.view.autenticacion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.david.easycutter.R
import com.david.easycutter.model.enums.Role
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.services.AuthScreenViewModel
import com.david.easycutter.services.UserViewModel
import com.david.easycutter.view.elements.NormalInput
import com.david.easycutter.view.elements.PasswordInput


/**
 * Composable para la pantalla de inicio de sesión.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param viewModel ViewModel para manejar la lógica de autenticación.
 */
@Composable
fun PantallaInicioSesion(
    navController: NavController,
    viewModel: AuthScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(color = Color(0xFFCAF0F8)) // Color: 202, 240, 248
                .fillMaxSize()
        ) {
            // Logo de la aplicación
            Image(
                painter = painterResource(id = R.drawable.logo_easy_cutter),
                contentDescription = "Aplicación Logo"
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Iniciar Sesión",
                color = Color(0xFF03045E), // Color: 3, 4, 94
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // Formulario para iniciar sesión
            FormularioInicioSesion { email, password ->
                viewModel.signInWithEmailAndPassword(email, password, context) {
                    // Obtiene el usuario actual después de iniciar sesión
                    UserViewModel().getCurrentUser("users", context) { user ->
                        user?.let {
                            // Navega a la pantalla correspondiente según el rol del usuario
                            if (it.rol == Role.Admin) {
                                navController.navigate(Screens.PantallaListaUsuarios.name)
                            } else {
                                navController.navigate(Screens.PantallaListaReservarCita.name)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            // Enlace para registrarse
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "¿No tienes cuenta?", color = Color(0xFF03045E)) // Color: 3, 4, 94
                Text(
                    text = "Regístrate",
                    modifier = Modifier
                        .clickable { navController.navigate(Screens.PantallaRegistro.name) }
                        .padding(start = 5.dp),
                    color = Color(0xFF0077B6) // Color: 0, 119, 182
                )
            }
        }
    }
}

/**
 * Formulario para el inicio de sesión.
 * @param onDone Función que se ejecuta al completar el formulario, recibe el email y la contraseña.
 */
@Composable
fun FormularioInicioSesion(
    onDone: (String, String) -> Unit = { email, pwd -> }
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val valido = comprobarValidez(email.value, password.value)
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Campo de entrada para el email
        NormalInput(
            variableState = email,
            keyBoardType = KeyboardType.Email,
            labelId = "Correo Electrónico",
        )
        // Campo de entrada para la contraseña
        PasswordInput(
            passwordState = password,
            labelId = "Contraseña",
            passwordVisible = passwordVisible,
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Botón para iniciar sesión
        Button(
            onClick = {
                onDone(email.value.trim(), password.value.trim())
                keyboardController?.hide()
            },
            modifier = Modifier
                .padding(3.dp)
                .fillMaxWidth(),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(Color(0xFF0077B6)), // Color: 0, 119, 182
            enabled = valido
        ) {
            Text(
                text = "Iniciar Sesión",
                modifier = Modifier.padding(5.dp),
                color = Color.White
            )
        }
    }
}

/**
 * Función que comprueba la validez del email y la contraseña.
 * @param email El email introducido.
 * @param password La contraseña introducida.
 * @return true si ambos son válidos, false en caso contrario.
 */
private fun comprobarValidez(email: String, password: String): Boolean {
    var valido = true
    if (email.isEmpty() || password.isEmpty()) {
        valido = false
    }
    if (password.length < 6) {
        valido = false
    }

    return valido
}