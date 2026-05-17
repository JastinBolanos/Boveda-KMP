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

data class TransferScreen(val currentBalance: Double) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { TransferScreenModel() }
        val state by screenModel.state.collectAsState()

        val amountNum = state.amount.toDoubleOrNull() ?: 0.0
        val isOverdraft = amountNum > currentBalance

        Scaffold(
            topBar = {
                // 1. APLICAMOS STATUS BAR PADDING AQUÍ
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
                // Offline Toggle
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

                // Amount Input
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
                        // 2. CORRECCIÓN DEL PLACEHOLDER
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

                // Recipient Input
                OutlinedTextField(
                    value = state.recipient,
                    onValueChange = { screenModel.onIntent(TransferIntent.UpdateRecipient(it)) },
                    label = { Text("Destinatario") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.weight(1f))

                // Confirm Button
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