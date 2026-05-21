package com.jastin.boveda.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.jastin.boveda.presentation.components.TransactionRow
import com.jastin.boveda.presentation.screens.activity.ActivityTab
import com.jastin.boveda.presentation.screens.detail.DetailScreen
import com.jastin.boveda.presentation.screens.transfer.TransferScreen
import com.jastin.boveda.presentation.screens.main.LocalMenuDrawerState
import com.jastin.boveda.presentation.theme.*
import com.jastin.boveda.utils.formatMoney
import kotlinx.coroutines.launch
import kotlin.math.max

/* =========================================================================
 * PANTALLA PRINCIPAL (DASHBOARD)
 * Tab principal de la aplicación. Actúa como el centro neurálgico de la UI,
 * observando los saldos en tiempo real y proveyendo acceso rápido a transferencias
 * y movimientos recientes.
 * ========================================================================= */
object HomeTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Home)
            return remember { TabOptions(index = 0u, title = "Inicio", icon = icon) }
        }

    @Composable
    override fun Content() {

        // --- 1. ENRUTADORES Y PUENTES (COMPOSITION LOCALS) ---
        // ¡CRÍTICO! Usamos navigator.parent para que las pantallas nuevas se abran
        // a pantalla completa y no queden atrapadas debajo del BottomNavigation.
        val navigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current

        // ¡MINA TERRESTRE! El Drawer no existe en este Tab, existe en el MainScreen.
        // Usamos CompositionLocal (LocalMenuDrawerState) para atravesar el árbol de
        // dependencias y poder abrirlo desde aquí sin tener que pasar callbacks infinitos.
        val drawerState = LocalMenuDrawerState.current
        val scope = rememberCoroutineScope()

        // --- 2. OBSERVACIÓN DEL DOMINIO ---
        val repository = com.jastin.boveda.globalTransactionRepository
        val balanceState = repository.currentBalance.collectAsState()
        val balance = balanceState.value
        val allTransactionsState = repository.transactions.collectAsState()
        val transactions = allTransactionsState.value.take(3)

        // --- 3. REGLAS DE NEGOCIO VISUALES ---
        // Aseguramos que visualmente el saldo jamás sea negativo (Data Sanitization).
        val displayBalance = max(0.0, balance)
        val hasInsufficientFunds = balance <= 0

        Scaffold(
            containerColor = Slate50,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(48.dp).background(Slate900, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("J", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Bienvenido de vuelta", fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Medium)
                            Text("Jastin Abel", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Slate950)
                        }
                    }

                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Slate950, modifier = Modifier.size(28.dp))
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding).padding(horizontal = 24.dp).fillMaxSize()
            ) {

                // --- 4. TARJETA DE SALDO PRINCIPAL ---
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate950)
                ) {
                    Column(modifier = Modifier.padding(28.dp)) {
                        Text("Saldo Disponible", color = Slate400, fontSize = 14.sp)
                        Text(
                            text = formatMoney(displayBalance),
                            color = if (hasInsufficientFunds) Red500 else Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        Button(
                            onClick = { navigator.push(TransferScreen(balance)) },
                            enabled = !hasInsufficientFunds,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Emerald500, contentColor = Slate950,
                                disabledContainerColor = Slate800, disabledContentColor = Slate400
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (hasInsufficientFunds) "Saldo Insuficiente" else "Transferir", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // --- 5. SECCIÓN DE ACTIVIDAD RECIENTE ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Actividad Reciente", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Slate950)
                    TextButton(onClick = { tabNavigator.current = ActivityTab }) {
                        Text("Ver todo", color = Emerald500, fontWeight = FontWeight.SemiBold)
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(transactions) { tx ->
                        TransactionRow(tx) { navigator.push(DetailScreen(tx)) }
                    }
                }
            }
        }
    }
}