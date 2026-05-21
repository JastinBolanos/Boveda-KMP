package com.jastin.boveda.presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
 * Contenedor principal de la aplicación posterior al Splash Screen.
 * Administra el enrutamiento interno mediante Tabs y aloja el sistema global de
 * navegación (Bottom Navigation Bar) y el Drawer lateral.
 * ========================================================================= */

// ¡MINA TERRESTRE! Puente invisible de dependencias (CompositionLocal).
// Permite que nodos hijos (como HomeTab) disparen la apertura del menú lateral
// sin necesidad de acoplarse pasando callbacks (`() -> Unit`) a lo largo del árbol.
val LocalMenuDrawerState = compositionLocalOf<DrawerState> { error("DrawerState no provisto") }

class MainScreen : Screen {

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        // --- 1. CONFIGURACIÓN DEL MENÚ LATERAL (RTL HACK) ---
        // ¡CRÍTICO! Material3 renderiza los menús por la izquierda (LTR) por defecto.
        // Forzamos el contexto de composición a 'Right-To-Left' (RTL) para que el Drawer
        // emerja desde el borde derecho, y luego revertimos inmediatamente a LTR para
        // no romper los textos de la UI interna.
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
                            drawerContainerColor = Color.White,
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

                        // ¡MINA TERRESTRE! Interceptores físicos del OS.
                        // Orden de prioridad del botón físico "Atrás":
                        // 1. Cierra el menú lateral (si está abierto).
                        // 2. Navega al Home (si está en otro tab).
                        // 3. Sale de la app (Default Android behavior).
                        BackPressHandler(enabled = drawerState.isOpen) {
                            scope.launch { drawerState.close() }
                        }
                        BackPressHandler(enabled = tabNavigator.current != HomeTab && !drawerState.isOpen) {
                            tabNavigator.current = HomeTab
                        }

                        // --- 3. BOTTOM NAVIGATION BAR ---
                        Scaffold(
                            containerColor = Slate50,
                            bottomBar = {
                                Surface(
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp, vertical = 24.dp)
                                        .navigationBarsPadding()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(32.dp),
                                    color = Color.White,
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
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text("Jastin Abel", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Slate950, modifier = Modifier.padding(top = 24.dp, bottom = 32.dp))
            DrawerItem(icon = Icons.Default.Security, text = "Token Digital")
            DrawerItem(icon = Icons.Default.Lock, text = "Seguridad y privacidad")
            DrawerItem(icon = Icons.Default.Place, text = "Puntos de atención")
            DrawerItem(icon = Icons.Default.Headset, text = "Comunícate con nosotros")
            DrawerItem(icon = Icons.Default.Info, text = "Acerca de Bóveda")
            Spacer(modifier = Modifier.weight(1f))
            Card(colors = CardDefaults.cardColors(containerColor = Slate50), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nota del Desarrollador", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate950, modifier = Modifier.padding(bottom = 8.dp))
                    Text("Este software fue construido con KMP. Esta sección es demostrativa para exhibir arquitectura y UI/UX, por lo que las opciones anteriores no están conectadas a flujos de producción.", fontSize = 12.sp, color = Slate400, lineHeight = 16.sp)
                }
            }
        }
    }

    @Composable
    private fun DrawerItem(icon: ImageVector, text: String) {
        Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Slate900, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, fontSize = 16.sp, color = Slate900, fontWeight = FontWeight.Medium)
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
        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Slate950).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }, contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Transferir", tint = Emerald500, modifier = Modifier.size(32.dp))
        }
    }
}