package com.jastin.boveda

/* =========================================================================
 * CONTRATO DE PLATAFORMA (EXPECT)
 * Abstracción base para identificar el sistema operativo anfitrión.
 * Permite a la capa compartida (commonMain) tomar decisiones de lógica,
 * renderizado o analíticas basadas en el entorno (iOS/Android) sin
 * acoplarse a librerías nativas.
 * ========================================================================= */
interface Platform {
    val name: String
}

expect fun getPlatform(): Platform