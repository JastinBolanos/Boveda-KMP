package com.jastin.boveda.utils

import kotlin.math.abs
import kotlin.math.round

/* =========================================================================
 * UTILIDAD DE FORMATEO FINANCIERO (PURE FUNCTION)
 * Centraliza la lógica de presentación de divisas. Al ser una función pura,
 * garantiza que las vistas (UI) permanezcan agnósticas a la lógica de strings.
 * ========================================================================= */
fun formatMoney(amount: Double): String {
    val isNegative = amount < 0
    val absoluteAmount = abs(amount)

    val rounded = round(absoluteAmount * 100) / 100.0
    val parts = rounded.toString().split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) parts[1].padEnd(2, '0') else "00"

    val sign = if (isNegative) "-" else ""

    // BLINDAJE VISUAL: Colocamos el signo antes del símbolo de la moneda
    // Si hay un espacio extra por el signo, usamos trim() para limpiarlo si es positivo.
    return ("$sign S/ $integerPart.$decimalPart").trim()
}