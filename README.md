# 🏦 Bóveda KMP

> **Arquitectura Fintech de Alta Seguridad y Disponibilidad Offline.**

Bóveda KMP no es solo una interfaz de banco más. Es un sistema financiero diseñado con principios de ingeniería de nivel empresarial para garantizar que **el dinero nunca se duplique ni se pierda en el limbo**, incluso cuando las transacciones ocurren en zonas sin conexión a internet.

## 🔐 Pilares Arquitectónicos

* **Idempotencia desde el Cliente:** Generación de UUIDs locales únicos para cada intención de transferencia. Si la red parpadea y la petición se dispara dos veces, el servidor identifica el UUID, ignora el duplicado y protege los fondos del usuario.
* **Optimistic UI & Cola Offline:** Las transferencias sin red actualizan el saldo visual de inmediato (para evitar que el usuario gaste dinero que ya comprometió) y se encolan en la base de datos local con estado `PENDIENTE`.
* **Time To Live (TTL):** Un mecanismo de caducidad automático. Si el dispositivo no recupera conexión en 24 horas, la transacción local expira, revirtiendo el saldo visual de forma segura.
* **Clean Architecture:** Separación absoluta entre `Domain`, `Data` y `Presentation`, garantizando un código testeable, mantenible y listo para escalar.

## 🛠️ Tech Stack (Stack Financiero)

* **Framework:** Kotlin Multiplatform (Android & iOS)
* **UI:** Compose Multiplatform
* **Inyección de Dependencias:** Koin
* **Red:** Ktor Client (Llamadas asíncronas seguras)
* **Base de Datos Local:** SQLDelight (Almacenamiento offline de la cola transaccional)
* **Navegación:** Voyager

---
*🚧 Fase actual: Cimientos Clean Architecture y Modelado de Dominio Transaccional (Entidades y UUID).*