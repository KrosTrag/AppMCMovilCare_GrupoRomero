package com.example.appmcmovilcare_gruporomero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.appmcmovilcare_gruporomero.navigation.Screen
import com.example.appmcmovilcare_gruporomero.ui.screens.HomeScreenCompacta
import com.example.appmcmovilcare_gruporomero.ui.screens.RegistroScreen
import com.example.appmcmovilcare_gruporomero.ui.screens.ResumenScreen
import com.example.appmcmovilcare_gruporomero.ui.theme.AppMCmovilCare_GrupoRomeroTheme
import com.example.appmcmovilcare_gruporomero.viewmodel.UsuarioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppMCmovilCare_GrupoRomeroTheme {
                AppNavegacion()
            }
        }
    }
}

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val usuarioViewModel: UsuarioViewModel = viewModel()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreenCompacta(onNavegarRegistro = { navController.navigate(Screen.Registro.route) })
            }
            composable(Screen.Registro.route) {
                RegistroScreen(navController = navController, viewModel = usuarioViewModel)
            }
            composable(Screen.Resumen.route) {
                ResumenScreen(viewModel = usuarioViewModel)
            }
        }
    }
}