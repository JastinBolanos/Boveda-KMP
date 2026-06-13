<p align="center">
  <img src="docs/assets/logo_transparente.png" width="120" alt="Bóveda Logo">
</p>

<h1 align="center">🏦 Bóveda KMP | Enterprise-Grade Fintech Architecture</h1>

> **Arquitectura Fintech de Alta Seguridad, Disponibilidad Offline y Reactividad en Tiempo Real.**

Bóveda KMP no es solo una interfaz de banco multiplataforma. Es un sistema financiero diseñado con principios de ingeniería de nivel empresarial para garantizar que **el dinero nunca se duplique ni se pierda en el limbo**, incluso cuando las transacciones ocurren en zonas con conectividad intermitente o nula.

---

## 📱 Interfaz Premium y UX
Diseño orientado a la fluidez transaccional, integrando micro-interacciones de estado, teclados nativizados y soporte total para Dark/Light Mode.

<p align="center">
  <img src="docs/assets/home_dark.jpg" width="250" alt="Pantalla de Inicio Oscura">
  &nbsp;&nbsp;
  <img src="docs/assets/transfer_dark.jpg" width="250" alt="Teclado de Transferencia">
  &nbsp;&nbsp;
  <img src="docs/assets/receipt_success.jpg" width="250" alt="Recibo Exitoso">
</p>

<p align="center">
  <img src="docs/assets/home_light.jpg" width="250" alt="Pantalla de Inicio Clara">
  &nbsp;&nbsp;
  <img src="docs/assets/activity_dark.jpg" width="250" alt="Historial de Actividad">
  &nbsp;&nbsp;
  <img src="docs/assets/receipt_pending.jpg" width="250" alt="Recibo Pendiente Offline">
</p>

---

## 🔐 Pilares Arquitectónicos

* **Idempotencia Transaccional:** Generación de UUIDs locales únicos para cada intención de transferencia. Previene el "doble cobro" si la red fluctúa y la petición se dispara múltiples veces; el servidor identifica el UUID e ignora los duplicados protegiendo los fondos.
* **Sincronización Híbrida (Foreground + Fallback):** Estrategia de doble capa. Intento de envío inmediato si hay red activa; si falla (Offline-First), la operación se encola en SQLite (`PENDING`) y el `WorkManager` asume el control silencioso en segundo plano.
* **Reactividad Pura (Single Source of Truth):** La navegación entre pantallas nunca transfiere objetos "muertos". Las vistas reciben únicamente IDs (`Strings`) y se suscriben a la base de datos (StateFlow) para auto-actualizarse en vivo (ej. un recibo que cambia de Ámbar a Verde mágicamente frente al usuario).
* **MVI (Model-View-Intent) & Unidirectional Data Flow:** Las vistas en Compose son estrictamente "tontas" (Stateless). Toda mutación de estado pasa por un `ScreenModel` que actúa como Reducer, garantizando un flujo predecible y libre de side-effects.
* **Clean Architecture Estricta:** Separación absoluta entre `Domain`, `Data` y `Presentation`. Modelos de dominio agnósticos al framework y contratos de plataforma (`expect`/`actual`) para aislar APIs nativas de iOS/Android.

---

## 🏗 Flujo de Sincronización y Datos

```mermaid
sequenceDiagram
    participant UI as TransferScreenModel
    participant DB as SQLite (SQLDelight)
    participant Worker as Sync Worker
    participant API as Backend (Ktor)

    UI->>DB: Guarda Transacción (Estado: PENDING)
    DB-->>UI: StateFlow repinta UI a "Ámbar ⏳"
    Worker->>Worker: Detecta conexión a Internet
    Worker->>DB: Lee transacciones PENDING
    Worker->>API: HTTP POST /sync (Idempotencia)
    API-->>Worker: 200 OK
    Worker->>DB: Actualiza a Estado: COMPLETED
    DB-->>UI: StateFlow repinta UI a "Verde ✅"