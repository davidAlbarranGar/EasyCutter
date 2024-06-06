package com.david.easycutter.view.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.services.AuthScreenViewModel

/**
 * Composable para mostrar una barra de aplicación simple con un título y opciones de navegación y menú.
 *
 * @param title el título que se mostrará en la barra de la aplicación.
 * @param navController el controlador de navegación para manejar las acciones de navegación.
 * @param function la función que define el contenido principal de la pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAppBar(title: String, navController: NavController, function: @Composable () -> Unit) {
    // Configuración del comportamiento de desplazamiento para la barra de aplicación
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    // Estado para controlar la visibilidad del menú desplegable
    var showMoreAccount by rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    // Composable de Material Scaffold que contiene la barra de aplicación y el contenido principal
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            // Barra de aplicación con título y opciones de navegación y menú
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    // Icono de navegación hacia atrás
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    // Icono de cuenta para mostrar el menú desplegable
                    IconButton(onClick = { showMoreAccount = true }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Cuenta"
                        )
                    }
                    // Menú desplegable de opciones de cuenta
                    DropdownMenu(
                        expanded = showMoreAccount,
                        onDismissRequest = { showMoreAccount = !showMoreAccount },
                        modifier = Modifier.width(140.dp)
                    ) {
                        // Opciones del menú desplegable
                        DropdownMenuItem(
                            text = {
                                Text(text = "Cuenta")
                            },
                            onClick = {
                                navController.navigate(Screens.PantallaCuenta.name)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(text = "Contactanos")
                            },
                            onClick = {
                                navController.navigate(Screens.Contactanos.name)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(text = "Logout")
                            },
                            onClick = {
                                AuthScreenViewModel().logout(context,navController)
                            }
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        // Contenedor principal del contenido de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            function() // Función que define el contenido principal de la pantalla
        }
    }
}