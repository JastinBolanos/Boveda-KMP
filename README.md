# 🏦 Bóveda KMP | Enterprise-Grade Fintech Architecture

> **Arquitectura Fintech de Alta Seguridad, Disponibilidad Offline y Reactividad en Tiempo Real.**

Bóveda KMP no es solo una interfaz de banco multiplataforma. Es un sistema financiero diseñado con principios de ingeniería de nivel empresarial para garantizar que **el dinero nunca se duplique ni se pierda en el limbo**, incluso cuando las transacciones ocurren en zonas con conectividad intermitente o nula.

## 🔐 Pilares Arquitectónicos

* **Idempotencia Transaccional:** Generación de UUIDs locales únicos para cada intención de transferencia. Previene el "doble cobro" si la red fluctúa y la petición se dispara múltiples veces; el servidor identifica el UUID e ignora los duplicados protegiendo los fondos.
* **Sincronización Híbrida (Foreground + Fallback):** Estrategia de doble capa. Intento de envío inmediato si hay red activa; si falla (Offline-First), la operación se encola en SQLite (`PENDING`) y el `WorkManager` asume el control silencioso en segundo plano.
* **Reactividad Pura (Single Source of Truth):** La navegación entre pantallas nunca transfiere objetos "muertos". Las vistas reciben únicamente IDs (`Strings`) y se suscriben a la base de datos (StateFlow) para auto-actualizarse en vivo (ej. un recibo que cambia de Ámbar a Verde mágicamente frente al usuario).
* **MVI (Model-View-Intent) & Unidirectional Data Flow:** Las vistas en Compose son estrictamente "tontas" (Stateless). Toda mutación de estado pasa por un `ScreenModel` que actúa como Reducer, garantizando un flujo predecible y libre de side-effects.
* **Clean Architecture Estricta:** Separación absoluta entre `Domain`, `Data` y `Presentation`. Modelos de dominio agnósticos al framework y contratos de plataforma (`expect`/`actual`) para aislar APIs nativas de iOS/Android.

## 🛠️ Stack Financiero (Tech Stack)

* **Framework:** Kotlin Multiplatform (KMP) para Android & iOS.
* **UI:** Compose Multiplatform (Material 3 + Transición a Sistema Premium).
* **Gestión de Estado:** Voyager (Navegación basada en IDs y ScreenModels).
* **Persistencia Local:** SQLDelight (Single Source of Truth transaccional).
* **Red & Conectividad:** Ktor Client (Captive Portal Ping) + Contratos de plataforma.
* **Background Processing:** WorkManager (Android) / BGTaskScheduler (iOS) vía `expect/actual`.

## 🗺️ Roadmap & Fase Actual

* ✅ **Fase 1:** Cimientos Clean Architecture y Modelado de Dominio (Entidades y Contratos).
* ✅ **Fase 2:** Sistema de Diseño (Tokens) e Inyección de Base de Datos Local.
* ✅ **Fase 3:** Implementación MVI, Optimistic UI y arquitectura estática.
* ✅ **Fase 4:** Refactorización, purga técnica y documentación de alta densidad (Tech Lead Standard).
* ✅ **Fase 5:** Motor de Sincronización (Background Sync Worker), Ping de Verdad y Navegación Reactiva.
* 🚧 **Fase 6: UI "God-Tier" y Rediseño Premium (Micro-interacciones, animaciones estilo Yape/Nubank, y ajuste de últimos "cables sueltos").** `[ESTAMOS AQUÍ]`

--- 