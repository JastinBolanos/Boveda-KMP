package com.jastin.boveda.presentation.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.*
import com.jastin.boveda.presentation.screens.activity.ActivityTab
import com.jastin.boveda.presentation.screens.home.HomeTab
import com.jastin.boveda.presentation.theme.*

class MainScreen : Screen {
    @Composable
    override fun Content() {
        // Iniciamos en el HomeTab
        TabNavigator(HomeTab) {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(ActivityTab)
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(bottom = padding.calculateBottomPadding())) {
                    CurrentTab()
                }
            }
        }
    }

    // Componente privado para no repetir código de los botones de la barra
    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current
        val isSelected = tabNavigator.current == tab

        NavigationBarItem(
            selected = isSelected,
            onClick = { tabNavigator.current = tab },
            icon = {
                tab.options.icon?.let { icon ->
                    Icon(painter = icon, contentDescription = tab.options.title)
                }
            },
            label = { Text(tab.options.title) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Emerald500,
                selectedTextColor = Emerald500,
                unselectedIconColor = Slate400,
                unselectedTextColor = Slate400,
                indicatorColor = Emerald500.copy(alpha = 0.1f)
            )
        )
    }
}