package com.jastin.boveda.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jastin.boveda.presentation.components.BovedaCard
import com.jastin.boveda.presentation.model.TxUiStatus
import com.jastin.boveda.presentation.theme.*
import com.jastin.boveda.utils.formatMoney
import kotlin.math.abs

/* =========================================================================
 * PANTALLA DE DETALLE DE TRANSACCIÓN (STATELESS SCREEN)
 * Renderiza la información exhaustiva de un movimiento financiero.
 * Al inyectar él [TransactionUiModel] directamente por el constructor de Voyager,
 * eliminamos la necesidad de un ViewModel local y evitamos golpear SQLite
 * con re-consultas innecesarias. La vista anterior ya digirió los datos.
 * ========================================================================= */

data class DetailScreen(val transactionId: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val transactions by com.jastin.boveda.globalTransactionRepository.transactions.collectAsState()
        val liveTransaction = transactions.find { it.id == transactionId }

        if (liveTransaction == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Emerald500)
            }
            return
        }

        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Text("Detalle de Operación", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.width(48.dp))
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(horizontal = 24.dp).fillMaxSize()) {

                // --- 1. IMPACTO FINANCIERO (HERO SECTION) ---
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)) {

                    val isPending = liveTransaction.status == TxUiStatus.PENDING
                    Text(
                        text = if (isPending) "¡Operación encolada!" else "¡Transferencia Exitosa!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isPending) Amber500 else Emerald500,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(if (liveTransaction.amount > 0) "RECIBISTE" else "ENVIASTE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate400)
                    Text(formatMoney(abs(liveTransaction.amount)), fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                    Text(liveTransaction.title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                }

                // --- 2. METADATOS DE LA OPERACIÓN ---
                BovedaCard(modifier = Modifier.padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        DetailRow("Estado", if (liveTransaction.status == TxUiStatus.PENDING) "Pendiente" else "Completado")
                        HorizontalDivider(color = MaterialTheme.colorScheme.background, modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("Fecha", "${liveTransaction.date}, ${liveTransaction.time}")
                        HorizontalDivider(color = MaterialTheme.colorScheme.background, modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("Método", liveTransaction.method)
                        HorizontalDivider(color = MaterialTheme.colorScheme.background, modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("Referencia", liveTransaction.reference)
                    }
                }

                // --- 3. LÍNEA DE TIEMPO ---
                BovedaCard {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Historial de la operación", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 16.dp))
                        liveTransaction.timeline.forEach { event ->
                            Row(modifier = Modifier.padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(if(event.done) Emerald500 else Slate400, CircleShape))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(event.status, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text(event.time, fontSize = 12.sp, color = Slate400)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { navigator.popUntilRoot() },
                    modifier = Modifier.fillMaxWidth().height(80.dp).padding(bottom = 30.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text("Volver al Inicio", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    @Composable
    private fun DetailRow(label: String, value: String) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Slate400, fontSize = 14.sp)
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}