package com.jastin.boveda.utils

import kotlin.math.abs
import kotlin.math.round

/* =========================================================================
 * UTILIDAD DE FORMATEO FINANCIERO (PURE FUNCTION)
 * Centraliza la lógica matemática para la presentación de divisas.
 * Arquitectura: Al ser una función pura y aislada, previene que las vistas
 * (Compose) o los modelos de datos asuman responsabilidades de parseo de strings.
 * ========================================================================= */
fun formatMoney(amount: Double): String {
    val isNegative = amount < 0
    val absoluteAmount = abs(amount)

    val rounded = round(absoluteAmount * 100) / 100.0
    val parts = rounded.toString().split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) parts[1].padEnd(2, '0') else "00"

    val sign = if (isNegative) "-" else ""
    return "S/ $sign$integerPart.$decimalPart"
}