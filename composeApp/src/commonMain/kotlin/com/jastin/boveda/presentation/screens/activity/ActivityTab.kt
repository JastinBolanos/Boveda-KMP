package com.jastin.boveda.presentation.screens.activity

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.jastin.boveda.domain.model.TransactionStatus
import com.jastin.boveda.presentation.components.TransactionRow
import com.jastin.boveda.presentation.model.TransactionUiModel
import com.jastin.boveda.presentation.model.TxUiStatus
import com.jastin.boveda.presentation.screens.detail.DetailScreen

/* =========================================================================
 * VISTA DE ACTIVIDAD (TAB COMPONENT)
 * =========================================================================
 * Implementa el historial financiero mediante el contrato [Tab].
 * Optimiza el filtrado de datos con [remember] y utiliza el navegador
 * superior (parent) para permitir transiciones a pantalla completa fuera
 * del alcance del BottomNavigation.
 */
object ActivityTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Insights)
            return remember { TabOptions(index = 1u, title = "Actividad", icon = icon) }
        }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
        var selectedFilter by remember { mutableStateOf("Todos") }
        val filters = listOf("Todos", "Entradas", "Salidas", "Pendientes")

        val repository = com.jastin.boveda.globalTransactionRepository
        val domainTransactions = repository.transactions.collectAsState().value

        // EL PUENTE (MAPPER DOMAIN -> UI)
        // Transformamos las entidades puras antes de que lleguen a la lógica de filtrado visual.
        val allTransactions = domainTransactions.map { tx ->
            TransactionUiModel(
                id = tx.id,
                title = tx.receiverName,
                amount = tx.amount,
                status = if (tx.status == TransactionStatus.PENDING) TxUiStatus.PENDING else TxUiStatus.COMPLETED,
                date = "Hoy",
                time = "00:00",
                method = "Saldo Bóveda",
                recipient = tx.receiverName,
                reference = "REF-${tx.id.take(6)}",
                timeline = emptyList()
            )
        }

        // Al usar allTransactions (que ahora es List<TransactionUiModel>),
        // tu lógica original de filtrado por TxUiStatus vuelve a funcionar perfectamente.
        val filteredTransactions = remember(selectedFilter, allTransactions) {
            when (selectedFilter) {
                "Entradas" -> allTransactions.filter { it.amount > 0 && it.status == TxUiStatus.COMPLETED }
                "Salidas" -> allTransactions.filter { it.amount < 0 && it.status == TxUiStatus.COMPLETED }
                "Pendientes" -> allTransactions.filter { it.status == TxUiStatus.PENDING }
                else -> allTransactions
            }
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text("Actividad", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 16.dp))
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        filters.forEach { filter ->
                            val isSelected = selectedFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter, color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold) },
                                colors = FilterChipDefaults.filterChipColors(containerColor = Color.Transparent, selectedContainerColor = MaterialTheme.colorScheme.onSurface, selectedLabelColor = Color.White),
                                border = FilterChipDefaults.filterChipBorder(borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline, enabled = true, selected = isSelected),
                                shape = RoundedCornerShape(24.dp)
                            )
                        }
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).padding(horizontal = 24.dp).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
            ) {
                items(filteredTransactions) { tx ->
                    TransactionRow(tx) { navigator.push(DetailScreen(tx.id)) }
                }
            }
        }
    }
}