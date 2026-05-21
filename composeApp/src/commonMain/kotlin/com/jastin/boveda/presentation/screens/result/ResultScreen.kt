package com.jastin.boveda.presentation.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jastin.boveda.presentation.components.BovedaButton
import com.jastin.boveda.presentation.model.TransactionUiModel
import com.jastin.boveda.presentation.model.TxUiStatus
import com.jastin.boveda.presentation.screens.detail.DetailScreen
import com.jastin.boveda.presentation.theme.*
import com.jastin.boveda.utils.BackPressHandler

/* =========================================================================
 * VOUCHER DE TRANSACCIÓN (RESULT SCREEN)
 * Renderiza el estado final (o pendiente) de una operación financiera.
 * Arquitectura de Navegación: Esta pantalla marca el fin de un flujo de usuario.
 * Debe ser hermética y no permitir que el usuario viaje "hacia atrás" para evitar
 * estados corruptos o dobles envíos en la capa de datos.
 * ========================================================================= */
data class ResultScreen(val transaction: TransactionUiModel) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val isSuccess = transaction.status == TxUiStatus.COMPLETED

        // --- 1. SEGURIDAD FINANCIERA (IDEMPOTENCIA DE UI) ---
        // ¡CRÍTICO! Destruimos la pila de navegación (backstack) si se usa el gesto físico "Atrás".
        // Esto previene el clásico bug donde el usuario vuelve al formulario, presiona
        // "Enviar" de nuevo, y duplica la mutación en SQLite/Backend.
        BackPressHandler {
            navigator.popUntilRoot()
        }

        // --- 2. GESTIÓN DEL THEME REACTIVO ---
        val backgroundColor = if (isSuccess) Slate950 else Amber500
        val contentColor = if (isSuccess) Color.White else Slate950

        Column(
            modifier = Modifier.fillMaxSize().background(backgroundColor).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // --- 3. HERO ICON (RETROALIMENTACIÓN INMEDIATA) ---
            Box(
                modifier = Modifier.size(120.dp).background(if (isSuccess) Emerald500 else Color.White, RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isSuccess) Slate950 else Amber500,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (isSuccess) "Transferencia\nExitosa" else "Transferencia\nEncolada",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isSuccess) {
                Text(
                    "Se enviará automáticamente al recuperar red",
                    color = Slate900,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- 4. ACCIONES DE CIERRE DE FLUJO ---
            Button(
                onClick = { navigator.popUntilRoot() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSuccess) Emerald500 else Slate950,
                    contentColor = if (isSuccess) Slate950 else Color.White
                )
            ) {
                Text("Volver al inicio", fontWeight = FontWeight.Bold)
            }

            if (isSuccess) {
                Spacer(modifier = Modifier.height(12.dp))
                BovedaButton(
                    text = "Ver detalles de operación",
                    onClick = { navigator.push(DetailScreen(transaction)) },
                    isGhost = true
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}