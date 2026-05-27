package com.jastin.boveda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jastin.boveda.database.DatabaseDriverFactory
import com.jastin.boveda.utils.AndroidPlatformContext

/*
 * =========================================================================
 * PUNTO DE ENTRADA NATIVO (ANDROID HOST)
 * =========================================================================
 * Actúa como el contenedor base para la interfaz gráfica compartida (Compose Multiplatform).
 * Arquitectónicamente, este es el límite del sistema (System Boundary), el único
 * lugar donde está permitido interactuar directamente con el SO. Aquí capturamos
 * el Context de Android y lo inyectamos hacia el dominio agnóstico de [App]
 * para que pueda construir la base de datos física.
 */
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

/**
 * =========================================================================
 * NOTA ARQUITECTÓNICA SOBRE PREVIEWS
 * =========================================================================
 * La vista previa de la app completa se mantiene intencionalmente vacía.
 * El motor de Compose Preview opera en un entorno de pruebas estático (Mock) que
 * carece de un Context de aplicación real. Al requerir acceso al disco duro
 * para SQLite, intentar renderizar la raíz causará un fallo de compilación gráfica.
 * Las pruebas visuales deben realizarse sobre componentes visuales aislados (Dumb Components).
 */
@Preview
@Composable
fun AppAndroidPreview() {
}