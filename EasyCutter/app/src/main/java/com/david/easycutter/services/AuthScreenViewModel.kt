package com.david.easycutter.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.david.easycutter.model.Usuario
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.services.logger.FileLogger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la autenticación de usuarios.
 *
 * Proporciona métodos para iniciar sesión, crear una cuenta, cerrar sesión,
 * y verificar si un usuario está bloqueado o existe.
 *
 * @author David Albarrán García
 */
class AuthScreenViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    // LiveData que indica si se está cargando una operación
    private val _loading = MutableLiveData(false)

    /**
     * Inicia sesión con el correo electrónico y la contraseña proporcionados.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param context Contexto de la aplicación.
     * @param home Función de retorno que se ejecuta cuando el inicio de sesión es exitoso.
     */
    fun signInWithEmailAndPassword(
        email: String,
        password:String,
        context: Context,
        home: ()-> Unit
    ) = viewModelScope.launch {
        try {
            comprobarCuentaBloqueada(context, email.lowercase()) { isBlocked ->
                if (!isBlocked) {
                    login(
                        email.lowercase(),
                        password,
                        context,
                        home
                    )
                }
            }
        }catch (e:Exception){
            FileLogger.logToFile(context,"UserAuth","Error inesperado al logearse")
            Toast.makeText(context, "Error inesperado, intentelo de nuevo en un tiempo", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Realiza el inicio de sesión con el correo electrónico y la contraseña proporcionados.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param context Contexto de la aplicación.
     * @param home Función de retorno que se ejecuta cuando el inicio de sesión es exitoso.
     */
    private fun login(email: String, password: String, context: Context, home: () -> Unit) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FileLogger.logToFile(context,"UserAuth","Logueado correctamente")
                    Toast.makeText(context, "Logueado correctamente", Toast.LENGTH_SHORT).show()
                    home()
                } else {
                    FileLogger.logToFile(context,"UserAuth","Error al loguearse: Credenciales incorrectas")
                    Toast.makeText(context, "Error al loguearse: Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Crea un nuevo usuario con el correo electrónico y la contraseña proporcionados.
     *
     * @param user Datos del usuario a crear.
     * @param password Contraseña del usuario.
     * @param context Contexto de la aplicación.
     * @param home Función de retorno que se ejecuta cuando la creación del usuario es exitosa.
     */
    fun createUserWithEmailAndPassword(
        user: Usuario,
        password: String,
        context: Context,
        home: () -> Unit
    ){
        try {
            if (_loading.value==false){
                _loading.value=true
                comprobarCuentaBloqueada(context, user.email.lowercase()) { isBlocked ->
                    if (!isBlocked){
                        comprobarCuentaExistente(context,user.email.lowercase()){exist->
                            if (!exist){
                                registerUser(user,password,context){
                                    home()
                                }
                            }
                        }
                    }
                }
            }
        }catch (e:Exception){
            FileLogger.logToFile(context,"UserAuth","Error inesperado al crear usuario")
            Toast.makeText(context, "Error inesperado, intentelo de nuevo en un tiempo", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Registra un nuevo usuario en Firebase Authentication.
     *
     * @param user Datos del usuario a registrar.
     * @param password Contraseña del usuario.
     * @param context Contexto de la aplicación.
     * @param home Función de retorno que se ejecuta cuando el registro del usuario es exitoso.
     */
    private fun registerUser(
        user: Usuario,
        password: String,
        context: Context,
        home: () -> Unit
    ) {
        val sharedViewModel = UserViewModel()
        auth.createUserWithEmailAndPassword(user.email.lowercase(),password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    sharedViewModel.saveUser(user, context)
                    FileLogger.logToFile(context,"UserAuth","Usuario creado correctamente")
                    home()
                }else {
                    FileLogger.logToFile(context,"UserAuth","Error al crear usuario")
                    Toast.makeText(context, "Error al crear usuario, vuelve a intentarlo", Toast.LENGTH_SHORT).show()
                }
                _loading.value=false;
            }
    }

    /**
     * Elimina la cuenta del usuario actualmente autenticado.
     *
     * @param context Contexto de la aplicación.
     */
    fun deleteCurrentUser(context: Context) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FileLogger.logToFile(context,"UserAuth","Usuario eliminado correctamente")
                        Toast.makeText(context, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        FileLogger.logToFile(context,"UserAuth","Error al eliminar usuario")
                        Toast.makeText(context, "Error al eliminar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            FileLogger.logToFile(context,"UserAuth","No hay usuario que eliminar")
            Toast.makeText(context, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Obtiene el usuario actualmente autenticado.
     *
     * @return El usuario actualmente autenticado, o null si no hay ningún usuario autenticado.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Verifica si la cuenta asociada al correo electrónico está bloqueada.
     *
     * @param context Contexto de la aplicación.
     * @param email Correo electrónico del usuario.
     * @param callback Función de retorno que se ejecuta con el resultado de la verificación.
     */
    fun comprobarCuentaBloqueada(context: Context, email: String, callback: (Boolean) -> Unit) {
        UserViewModel().getUserBlock(email, context) { user ->
            if (user != null) {
                Toast.makeText(context, "Usuario Bloqueado", Toast.LENGTH_SHORT).show()
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    /**
     * Verifica si existe una cuenta asociada al correo electrónico proporcionado.
     *
     * @param context Contexto de la aplicación.
     * @param email Correo electrónico del usuario.
     * @param callback Función de retorno que se ejecuta con el resultado de la verificación.
     */
    fun comprobarCuentaExistente(context: Context, email: String, callback: (Boolean) -> Unit) {
        UserViewModel().getUser(email, context) { user ->
            if (user != null) {
                Toast.makeText(context, "La cuenta introducida ya existe", Toast.LENGTH_SHORT).show()
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    /**
     * Cierra la sesión del usuario actualmente autenticado.
     *
     * @param context Contexto de la aplicación.
     * @param navController NavController utilizado para la navegación.
     */
    fun logout(context: Context, navController: NavController) {
        // Obtenemos una instancia de FirebaseAuth
        val firebaseAuth = FirebaseAuth.getInstance()
        // Cerramos la sesión actual
        firebaseAuth.signOut()

        // Creamos un listener para controlar los cambios en la autenticación
        val authStateListener = FirebaseAuth.AuthStateListener{
            // Verificamos si no hay ningún usuario autenticado
            if (it.currentUser == null){
                Log.d("Logout", "Sesión cerrada correctamente")
                FileLogger.logToFile(context,"Auth","Logout correcto")
                navController.navigate(Screens.PantallaInicioSesion.name)
            }
        }

        // Agregamos el listener a FirebaseAuth para escuchar los cambios en la autenticación
        firebaseAuth.addAuthStateListener(authStateListener)
    }
}