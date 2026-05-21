package com.jastin.boveda

import android.os.Build

/*
 * =========================================================================
 * IDENTIFICADOR DE PLATAFORMA (ANDROID)
 * =========================================================================
 * Implementación 'actual' del contrato de plataforma definido en commonMain.
 * Arquitectónicamente, esta clase extrae metadatos específicos del hardware
 * o sistema operativo (en este caso, la versión del SDK de Android) y los
 * expone a la capa compartida de forma agnóstica.
 * * Útil para telemetría, analíticas o bifurcaciones de lógica condicional
 * en la UI sin contaminar el dominio con dependencias de [android.os].
 */
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()