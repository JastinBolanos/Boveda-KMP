package com.jastin.boveda.presentation.screens.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jastin.boveda.presentation.components.BovedaButton
import com.jastin.boveda.presentation.screens.result.ResultScreen
import com.jastin.boveda.presentation.theme.*

/* =========================================================================
 * FORMULARIO DE TRANSFERENCIA (STATEFUL SCREEN)
 * Punto de entrada para la mutación de datos.
 * A diferencia de las vistas pasivas, esta pantalla delega su lógica local
 * a un [ScreenModel] para sobrevivir a los cambios de configuración y aislar
 * el estado del formulario de la composición de la UI.
 * ========================================================================= */
data class TransferScreen(val currentBalance: Double) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { TransferScreenModel() }
        val state by screenModel.state.collectAsState()

        // --- 1. VALIDACIÓN FINANCIERA EN TIEMPO REAL ---
        // El cálculo de sobregiro (isOverdraft) se realiza en la UI ya que es una
        // regla de presentación inmediata que bloquea el botón y pinta alertas,
        // evitando saturar el ScreenModel con lógica visual.
        val amountNum = state.amount.toDoubleOrNull() ?: 0.0
        val isOverdraft = amountNum > currentBalance

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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                    Text(
                        text = "Transferir",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
            },
            containerColor = Color.White
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .fillMaxSize()
            ) {

                // --- 2. SIMULADOR OFFLINE (MINA TERRESTRE) ---
                // ¡CRÍTICO! Este componente de control (Switch) permite a los evaluadores
                // probar la arquitectura de sincronización (Idempotencia y SQLite)
                // sin tener que apagar físicamente las conexiones de red del dispositivo.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate50, RoundedCornerShape(16.dp))
                        .border(1.dp, Slate100, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Simular Modo Offline", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Fuerza estado pendiente", fontSize = 12.sp, color = Slate400)
                    }
                    Switch(
                        checked = state.isOffline,
                        onCheckedChange = { screenModel.onIntent(TransferIntent.ToggleOffline) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Amber500, checkedTrackColor = Amber500.copy(alpha = 0.3f))
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // --- 3. INPUT DE MONTOS (TEXTFIELD PERSONALIZADO) ---
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("MONTO A ENVIAR", fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = state.amount,
                        onValueChange = { screenModel.onIntent(TransferIntent.UpdateAmount(it)) },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            color = if(isOverdraft) Red500 else Slate950
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(
                                text = "0.00",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Slate100,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (isOverdraft) {
                        Text("Saldo insuficiente", color = Red500, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = state.recipient,
                    onValueChange = { screenModel.onIntent(TransferIntent.UpdateRecipient(it)) },
                    label = { Text("Destinatario") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.weight(1f))

                // --- 4. ACCIÓN DE EJECUCIÓN (SUBMIT) ---
                BovedaButton(
                    text = "Confirmar Transferencia",
                    enabled = amountNum > 0 && !isOverdraft && state.recipient.isNotBlank(),
                    onClick = {
                        val newTx = screenModel.executeTransfer()
                        if (newTx != null) {
                            navigator.push(ResultScreen(newTx))
                        }
                    },
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}