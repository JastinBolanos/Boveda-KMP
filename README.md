
---

# 🏦 Bóveda KMP | Enterprise-Grade Fintech Architecture

> **Arquitectura Fintech de Alta Seguridad y Disponibilidad Offline.**

Bóveda KMP no es solo una interfaz de banco multiplataforma. Es un sistema financiero diseñado con principios de ingeniería de nivel empresarial para garantizar que **el dinero nunca se duplique ni se pierda en el limbo**, incluso cuando las transacciones ocurren en zonas con conectividad intermitente o nula.

## 🔐 Pilares Arquitectónicos

* **Idempotencia Transaccional:** Generación de UUIDs locales únicos para cada intención de transferencia. Previene el "doble cobro" si la red fluctúa y la petición se dispara múltiples veces; el servidor identifica el UUID e ignora los duplicados protegiendo los fondos.
* **Optimistic UI & Cola Offline (Offline-First):** Las transferencias sin red actualizan el saldo visual de inmediato (bloqueando el capital para evitar sobregiros) y se encolan en la base de datos local SQLite bajo un estado transitorio `PENDING`.
* **MVI (Model-View-Intent) & Unidirectional Data Flow:** Las vistas en Compose son estrictamente "tontas" (Stateless). Toda mutación de estado pasa por un `ScreenModel` que actúa como Reducer, garantizando un flujo de datos predecible y libre de side-effects en la UI.
* **Time To Live (TTL):** Mecanismo de caducidad de negocio. Si el dispositivo no recupera conexión en un periodo máximo, la transacción local expira (`EXPIRED`), revirtiendo el saldo de forma atómica y segura.
* **Clean Architecture Estricta:** Separación absoluta entre `Domain`, `Data` y `Presentation`. Modelos de dominio agnósticos al framework y contratos de plataforma (`expect`/`actual`) para aislar APIs nativas de iOS/Android.

## 🛠️ Stack Financiero (Tech Stack)

* **Framework:** Kotlin Multiplatform (KMP) para Android & iOS.
* **UI:** Compose Multiplatform (Material 3 + Design Tokens personalizados).
* **Gestión de Estado:** Voyager (Navegación y ScreenModels).
* **Persistencia Local:** SQLDelight (Single Source of Truth transaccional).
* **Inyección de Dependencias:** Service Locator Pattern (Evitando sobre-ingeniería en fases tempranas).
* **Red:** Ktor Client *(Próxima integración)*.

## 🗺️ Roadmap & Fase Actual

* ✅ **Fase 1:** Cimientos Clean Architecture y Modelado de Dominio (Entidades y Contratos).
* ✅ **Fase 2:** Sistema de Diseño (Tokens) e Inyección de Base de Datos Local.
* ✅ **Fase 3:** Implementación MVI, Optimistic UI y Simulador Offline.
* ✅ **Fase 4:** Refactorización, purga técnica y documentación de alta densidad (Tech Lead Standard).
* 🚧 **Fase 5: Motor de Sincronización (Background Sync Worker) y Conexión de Red.** `[ESTAMOS AQUÍ]`

---
