package com.jastin.boveda

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.jastin.boveda.presentation.screens.main.MainScreen
import com.jastin.boveda.presentation.theme.BovedaTheme

@Composable
fun App() {
    BovedaTheme {
        Navigator(MainScreen())
    }
}