package com.jastin.boveda.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.jastin.boveda.presentation.model.TransactionUiModel
import com.jastin.boveda.presentation.model.TxUiStatus
import com.jastin.boveda.presentation.theme.*
import com.jastin.boveda.utils.formatMoney
import kotlin.math.abs

data class DetailScreen(val transaction: TransactionUiModel) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.pop() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                    Text("Detalle de Operación", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.width(48.dp))
                }
            },
            containerColor = Slate50
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(horizontal = 24.dp).fillMaxSize()) {

                // Cabecera del detalle
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)) {
                    Text(if (transaction.amount > 0) "RECIBISTE" else "ENVIASTE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate400)
                    Text(formatMoney(abs(transaction.amount)), fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Slate950)
                    Text(transaction.title, fontSize = 16.sp, color = Slate800)
                }

                // Tarjeta de información principal
                BovedaCard(modifier = Modifier.padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        DetailRow("Estado", if (transaction.status == TxUiStatus.PENDING) "Pendiente" else "Completado")
                        HorizontalDivider(color = Slate50, modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("Fecha", "${transaction.date}, ${transaction.time}")
                        HorizontalDivider(color = Slate50, modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("Método", transaction.method)
                        HorizontalDivider(color = Slate50, modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("Referencia", transaction.reference)
                    }
                }

                // Tarjeta de Historial (Timeline)
                BovedaCard {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Historial de la operación", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                        transaction.timeline.forEach { event ->
                            Row(modifier = Modifier.padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(if(event.done) Emerald500 else Slate400, CircleShape))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(event.status, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Slate950)
                                    Text(event.time, fontSize = 12.sp, color = Slate400)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Componente privado solo para esta pantalla
    @Composable
    private fun DetailRow(label: String, value: String) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Slate400, fontSize = 14.sp)
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Slate950)
        }
    }
}