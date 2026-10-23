package com.example.appmcmovilcare_gruporomero.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home_page")
    data object Registro : Screen("registro_page")
    data object Resumen : Screen("resumen_page")
    data object Perfil : Screen("perfil_page")
}