package com.jastin.boveda

/* =========================================================================
 * CONTRATO DE PLATAFORMA (EXPECT)
 * Abstracción para identificar el SO anfitrión (Android/iOS) y ejecutar
 * lógica específica de plataforma sin acoplamiento nativo.
 * ========================================================================= */
interface Platform {
    val name: String
}

expect fun getPlatform(): Platform