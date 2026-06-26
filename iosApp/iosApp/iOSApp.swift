import SwiftUI
import BackgroundTasks
import ComposeApp

@main
struct iOSApp: App {

    // BLINDAJE DE INFRAESTRUCTURA (iOS Lifecycle)
    init() {
        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: "com.jastin.boveda", // ⚠️ Asegúrate de que tu Info.plist diga exactamente esto
            using: nil
        ) { task in

            // 1. Instanciamos el Worker nativo que creaste en BovedaSyncWorker.ios.kt
            let worker = BovedaSyncWorker()

            // 2. Disparamos tu Caso de Uso (UseCase) de sincronización
            worker.enqueueSync()

            // 3. REGLA DE ORO DE APPLE: Avisar al SO que terminamos para liberar memoria
            task.setTaskCompleted(success: true)
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}