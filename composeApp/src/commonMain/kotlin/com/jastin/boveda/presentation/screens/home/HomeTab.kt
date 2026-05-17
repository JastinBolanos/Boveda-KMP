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
import androidx.compose.runtime.remember
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
import com.jastin.boveda.presentation.model.TransactionUiModel
import com.jastin.boveda.presentation.screens.activity.ActivityTab
import com.jastin.boveda.presentation.screens.detail.DetailScreen
import com.jastin.boveda.presentation.screens.transfer.TransferScreen
import com.jastin.boveda.presentation.theme.*
import com.jastin.boveda.utils.formatMoney
import kotlin.math.max

object HomeTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Home)
            return remember { TabOptions(index = 0u, title = "Inicio", icon = icon) }
        }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current

        // Valores iniciales (Más adelante vendrán de la base de datos)
        val balance = 1500.00
        val transactions = emptyList<TransactionUiModel>()
        val displayBalance = max(0.0, balance)
        val hasInsufficientFunds = balance <= 0

        Scaffold(
            containerColor = Slate50,
            // 1. BARRA SUPERIOR (CON SAFE AREA)
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
                            modifier = Modifier
                                .size(48.dp)
                                .background(Slate900, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("J", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Bienvenido de vuelta", fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Medium)
                            Text("Jastin", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Slate950)
                        }
                    }

                    // ICONO DE TRES RAYAS (MENU)
                    IconButton(onClick = { /* Abrir Menu Lateral o Ajustes */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Slate950,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .fillMaxSize()
            ) {
                // TARJETA DE SALDO
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
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
                                containerColor = Emerald500,
                                contentColor = Slate950,
                                disabledContainerColor = Slate800,
                                disabledContentColor = Slate400
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (hasInsufficientFunds) "Saldo Insuficiente" else "Transferir", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // CABECERA DE ACTIVIDAD CON "VER TODO"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Actividad Reciente",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Slate950
                    )
                    TextButton(onClick = { tabNavigator.current = ActivityTab }) {
                        Text("Ver todo", color = Emerald500, fontWeight = FontWeight.SemiBold)
                    }
                }

                // LISTA DE MOVIMIENTOS
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