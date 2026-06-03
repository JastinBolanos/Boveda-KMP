package com.jastin.boveda.presentation.screens.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
        val exceedsLimit = amountNum > 500.00
        val hasError = isOverdraft || exceedsLimit
        val hasTrailingDot = state.amount.endsWith(".")

        val emptyZeroColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        val defaultTextColor = MaterialTheme.colorScheme.onSurface

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
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        Text(
                            text = "Transferir",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(horizontal = 24.dp)
                        .fillMaxSize()
                ) {
                    // --- INPUT DE MONTOS ---
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "MONTO A ENVIAR",
                            fontSize = 12.sp,
                            color = Slate400,
                            fontWeight = FontWeight.Bold
                        )

                        BasicTextField(
                            value = state.amount,
                            onValueChange = { screenModel.onIntent(TransferIntent.UpdateAmount(it)) },
                            textStyle = TextStyle(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            enabled = !state.isLoading,
                            cursorBrush = SolidColor(Emerald500),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),

                            visualTransformation = VisualTransformation { text ->
                                if (text.text.isEmpty()) {
                                    TransformedText(
                                        text = AnnotatedString(
                                            "0",
                                            spanStyle = SpanStyle(color = emptyZeroColor)
                                        ),
                                        offsetMapping = object : OffsetMapping {
                                            override fun originalToTransformed(offset: Int): Int = 1
                                            override fun transformedToOriginal(offset: Int): Int = 0
                                        }
                                    )
                                } else {
                                    TransformedText(
                                        text = AnnotatedString(
                                            text = text.text,
                                            spanStyle = SpanStyle(color = if (hasError) Red500 else defaultTextColor)
                                        ),
                                        offsetMapping = OffsetMapping.Identity
                                    )
                                }
                            }
                        )

                        if (isOverdraft) {
                            Text("Saldo insuficiente", color = Red500, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        } else if (exceedsLimit) {
                            Text("El monto máximo por envío es de S/ 500.00", color = Red500, fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
                        enabled = !state.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = Emerald500,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // --- ACCIÓN DE EJECUCIÓN (SUBMIT) ---
                    BovedaButton(
                        text = "Confirmar Transferencia",
                        // Bloqueamos el botón si está cargando para evitar dobles envíos
                        enabled = amountNum > 0 && !hasError && !hasTrailingDot && state.recipient.isNotBlank() && !state.isLoading,
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