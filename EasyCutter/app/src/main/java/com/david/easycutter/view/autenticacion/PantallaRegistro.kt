package com.david.easycutter.view.autenticacion

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.david.easycutter.model.enums.Role
import com.david.easycutter.model.Usuario
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.services.AuthScreenViewModel
import com.david.easycutter.view.elements.NormalInput
import com.david.easycutter.view.elements.PasswordInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.random.Random

/**
 * Composable para la pantalla de registro de usuario.
 * Esta pantalla consta de tres pasos:
 * 1. Recopilación de información del usuario y contraseña.
 * 2. Verificación de correo electrónico mediante un código enviado por email.
 * 3. Registro del usuario en la base de datos.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param viewModel ViewModel para manejar la lógica de autenticación.
 */
@Composable
fun PantallaRegistro(navController: NavHostController, viewModel: AuthScreenViewModel = AuthScreenViewModel()) {
    val context = LocalContext.current
    var pasoActual by rememberSaveable { mutableStateOf(1) } // Estado para controlar el paso actual del proceso de registro
    val usuario = remember { mutableStateOf(Usuario()) } // Estado para almacenar los datos del usuario
    val contrasena = rememberSaveable { mutableStateOf("") } // Estado para almacenar la contraseña
    val codigoVerificacion = rememberSaveable { mutableStateOf("") } // Estado para almacenar el código de verificación
    val numeroAleatorio = rememberSaveable { mutableStateOf(generarNumAleatorio()) } // Generar un número aleatorio para el código de verificación

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFCAF0F8)) // Color: 202, 240, 248
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFFCAF0F8)) // Color: 202, 240, 248
        ) {
            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF03045E) // Color: 3, 4, 94
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lógica de pasos del proceso de registro
            when (pasoActual) {
                1 -> PasoUno(usuario, contrasena, navController) {
                    // Al finalizar el paso uno, se avanza al paso dos y se envía un correo electrónico con el código de verificación
                    pasoActual = 2
                    sendEmail(code = numeroAleatorio.value.toString(), to = usuario.value.email, context = context) {
                        Log.d("Email", "Correo enviado Correctamente")
                    }
                }
                2 -> PasoDos(codigoVerificacion) { codigo ->
                    // Verifica si el código ingresado es correcto
                    if (codigo == numeroAleatorio.value.toString()) {
                        pasoActual = 3
                    } else {
                        // Si el código no es correcto, se genera un nuevo número aleatorio y se reinicia el registro
                        numeroAleatorio.value = generarNumAleatorio()
                        navController.navigate(Screens.PantallaRegistro.name)
                    }
                }
                3 -> PasoTres(usuario, onRegistrar = {
                    // Al finalizar el paso tres, se registra el usuario en la base de datos
                    AuthScreenViewModel().createUserWithEmailAndPassword(usuario.value, contrasena.value, context) {
                        navController.navigate(Screens.PantallaListaReservarCita.name)
                    }
                }, onCancelar = {
                    // Opción para cancelar y volver a la pantalla de inicio de sesión
                    navController.navigate(Screens.PantallaInicioSesion.name)
                })
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Enlace para ir a la pantalla de inicio de sesión
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "¿Ya tienes una cuenta?", color = Color(0xFF03045E)) // Color: 3, 4, 94
                Text(
                    text = "Inicia sesión",
                    modifier = Modifier
                        .clickable { navController.navigate(Screens.PantallaInicioSesion.name) }
                        .padding(start = 5.dp),
                    color = Color(0xFF0077B6) // Color: 0, 119, 182
                )
            }
        }
    }
}

/**
 * Función para generar un número aleatorio de 5 dígitos.
 * @return Número aleatorio de 5 dígitos.
 */
fun generarNumAleatorio(): Int {
    return Random.nextInt(10000, 100000)
}

/**
 * Composable que representa el primer paso del registro de usuario.
 * En este paso, el usuario debe proporcionar su información personal, incluyendo
 * correo electrónico, contraseña, nombre, apellido y avatar.
 *
 * @param usuario MutableState que contiene la información del usuario.
 * @param contrasena MutableState que contiene la contraseña del usuario.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param onSiguiente Función lambda que se ejecuta al hacer clic en el botón "Siguiente".
 */
@Composable
fun PasoUno(
    usuario: MutableState<Usuario>,
    contrasena: MutableState<String>,
    navController: NavHostController,
    onSiguiente: () -> Unit
) {
    val email = rememberSaveable { mutableStateOf("") }
    val pwd = rememberSaveable { mutableStateOf("") }
    val repetirContrasena = rememberSaveable { mutableStateOf("") }
    val contrasenaVisible = rememberSaveable { mutableStateOf(false) }
    val nombre = rememberSaveable { mutableStateOf("") }
    val apellido = rememberSaveable { mutableStateOf("") }
    val avatarUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            avatarUri.value = uri
        }
    }

    val emailError = rememberSaveable { mutableStateOf<String?>(null) }
    val contrasenaError = rememberSaveable { mutableStateOf<String?>(null) }
    val repetirContrasenaError = rememberSaveable { mutableStateOf<String?>(null) }
    val nombreError = rememberSaveable { mutableStateOf<String?>(null) }
    val apellidoError = rememberSaveable { mutableStateOf<String?>(null) }
    val avatarError = rememberSaveable { mutableStateOf<String?>(null) }

    val esValido = validarFormulario(
        email.value, pwd.value, repetirContrasena.value,
        emailError, contrasenaError, repetirContrasenaError,
        nombre.value, nombreError, apellido.value, apellidoError,
        avatarUri.value, avatarError
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Paso 1: Información Personal",
                style = MaterialTheme.typography.displaySmall,
                color = Color(0xFF03045E) // Color: 3, 4, 94
            )
        }
        item {
            Text(
                text = "Por favor, complete la siguiente información para crear su cuenta.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        item {
            NormalInput(
                variableState = email,
                labelId = "Correo Electrónico",
                keyBoardType = KeyboardType.Email,
            )
            emailError.value?.let { Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall) }
        }
        item {
            PasswordInput(
                passwordState = pwd,
                labelId = "Contraseña",
                passwordVisible = contrasenaVisible,
            )
            contrasenaError.value?.let { Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall) }
        }
        item {
            PasswordInput(
                passwordState = repetirContrasena,
                labelId = "Repetir Contraseña",
                passwordVisible = contrasenaVisible,
            )
            repetirContrasenaError.value?.let { Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall) }
        }
        item {
            NormalInput(
                variableState = nombre,
                labelId = "Nombre",
                keyBoardType = KeyboardType.Text,
            )
            nombreError.value?.let { Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall) }
        }
        item {
            NormalInput(
                variableState = apellido,
                labelId = "Apellido",
                keyBoardType = KeyboardType.Text,
            )
            apellidoError.value?.let { Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall) }
        }
        item {
            // Botón para seleccionar avatar
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0, 180, 216))
            ) {
                Text("Seleccionar Avatar", color = Color.White)
            }
            // Muestra la imagen seleccionada, si hay alguna
            avatarUri.value?.let { uri ->
                Image(
                    painter = rememberImagePainter(uri),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp) // Tamaño del avatar
                        .clip(CircleShape)
                        .background(Color.Gray) // Color de fondo del círculo
                        .border(2.dp, Color.Gray, CircleShape) // Borde opcional
                )
            }
            avatarError.value?.let { Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall) }
        }
        item {
            Button(
                onClick = {
                    usuario.value = Usuario(
                        email = email.value,
                        nombre = nombre.value,
                        apellidos = apellido.value,
                        avatarUrl = avatarUri.value.toString(),
                        rol = Role.User
                    )
                    contrasena.value = pwd.value
                    onSiguiente()
                },
                enabled = esValido,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0xFF0077B6)) // Color: 0, 119, 182
            ) {
                Text("Siguiente", color = Color.White)
            }
        }
        item {
            Spacer(modifier = Modifier.height(15.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "¿Ya tienes una cuenta?", color = Color(0xFF03045E)) // Color: 3, 4, 94
                Text(
                    text = "Inicia sesión",
                    modifier = Modifier
                        .clickable { navController.navigate(Screens.PantallaInicioSesion.name) }
                        .padding(start = 5.dp),
                    color = Color(0xFF0077B6) // Color: 0, 119, 182
                )
            }
        }
    }
}

/**
 * Composable que representa el segundo paso del registro de usuario.
 * En este paso, el usuario debe ingresar el código de verificación enviado a su correo electrónico.
 *
 * @param codigoVerificacion MutableState que contiene el código de verificación ingresado por el usuario.
 * @param onVerificar Función lambda que se ejecuta al hacer clic en el botón "Verificar".
 */
@Composable
fun PasoDos(codigoVerificacion: MutableState<String>, onVerificar: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Paso 2: Verificación de Código",
                style = MaterialTheme.typography.displaySmall,
                color = Color(0xFF03045E) // Color: 3, 4, 94
            )
        }
        item {
            NormalInput(
                variableState = codigoVerificacion,
                labelId = "Código de Verificación",
                keyBoardType = KeyboardType.Number
            )
        }
        item {
            Text(
                text = "Ingrese el código de verificación de 5 dígitos enviado a su correo electrónico.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        item {
            Button(
                onClick = { onVerificar(codigoVerificacion.value) },
                modifier = Modifier.fillMaxWidth(),
                enabled = codigoVerificacion.value.length == 5,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (codigoVerificacion.value.length == 5) Color(0xFF0077B6) else Color.LightGray,
                    contentColor = Color.White
                )
            ) {
                Text("Verificar", color = Color.White)
            }
        }
    }
}

/**
 * Composable que representa el tercer paso del registro de usuario.
 * En este paso, el usuario revisa la información ingresada antes de registrar su cuenta.
 *
 * @param usuario MutableState que contiene la información del usuario.
 * @param onRegistrar Función lambda que se ejecuta al hacer clic en el botón "Registrar".
 * @param onCancelar Función lambda que se ejecuta al hacer clic en el botón "Cancelar".
 */
@Composable
fun PasoTres(usuario: MutableState<Usuario>, onRegistrar: () -> Unit, onCancelar: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Paso 3: Revisión de Datos",
                style = MaterialTheme.typography.displaySmall,
                color = Color(0xFF03045E) // Color: 3, 4, 94
            )
        }
        item {
            Text("Por favor, revise la información a continuación antes de registrar su cuenta.", color = Color.Gray)
        }
        item {
            Text("Correo Electrónico:", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            Text(usuario.value.email, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
        }
        item {
            Text("Nombre:", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            Text(usuario.value.nombre, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
        }
        item {
            Text("Apellidos:", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            Text(usuario.value.apellidos, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
        }
        item {
            Text("Avatar:", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            val imageUri = usuario.value.avatarUrl.toUri()

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .padding(8.dp)
            ) {
                Image(
                    painter = rememberImagePainter(imageUri),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .border(2.dp, Color.Gray, CircleShape)
                )
            }
        }
        item {
            Button(
                onClick = onRegistrar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0xFF0077B6)) // Color: 0, 119, 182
            ) {
                Text("Registrar", color = Color.White)
            }
        }
        item {
            Button(
                onClick = onCancelar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0xFF03045E)) // Color: 3, 4, 94
            ) {
                Text("Cancelar", color = Color.White)
            }
        }
    }
}

/**
 * Función que valida los datos ingresados en el formulario de registro.
 * Verifica que todos los campos estén completos y que las contraseñas coincidan.
 *
 * @param email Correo electrónico ingresado por el usuario.
 * @param contrasena Contraseña ingresada por el usuario.
 * @param repetirContrasena Repetición de la contraseña ingresada por el usuario.
 * @param errorEmail MutableState que contiene el mensaje de error del correo electrónico.
 * @param errorContrasena MutableState que contiene el mensaje de error de la contraseña.
 * @param errorRepetirContrasena MutableState que contiene el mensaje de error de la repetición de la contraseña.
 * @param nombre Nombre ingresado por el usuario.
 * @param errorNombre MutableState que contiene el mensaje de error del nombre.
 * @param apellido Apellido ingresado por el usuario.
 * @param errorApellido MutableState que contiene el mensaje de error del apellido.
 * @param avatarUri URI del avatar seleccionado por el usuario.
 * @param errorAvatar MutableState que contiene el mensaje de error del avatar.
 * @return Boolean que indica si el formulario es válido o no.
 */
fun validarFormulario(
    email: String, contrasena: String, repetirContrasena: String,
    errorEmail: MutableState<String?>, errorContrasena: MutableState<String?>, errorRepetirContrasena: MutableState<String?>,
    nombre: String, errorNombre: MutableState<String?>, apellido: String, errorApellido: MutableState<String?>,
    avatarUri: Uri?, errorAvatar: MutableState<String?>
): Boolean {
    var esValido = true

    if (email.isEmpty()) {
        errorEmail.value = "El correo electrónico no puede estar vacío"
        esValido = false
    } else {
        errorEmail.value = null
    }

    if (contrasena.isEmpty()) {
        errorContrasena.value = "La contraseña no puede estar vacía"
        esValido = false
    } else if (contrasena.length < 6) {
        errorContrasena.value = "La contraseña debe tener al menos 6 caracteres"
        esValido = false
    } else {
        errorContrasena.value = null
    }

    if (repetirContrasena.isEmpty()) {
        errorRepetirContrasena.value = "La repetición de la contraseña no puede estar vacía"
        esValido = false
    } else if (repetirContrasena != contrasena) {
        errorRepetirContrasena.value = "Las contraseñas no coinciden"
        esValido = false
    } else {
        errorRepetirContrasena.value = null
    }

    if (nombre.isEmpty()) {
        errorNombre.value = "El nombre no puede estar vacío"
        esValido = false
    } else {
        errorNombre.value = null
    }

    if (apellido.isEmpty()) {
        errorApellido.value = "El apellido no puede estar vacío"
        esValido = false
    } else {
        errorApellido.value = null
    }

    if (avatarUri == null) {
        errorAvatar.value = "Debe seleccionar un avatar"
        esValido = false
    } else {
        errorAvatar.value = null
    }

    return esValido
}

/**
 * Función que envía un correo electrónico con un código de verificación al usuario.
 * Utiliza un CoroutineScope para realizar operaciones de E/S en el hilo de E/S.
 *
 * @param to Dirección de correo electrónico del destinatario.
 * @param code Código de verificación a enviar.
 * @param context Contexto de la aplicación.
 * @param onSuccess Función lambda que se ejecuta al enviar el correo exitosamente.
 */
fun sendEmail(
    to: String,
    code: String,
    context: Context,
    onSuccess: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        // Detalles del servidor SMTP
        val host = "smtp.gmail.com"
        val port = 587
        val username = "easycutterinfo@gmail.com"
        val password = "flur dhxa ywhn txzw"

        // Destinatario/a del correo electrónico
        val to = to

        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = host
        props["mail.smtp.port"] = port

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(username))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            message.subject = "Verification code EasyCutter"
            message.setContent("<p>Your verification code is: <strong>$code</strong></p>", "text/html; charset=utf-8")

            Transport.send(message)

            // Realizar operaciones de UI en el hilo principal
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Codigo de verificación enviado correctamente", Toast.LENGTH_LONG).show()

                // Invocar la función de devolución de llamada onSuccess
                onSuccess.invoke()
            }
        } catch (e: MessagingException) {
            e.printStackTrace()

            // Realizar operaciones de UI en el hilo principal
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Error al enviar correo", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
}