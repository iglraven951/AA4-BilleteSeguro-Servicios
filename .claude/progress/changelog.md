# Changelog

## 2026-05-16 - Sistema de Notificaciones Toast (Observer Pattern)

### Sistema de Notificaciones Global
- Added: `static/js/toast.js` - Sistema de notificaciones toast reutilizable
- Added: Estilos CSS para toast notifications en style.css
- Added: Script toast.js a TODAS las paginas (index, cuentas, operaciones, movimientos, intereses, demos)
- Added: Enlace "Demos en Vivo" con badge LIVE en navegacion de TODAS las paginas

### Notificaciones en Operaciones Bancarias
- Modified: operaciones.html - Notificaciones toast en depositos, retiros y transferencias
- Added: Notificaciones en cascada mostrando observers (NotificacionObserver, AuditoriaObserver)
- Added: Alerta de fraude automatica cuando monto > S/.10,000
- Added: Animaciones slide-in/slide-out con progress bar

### Integracion con demos.html
- Modified: Simulador Observer ahora muestra toast notifications globales
- Added: Doble feedback visual (feed inline + toast global)

## 2026-05-16 - Demos Interactivas de Patrones (Pagina Dedicada)

### Nueva Pagina de Demos Interactivas
- Added: `demos.html` - Pagina separada dedicada a demos en tiempo real
- Added: Ruta `/demos` en VistaController.java
- Added: Enlace "Demos en Vivo" con badge LIVE en navegacion lateral del Dashboard
- Removed: Seccion de demos interactivas duplicada de index.html (ahora solo en demos.html)
- Removed: JavaScript de demos de index.html (movido a demos.html)

### Nuevos Endpoints Interactivos
- `POST /api/patrones/interactivo/calcular-interes` - Calculadora Strategy en vivo
- `POST /api/patrones/interactivo/circuit-breaker/simular-fallo` - Simula fallos
- `POST /api/patrones/interactivo/circuit-breaker/resetear` - Resetea estado
- `GET /api/patrones/interactivo/circuit-breaker/estado` - Obtiene estado actual
- `POST /api/patrones/interactivo/observer/simular` - Simula movimientos con notificaciones
- `GET /api/patrones/interactivo/health` - Health check simplificado

### Caracteristicas de demos.html
- Added: Calculadora de Interes visual con Strategy Pattern
- Added: Monitor de Circuit Breaker con indicador visual (verde/rojo/amarillo)
- Added: Simulador de Observer con notificaciones en tiempo real
- Added: Health Check en vivo con auto-refresh cada 3 segundos
- Added: JavaScript para interacciones asincronas con la API
- Added: Logs visuales para Circuit Breaker
- Added: Seccion de referencia de API

## 2026-05-15 - Visualizacion de Patrones en Dashboard
- Added: Seccion visual "Patrones de Diseño" en index.html
- Added: Navegacion lateral "Arquitectura AA4" con enlace a patrones
- Added: Cards visuales para SOLID (5 principios con colores distintos)
- Added: Cards para Patrones Creacionales (Factory, Builder, Singleton)
- Added: Cards para Patrones de Comportamiento (Strategy, Observer, Template)
- Added: Cards para Patrones de Microservicios (5 patrones)
- Added: Enlaces a API endpoints de demostracion
- Added: Efectos hover en API links y pattern cards

## 2026-05-15 - Patrones de Diseño y SOLID (AA4)
- Added: 3 Patrones Creacionales (Factory, Builder, Singleton)
- Added: 3 Patrones de Comportamiento (Strategy, Observer, Template Method)
- Added: 5 Patrones de Microservicios (API Gateway, DTO, Circuit Breaker, Rate Limiter, Health Check)
- Added: Interfaces SOLID (OperacionesLectura, OperacionesEscritura, ServicioBancario)
- Added: PatronesController.java con endpoints de demo
- Added: HealthController.java con health checks
- Added: PATRONES_IMPLEMENTADOS.md (documentacion para presentacion)
- Modified: BancoService.java implementa ServicioBancario
- Fixed: CuentaFactory.java soporte para todos los TipoCuenta

## 2026-04-03 - Project Memory Initialized
- Added: .claude/ directory structure
- Added: Auto-generated project documentation
