package com.david.easycutter.view.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.david.easycutter.R
import com.david.easycutter.model.enums.Screens
import kotlinx.coroutines.delay

/**
 * Composable que representa la pantalla de presentación de la aplicación.
 * Muestra el logo de la aplicación y un indicador de progreso lineal mientras realiza alguna animación de entrada.
 * Después de un breve retraso, navega a la pantalla de inicio de sesión.
 *
 * @param navController El controlador de navegación para manejar la navegación entre pantallas.
 */
@Composable
fun SplashScreen(navController: NavController){

    // Animación de escala para la transición de la pantalla de presentación
    val scale = remember {
        Animatable(0f)
    }

    // Inicia la animación de escala y navega a la pantalla de inicio de sesión después de un retraso
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.8f,
            animationSpec = tween(delayMillis = 800)
        )
        delay(1000)
        navController.navigate(Screens.PantallaInicioSesion.name)
        /*if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
            navController.navigate(Screens.LoginScreen.name)
        }else{
            navController.navigate(Screens.HomeScreen.name){
                popUpTo(Screens.SplashScreen.name){
                    inclusive=true
                }
            }
        }*/
    }

    // Interfaz de usuario de la pantalla de presentación
    Surface(color = Color(202, 240, 248)) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .scale(scale.value) // Aplica la escala a la Columna
        ) {
            // Logo de la aplicación
            Image(
                painter = painterResource(id = R.drawable.logo_easy_cutter),
                contentDescription = "Logo App",
                modifier = Modifier
                    .size(250.dp)
                    .padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            // Indicador de progreso lineal
            LinearProgressIndicator()
        }
    }

}