package com.example.appmcmovilcare_gruporomero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appmcmovilcare_gruporomero.navigation.Screen
import com.example.appmcmovilcare_gruporomero.ui.screens.HomeScreenCompacta
import com.example.appmcmovilcare_gruporomero.ui.screens.RegistroScreen
import com.example.appmcmovilcare_gruporomero.ui.screens.ResumenScreen
import com.example.appmcmovilcare_gruporomero.ui.screens.AdminDashboardScreen
import com.example.appmcmovilcare_gruporomero.ui.screens.AdminLoginScreen
import com.example.appmcmovilcare_gruporomero.ui.screens.EditarSolicitudScreen
import com.example.appmcmovilcare_gruporomero.ui.theme.AppMCmovilCare_GrupoRomeroTheme
import com.example.appmcmovilcare_gruporomero.viewmodel.UsuarioViewModel

class MainActivity : ComponentActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppMCmovilCare_GrupoRomeroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreenCompacta(
                                viewModel = usuarioViewModel,
                                onNavegarARegistro = {
                                    navController.navigate(Screen.Registro.route)
                                },
                                onNavegarAListaSolicitudes = {
                                    navController.navigate(Screen.AdminLogin.route)
                                },
                                onNavegarAEditarSolicitud = {
                                    navController.navigate(Screen.EditarSolicitud.route)
                                }
                            )
                        }
                        composable(Screen.Registro.route) {
                            RegistroScreen(
                                viewModel = usuarioViewModel,
                                onNavegarAResumen = {
                                    navController.navigate(Screen.Resumen.route) {
                                        popUpTo(Screen.Home.route)
                                    }
                                },
                                onVolver = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(Screen.Resumen.route) {
                            ResumenScreen(
                                viewModel = usuarioViewModel,
                                onNuevaOrden = {
                                    navController.navigate(Screen.Registro.route)
                                }
                            )
                        }
                        composable(Screen.ListaSolicitudes.route) {
                            AdminDashboardScreen(
                                viewModel = usuarioViewModel,
                                onCerrarSesion = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable(Screen.AdminLogin.route) {
                            AdminLoginScreen(
                                viewModel = usuarioViewModel,
                                onVolver = { navController.popBackStack() },
                                onLoginExitoso = {
                                    navController.navigate(Screen.ListaSolicitudes.route) {
                                        popUpTo(Screen.AdminLogin.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.EditarSolicitud.route) {
                            EditarSolicitudScreen(
                                viewModel = usuarioViewModel,
                                onVolver = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}