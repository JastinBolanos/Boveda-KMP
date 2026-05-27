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
import com.jastin.boveda.presentation.components.TransactionRow
import com.jastin.boveda.presentation.model.TxUiStatus
import com.jastin.boveda.presentation.screens.detail.DetailScreen
import com.jastin.boveda.presentation.theme.*

/*
 * =========================================================================
 * VISTA DE ACTIVIDAD (TAB COMPONENT)
 * =========================================================================
 * Implementación de la pestaña de historial de transacciones.
 * * Estrategia de Navegación: Al implementar [Tab] en lugar de Screen, delegamos
 * el ciclo de vida a Voyager para que mantenga la vista activa en memoria. Esto
 * preserva el estado de la UI (scroll, filtros seleccionados) al intercambiar pestañas.
 * * Enrutamiento de Capas: El acceso al Root Navigator (`parent`) garantiza que las
 * transiciones hacia vistas de detalle se dibujen sobre el BottomNavigationBar.
 * * Optimización de Renderizado: Se utiliza el patrón de 'Derived State' (memoization
 * con [remember] sobre listas filtradas) para prevenir recálculos costosos en el
 * Main Thread durante las recomposiciones gráficas.
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
        val transactionsState = repository.transactions.collectAsState()
        val allTransactions = transactionsState.value

        val filteredTransactions = remember(selectedFilter, allTransactions) {
            when (selectedFilter) {
                "Entradas" -> allTransactions.filter { it.amount > 0 && it.status == TxUiStatus.COMPLETED }
                "Salidas" -> allTransactions.filter { it.amount < 0 && it.status == TxUiStatus.COMPLETED }
                "Pendientes" -> allTransactions.filter { it.status == TxUiStatus.PENDING }
                else -> allTransactions
            }
        }

        Scaffold(
            containerColor = Slate50,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text("Actividad", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Slate950, modifier = Modifier.padding(bottom = 16.dp))
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        filters.forEach { filter ->
                            val isSelected = selectedFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter, color = if (isSelected) Color.White else Slate800, fontWeight = FontWeight.SemiBold) },
                                colors = FilterChipDefaults.filterChipColors(containerColor = Color.Transparent, selectedContainerColor = Slate950, selectedLabelColor = Color.White),
                                border = FilterChipDefaults.filterChipBorder(borderColor = if (isSelected) Color.Transparent else Slate100, enabled = true, selected = isSelected),
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