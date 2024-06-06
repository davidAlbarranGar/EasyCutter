package com.david.easycutter.view.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SavedSearch
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.david.easycutter.R
import com.david.easycutter.model.enums.Role
import com.david.easycutter.model.enums.Screens
import com.david.easycutter.services.AuthScreenViewModel
import com.david.easycutter.services.UserViewModel
import kotlinx.coroutines.launch

/**
 * Componente de AppBar personalizado que incluye un cajón de navegación (Drawer) y una barra superior (TopAppBar).
 * También maneja la navegación entre diferentes pantallas de la aplicación.
 *
 * @param title Título de la pantalla actual.
 * @param navController Controlador de navegación para manejar las transiciones entre pantallas.
 * @param function Función lambda que representa el contenido principal de la pantalla.
 */
@Composable
fun AppBar(title: String, navController: NavController, function: @Composable () -> Unit) {
    // Estado mutable para almacenar el rol del usuario actual
    var roleCurrentUser by remember { mutableStateOf(Role.User) }

    // Contexto actual
    val context = LocalContext.current

    // Obtener el usuario actual y obtener su rol
    LaunchedEffect(Unit) {
        UserViewModel().getCurrentUser("users", context) { user ->
            user?.let {
                roleCurrentUser = it.rol
            }
        }
    }

    // Estado del Drawer (cajón de navegación)
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // Estado del Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // Scope para gestionar las corrutinas
    val scope = rememberCoroutineScope()

    // Creación del Drawer
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen || drawerState.isClosed,
        drawerContent = {
            // Contenido del Drawer según el rol del usuario
            if (roleCurrentUser == Role.Admin) {
                MyDrawerContentAdmin(navController = navController, title = title)
            } else {
                MyDrawerContentUser(navController = navController, title = title, roleCurrentUser = roleCurrentUser)
            }
        },
    ) {
        // Scaffold que contiene la estructura básica de la pantalla
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                // AppBar personalizado
                MyTopBar(
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    navController,
                    title
                )

            },
        ) { paddingValues ->
            // Contenido principal de la pantalla
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                function()
            }
        }
    }
}

/**
 * Barra superior personalizada que incluye el título de la pantalla y opciones de menú.
 *
 * @param onMenuClick Función lambda para manejar el clic en el botón de menú.
 * @param navController Controlador de navegación para manejar las transiciones entre pantallas.
 * @param title Título de la pantalla actual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    onMenuClick: () -> Unit,
    navController: NavController,
    title: String,
) {
    // Configuración del comportamiento de desplazamiento para la barra superior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    // Estado para controlar la visibilidad del menú desplegable
    var showMoreAccount by rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    // Composable para la barra superior centrada
    CenterAlignedTopAppBar(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            // Botón de menú para mostrar el Drawer
            IconButton(onClick = { onMenuClick() }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            // Icono de tres puntos para mostrar el menú desplegable
            IconButton(onClick = { showMoreAccount = true }) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Cuenta"
                )
            }
            // Menú desplegable
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
}

data class MenuItem(val title: String, val icon: ImageVector)

/**
 * Composable para mostrar el contenido del cajón de navegación para un usuario administrador.
 * Muestra un menú desplegable con elementos de menú y una imagen en la parte superior.
 *
 * @param modifier el modificador que se aplica al composable.
 * @param navController el controlador de navegación para manejar las acciones de navegación.
 * @param title el título de la pantalla actual.
 */
@Composable
fun MyDrawerContentAdmin(
    modifier: Modifier = Modifier,
    navController: NavController,
    title: String
) {
    // Lista de elementos del menú con títulos e íconos
    val menu = listOf(
        MenuItem(
            title = "Lista Usuarios",
            icon = Icons.Default.FormatListBulleted,
        ),
        MenuItem(
            title = "Lista Peluquerías",
            icon = Icons.Default.FormatListNumbered,
        ),
        MenuItem(
            title = "Peticiones",
            icon = Icons.Default.CallReceived,
        )
    )

    // Creación del menú desplegable
    ModalDrawerSheet(modifier) {
        Column(modifier.fillMaxSize(0.7f)) {
            // Sección superior del menú con una imagen
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.3f)
                    .fillMaxWidth()
            ){
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Imagen menú desplegable",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Lista desplegable con elementos de menú
            LazyColumn (
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ){
                items(menu) { menuList ->
                    val selected = (title == "Lista Usuarios" && menuList == menu[0]) ||
                            (title == "Lista Peluquerías" && menuList == menu[1]) ||
                            (title == "Peticiones" && menuList == menu[2])

                    // Elemento del menú de navegación
                    NavigationDrawerItem(
                        shape = MaterialTheme.shapes.small,
                        label = {
                            Text(
                                text = menuList.title,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        },
                        selected = selected,
                        icon = {
                            Icon(
                                imageVector = menuList.icon,
                                contentDescription = menuList.title
                            )
                        },
                        onClick = {
                            // Navegación a diferentes pantallas según la selección del menú
                            when (menuList.title) {
                                "Lista Usuarios" -> {
                                    navController.navigate(Screens.PantallaListaUsuarios.name)
                                }
                                "Lista Peluquerías" -> {
                                    navController.navigate(Screens.PantallaListaPeluquerias.name)
                                }
                                "Peticiones" -> {
                                    navController.navigate(Screens.PantallaPeticiones.name)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Composable para mostrar el contenido del cajón de navegación para un usuario normal o peluquero.
 * Muestra un menú desplegable con elementos de menú y una imagen en la parte superior.
 *
 * @param modifier el modificador que se aplica al composable.
 * @param navController el controlador de navegación para manejar las acciones de navegación.
 * @param title el título de la pantalla actual.
 * @param roleCurrentUser el rol del usuario actual.
 */
@Composable
fun MyDrawerContentUser(
    modifier: Modifier = Modifier,
    navController: NavController,
    title: String,
    roleCurrentUser: Role
) {
    // Lista de elementos del menú con títulos e íconos
    var menu = listOf<MenuItem>()
    if (roleCurrentUser == Role.User) {
        menu = listOf(
            MenuItem(
                title = "Reservar Cita",
                icon = Icons.Default.SavedSearch,
            ),
            MenuItem(
                title = "Mis Citas",
                icon = Icons.Default.Home,
            )
        )
    } else {
        menu = listOf(
            MenuItem(
                title = "Reservar Cita",
                icon = Icons.Default.SavedSearch,
            ),
            MenuItem(
                title = "Mis Citas",
                icon = Icons.Default.Home,
            ),
            MenuItem(
                title = "Mis Peluquerías",
                icon = Icons.Default.LibraryBooks,
            )
        )
    }

    // Creación del menú desplegable
    ModalDrawerSheet(modifier) {
        Column(modifier.fillMaxSize(0.7f)) {
            // Sección superior del menú con una imagen
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.3f)
                    .fillMaxWidth()
            ){
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Imagen menú desplegable",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Lista desplegable con elementos de menú
            LazyColumn (
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ){
                items(menu) { menuList ->
                    var selected: Boolean
                    if (roleCurrentUser == Role.User) {
                        selected = (title == "Reservar Cita" && menuList == menu[0]) ||
                                (title == "Mis Citas" && menuList == menu[1])
                    } else {
                        selected = (title == "Reservar Cita" && menuList == menu[0]) ||
                                (title == "Mis Citas" && menuList == menu[1]) ||
                                (title == "Mis Peluquerías" && menuList == menu[2])
                    }


                    // Elemento del menú de navegación
                    NavigationDrawerItem(
                        shape = MaterialTheme.shapes.small,
                        label = {
                            Text(
                                text = menuList.title,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        },
                        selected = selected,
                        icon = {
                            Icon(
                                imageVector = menuList.icon,
                                contentDescription = menuList.title
                            )
                        },
                        onClick = {
                            // Navegación a diferentes pantallas según la selección del menú
                            when (menuList.title) {
                                "Reservar Cita" -> {
                                    navController.navigate(Screens.PantallaListaReservarCita.name)
                                }
                                "Mis Citas" -> {
                                    navController.navigate(Screens.MisCitas.name)
                                }
                                "Mis Peluquerías" -> {
                                    navController.navigate(Screens.PantallaListaBarberiasBarbero.name)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}