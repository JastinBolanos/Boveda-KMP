package com.jastin.boveda

import android.os.Build

/* =========================================================================
 * IDENTIFICADOR DE PLATAFORMA (ANDROID)
 * Implementación 'actual' que extrae metadatos del sistema (ej. SDK Version):
 * * Abstracción: Expone información del SO a la capa compartida de forma
 * agnóstica.
 * * Encapsulación: Permite lógica condicional basada en plataforma sin
 * contaminar el dominio con dependencias directas de [android.os].
 * ========================================================================= */
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()