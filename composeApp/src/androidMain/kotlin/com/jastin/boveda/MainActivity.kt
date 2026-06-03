package com.jastin.boveda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jastin.boveda.database.DatabaseDriverFactory
import com.jastin.boveda.utils.AndroidPlatformContext

/* =========================================================================
 * HOST NATIVO DE LA APLICACIÓN (ANDROID)
 * Punto de entrada base para el sistema de interfaz compartida (Compose MP):
 * * Frontera del Sistema: Límite arquitectónico donde reside la interacción
 * directa con el SO.
 * * Inyección de Dependencias: Captura y provee él [Context] al dominio
 * agnóstico, facilitando la inicialización de la persistencia (SQL).
 * ========================================================================= */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AndroidPlatformContext.applicationContext = this.applicationContext

        setContent {
            App(driverFactory = DatabaseDriverFactory(this))
        }
    }
}

/* =========================================================================
 * NOTA ARQUITECTÓNICA: LIMITACIONES DE PREVIEW
 * =========================================================================
 * La vista previa de la raíz está deshabilitada: el motor de Compose opera
 * en un entorno estático (Mock) sin acceso al Context o al sistema de archivos.
 * * * Implicación: Intentar instanciar la jerarquía completa dispara errores
 * de acceso a SQLite.
 * * Estrategia: Las pruebas visuales deben limitarse a componentes
 * presentacionales (Dumb) aislados de dependencias de persistencia.
 */
@Preview
@Composable
fun AppAndroidPreview() {
}