package com.david.easycutter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.view.admin.PantallaListaPeluquerias
import com.david.easycutter.view.cuenta.Contactanos
import com.david.easycutter.view.cuenta.PantallaCuenta
import com.david.easycutter.view.admin.PantallaListaUsuarios
import com.david.easycutter.view.admin.PantallaPeticiones
import com.david.easycutter.view.autenticacion.PantallaInicioSesion
import com.david.easycutter.view.autenticacion.PantallaRegistro
import com.david.easycutter.view.cuenta.SolicitarBarberia
import com.david.easycutter.view.elements.ShowLocalitation
import com.david.easycutter.view.peluquero.PantallaCitasBarbero
import com.david.easycutter.view.peluquero.PantallaEditarPeluqueria
import com.david.easycutter.view.peluquero.PantallaListaBarberiasBarbero
import com.david.easycutter.view.splash.SplashScreen
import com.david.easycutter.view.usuario.MisCitas
import com.david.easycutter.view.usuario.PantallaListaReservarCita
import com.david.easycutter.view.usuario.ReservarCita

/**
 * Composable que define la navegación de la aplicación.
 *
 * Este Composable utiliza Navigation Compose para definir las diferentes pantallas de la aplicación
 * y sus transiciones.
 *
 * @author David Albarrán García
 */
@Composable
fun Navigation() {
    val navControler = rememberNavController()

    // Define el NavHost con las pantallas y sus transiciones
    NavHost(
        navController = navControler,
        startDestination = Screens.SplashScreen.name
    ) {
        // Pantalla SplashScreenLogin
        composable(Screens.SplashScreen.name) {
            SplashScreen(navController = navControler)
        }

        // Pantalla Login
        composable(Screens.PantallaInicioSesion.name) {
            PantallaInicioSesion(navController = navControler)
        }

        // Pantalla Register
        composable(Screens.PantallaRegistro.name) {
            PantallaRegistro(navController = navControler)
        }

        // Pantalla Listado Usuarios
        composable(Screens.PantallaListaUsuarios.name) {
            PantallaListaUsuarios(navControler)
        }

        // Pantalla Listado Peluquerías Admin
        composable(Screens.PantallaListaPeluquerias.name) {
            PantallaListaPeluquerias(navControler)
        }

        // Pantalla Peticiones
        composable(Screens.PantallaPeticiones.name) {
            PantallaPeticiones(navControler)
        }

        // Pantalla Listado Peluquerías User
        composable(Screens.PantallaListaReservarCita.name) {
            PantallaListaReservarCita(navControler)
        }

        // Pantalla Reservar Cita
        composable(Screens.ReservarCita.name + "/{barberShopId}") { backStackEntry ->
            val barberShopId = backStackEntry.arguments?.getString("barberShopId").orEmpty()
            ReservarCita(barberShopId, navControler)
        }

        // Pantalla Listado Peluquerías Barber
        composable(Screens.PantallaListaBarberiasBarbero.name) {
            PantallaListaBarberiasBarbero(navControler)
        }

        // Pantalla Citas Peluquero
        composable(Screens.PantallaCitasBarbero.name + "/{barberShopId}") { backStackEntry ->
            val barberShopId = backStackEntry.arguments?.getString("barberShopId").orEmpty()
            PantallaCitasBarbero(barberShopId, navControler)
        }

        // Pantalla Listado Mis Citas
        composable(Screens.MisCitas.name) {
            MisCitas(navControler)
        }

        // Pantalla Solicitudes
        composable(Screens.SolicitarBarberia.name) {
            SolicitarBarberia(navControler)
        }

        // Pantalla Contactanos
        composable(Screens.Contactanos.name) {
            Contactanos(navControler)
        }

        // Pantalla Account
        composable(Screens.PantallaCuenta.name) {
            PantallaCuenta(navControler)
        }

        // Pantalla Ubicación
        composable(Screens.ShowLocation.name + "/{latitude}:{longitude}") { backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("latitude").orEmpty()
            val longitude = backStackEntry.arguments?.getString("longitude").orEmpty()
            ShowLocalitation(latitude.toDouble(), longitude.toDouble(), navControler)
        }

        // Pantalla editar barberShop
        composable(Screens.PantallaEditarPeluqueria.name + "/{idBarberShop}") { backStackEntry ->
            val idBarberShop = backStackEntry.arguments?.getString("idBarberShop").orEmpty()
            PantallaEditarPeluqueria(idBarberShop, navControler)
        }
    }
}