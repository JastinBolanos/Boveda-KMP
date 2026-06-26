package com.jastin.boveda.presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.*
import com.jastin.boveda.globalTransactionRepository
import com.jastin.boveda.presentation.screens.activity.ActivityTab
import com.jastin.boveda.presentation.screens.home.HomeTab
import com.jastin.boveda.presentation.screens.transfer.TransferScreen
import com.jastin.boveda.presentation.theme.*
import com.jastin.boveda.utils.BackPressHandler
import kotlinx.coroutines.launch

/* =========================================================================
 * ROOT NAVIGATOR & MAIN SCAFFOLD
 * Contenedor principal tras el Splash. Gestiona el enrutamiento interno
 * (Tabs), la navegación global (Bottom Bar) y el Drawer lateral.
 * ========================================================================= */

val LocalMenuDrawerState = compositionLocalOf<DrawerState> { error("DrawerState no provisto") }

class MainScreen : Screen {

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        // --- 1. CONFIGURACIÓN DEL MENÚ LATERAL (RTL HACK) ---
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl,
            LocalMenuDrawerState provides drawerState
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                scrimColor = Slate950.copy(alpha = 0.5f),
                drawerContent = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        ModalDrawerSheet(
                            drawerContainerColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.width(320.dp).fillMaxHeight()
                        ) {
                            DrawerMenuContent()
                        }
                    }
                }
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {

                    // --- 2. SISTEMA DE PESTAÑAS Y NAVEGACIÓN ---
                    TabNavigator(HomeTab) {
                        val tabNavigator = LocalTabNavigator.current

                        BackPressHandler(enabled = drawerState.isOpen) {
                            scope.launch { drawerState.close() }
                        }
                        BackPressHandler(enabled = tabNavigator.current != HomeTab && !drawerState.isOpen) {
                            tabNavigator.current = HomeTab
                        }

                        // --- 3. BOTTOM NAVIGATION BAR ---
                        Scaffold(
                            containerColor = MaterialTheme.colorScheme.background,
                            bottomBar = {
                                Surface(
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp, vertical = 24.dp)
                                        .navigationBarsPadding()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(32.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 8.dp,
                                    shadowElevation = 12.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                            TabIconItem(tab = HomeTab, icon = Icons.Default.Home, label = "Inicio", isSelected = tabNavigator.current == HomeTab) { tabNavigator.current = HomeTab }
                                        }
                                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                            FloatingTransferButton {
                                                val currentBalance = globalTransactionRepository.currentBalance.value
                                                rootNavigator.push(TransferScreen(currentBalance))
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                            TabIconItem(tab = ActivityTab, icon = Icons.Default.Insights, label = "Actividad", isSelected = tabNavigator.current == ActivityTab) { tabNavigator.current = ActivityTab }
                                        }
                                    }
                                }
                            }
                        ) { padding ->
                            Box(modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {
                                CurrentTab()
                            }
                        }
                    }
                }
            }
        }
    }

    // --- 4. COMPONENTES PRIVADOS DEL MENÚ (STATELESS) ---
    @Composable
    private fun DrawerMenuContent() {
        val isDarkMode by com.jastin.boveda.globalIsDarkMode.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text(
                "Jastin Abel",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 24.dp, bottom = 32.dp)
            )

            DrawerItem(icon = Icons.Default.Security, text = "Token Digital")
            DrawerItem(icon = Icons.Default.Lock, text = "Seguridad y privacidad")
            DrawerItem(icon = Icons.Default.Place, text = "Puntos de atención")
            DrawerItem(icon = Icons.Default.Headset, text = "Comunícate con nosotros")
            DrawerItem(icon = Icons.Default.Info, text = "Acerca de Bóveda")

            Spacer(modifier = Modifier.weight(1f))

            // Interruptor de Modo Oscuro/Modo Claro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Modo Oscuro",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Modo Oscuro",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }

                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { isDark ->
                        com.jastin.boveda.globalSettingsRepository.updateThemePreference(isDark)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Emerald500,
                        checkedTrackColor = MaterialTheme.colorScheme.background,
                        uncheckedThumbColor = Slate400,
                        uncheckedTrackColor = Slate100
                    )
                )
            }

            // Nota del Desarrollador
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Nota del Desarrollador",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        "Este software fue construido con KMP. Esta sección es demostrativa para exhibir arquitectura y UI/UX, por lo que las opciones anteriores no están conectadas a flujos de producción.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }

    @Composable
    private fun DrawerItem(icon: ImageVector, text: String) {
        Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // --- 5. COMPONENTES PRIVADOS DEL BOTTOM NAV (STATELESS) ---
    @Composable
    private fun TabIconItem(tab: Tab, icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }) {
            Icon(imageVector = icon, contentDescription = label, tint = if (isSelected) Emerald500 else Slate400, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 11.sp, color = if (isSelected) Emerald500 else Slate400, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
        }
    }

    @Composable
    private fun FloatingTransferButton(onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Transferir", tint = Emerald500, modifier = Modifier.size(32.dp))
        }
    }
}