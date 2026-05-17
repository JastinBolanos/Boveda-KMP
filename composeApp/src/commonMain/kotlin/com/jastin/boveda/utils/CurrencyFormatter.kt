package com.jastin.boveda.utils

import kotlin.math.abs
import kotlin.math.round

fun formatMoney(amount: Double): String {
    // 1. Guardamos el signo
    val isNegative = amount < 0
    val absoluteAmount = abs(amount)

    // 2. Redondeamos matemáticamente a 2 decimales exactos
    val rounded = round(absoluteAmount * 100) / 100.0

    // 3. Separamos enteros de decimales
    val parts = rounded.toString().split(".")
    val integerPart = parts[0]

    // 4. Forzamos que siempre haya 2 ceros al final (ej: "0" -> "00", "9" -> "90")
    val decimalPart = if (parts.size > 1) parts[1].padEnd(2, '0') else "00"

    // 5. Armamos el texto final
    val sign = if (isNegative) "-" else ""
    return "S/ $sign$integerPart.$decimalPart"
}