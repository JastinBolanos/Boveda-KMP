package com.jastin.boveda.presentation.screens.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.jastin.boveda.presentation.theme.*

/* =========================================================================
 * FORMULARIO DE TRANSFERENCIA (STATEFUL SCREEN)
 * ========================================================================= */
data class TransferScreen(val currentBalance: Double) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { TransferScreenModel() }
        val state by screenModel.state.collectAsState()

        val amountNum = state.amount.toDoubleOrNull() ?: 0.0
        val isOverdraft = amountNum > currentBalance

        LaunchedEffect(state.successTransactionId) {
            state.successTransactionId?.let { txId ->
                screenModel.onIntent(TransferIntent.ClearNavigation)
                navigator.replace(com.jastin.boveda.presentation.screens.detail.DetailScreen(txId))
            }
        }

        // Envolvemos todo en un Box para poder superponer la pantalla de carga
        Box(modifier = Modifier.fillMaxSize()) {

            Scaffold(
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navigator.pop() },
                            enabled = !state.isLoading
                        ) {
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
                    // --- INPUT DE MONTOS ---
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
                            enabled = !state.isLoading, // Deshabilitar si carga
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent
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
                        singleLine = true,
                        enabled = !state.isLoading
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // --- ACCIÓN DE EJECUCIÓN (SUBMIT) ---
                    BovedaButton(
                        text = "Confirmar Transferencia",
                        // Bloqueamos el botón si está cargando para evitar dobles envíos
                        enabled = amountNum > 0 && !isOverdraft && state.recipient.isNotBlank() && !state.isLoading,
                        onClick = {
                            screenModel.executeTransfer()
                        },
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }

            // --- OVERLAY DE CARGA ---
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Emerald500,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Procesando pago...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}