# Sistema Bancario - Patrones de Diseño y SOLID

## Resumen para Presentacion AA4

---

## PRINCIPIOS SOLID IMPLEMENTADOS (5/5)

### S - Single Responsibility Principle
**Cada clase tiene una sola responsabilidad:**
| Clase | Responsabilidad Unica |
|-------|----------------------|
| `BancoService` | Logica de negocio bancaria |
| `CuentaRepository` | Acceso a datos de cuentas |
| `CuentaFactory` | Creacion de cuentas |
| `MovimientoBuilder` | Construccion de movimientos |
| `NotificacionObserver` | Envio de notificaciones |

**Archivo de referencia:** `BancoService.java` lineas 17-55

---

### O - Open/Closed Principle
**Abierto a extension, cerrado a modificacion:**
- `InteresStrategy`: Agregar nueva estrategia sin modificar existentes
- `MovimientoObserver`: Agregar observadores sin cambiar Subject
- `OperacionBancariaTemplate`: Nuevas operaciones extendiendo

**Archivo de referencia:** `InteresStrategy.java` y sus implementaciones

---

### L - Liskov Substitution Principle
**Objetos sustituibles por sus subtipos:**
- Cualquier `InteresStrategy` es intercambiable
- `BancoService` puede reemplazarse por cualquier `ServicioBancario`
- Observers intercambiables en `MovimientoSubject`

**Archivo de referencia:** `ServicioBancario.java`

---

### I - Interface Segregation Principle
**Interfaces especificas, no generales:**
| Interfaz | Operaciones |
|----------|-------------|
| `OperacionesLectura` | GET/Consultas |
| `OperacionesEscritura` | POST/Modificaciones |
| `ServicioBancario` | Combina ambas |

**Archivos:** `OperacionesLectura.java`, `OperacionesEscritura.java`, `ServicioBancario.java`

---

### D - Dependency Inversion Principle
**Depender de abstracciones, no implementaciones:**
- `BancoService` depende de interfaces Repository
- `@Autowired` para inyeccion de dependencias
- Constructor recibe interfaces, no clases concretas

**Archivo de referencia:** `BancoService.java` constructor lineas 73-84

---

## PATRONES CREACIONALES (3)

### 1. Factory Method
**Clase:** `CuentaFactory`
**Ubicacion:** `com.trabajo.api.pattern.creational`
**Proposito:** Crea diferentes tipos de cuentas (AHORRO, CORRIENTE, PLAZO_FIJO, SUELDO) con configuraciones especificas.

```java
Cuenta cuenta = cuentaFactory.crearCuenta(TipoCuenta.AHORRO, "Juan", "12345678");
```

**Beneficios:**
- Encapsula logica de creacion
- Facilita agregar nuevos tipos (OCP)
- Centraliza validacion inicial

---

### 2. Builder
**Clase:** `MovimientoBuilder`
**Ubicacion:** `com.trabajo.api.pattern.creational`
**Proposito:** Construye objetos Movimiento paso a paso con API fluida.

```java
Movimiento mov = movimientoBuilder.nuevo()
    .conCuenta(cuenta)
    .conTipo(TipoMovimiento.DEPOSITO)
    .conMonto(500.0)
    .conDescripcion("Deposito mensual")
    .build();
```

**Beneficios:**
- Construccion paso a paso
- API fluida y legible
- Validacion en build()

---

### 3. Singleton
**Clase:** `ServiceLocator` + Spring `@Service`
**Ubicacion:** `com.trabajo.api.pattern.creational`
**Proposito:** Una sola instancia de cada servicio en la aplicacion.

**Beneficios:**
- Control de instancias unicas
- Acceso centralizado a servicios
- Spring gestiona automaticamente

---

## PATRONES DE COMPORTAMIENTO (3)

### 1. Strategy
**Interface:** `InteresStrategy`
**Implementaciones:** 
- `InteresAhorroStrategy` (3% anual)
- `InteresCorrienteStrategy` (0% anual)
- `InteresPlazoFijoStrategy` (8% anual + bonificacion)

**Ubicacion:** `com.trabajo.api.pattern.behavioral`

```java
InteresStrategy strategy = interesStrategyFactory.getStrategy(TipoCuenta.AHORRO);
double interes = strategy.calcularInteres(10000, 365);
```

**Beneficios:**
- Algoritmos intercambiables
- Facil agregar nuevas estrategias
- Elimina condicionales complejos

---

### 2. Observer
**Subject:** `MovimientoSubject`
**Observers:**
- `NotificacionObserver` (envia notificaciones)
- `AuditoriaObserver` (registra auditoria)
- `FraudeObserver` (detecta fraude >10,000)

**Ubicacion:** `com.trabajo.api.pattern.behavioral`

```java
movimientoSubject.notificarMovimiento(movimiento);
// Todos los observers son notificados automaticamente
```

**Beneficios:**
- Desacoplamiento subject-observers
- Agregar observers sin modificar subject
- Notificacion automatica

---

### 3. Template Method
**Clase abstracta:** `OperacionBancariaTemplate`
**Implementaciones:**
- `DepositoOperacion`
- `RetiroOperacion`

**Ubicacion:** `com.trabajo.api.pattern.behavioral`

**Estructura del Template:**
1. `validarCuenta()`
2. `validarMonto()`
3. `validacionEspecifica()` *[ABSTRACTO]*
4. `realizarOperacion()` *[ABSTRACTO]*
5. `registrarOperacion()`

**Beneficios:**
- Estructura comun reutilizable
- Subclases solo implementan pasos especificos
- Evita duplicacion de codigo

---

## PATRONES DE MICROSERVICIOS (5)

### 1. API Gateway
**Clase:** `PatronesController`, `BancoController`, `HealthController`
**Proposito:** Punto de entrada centralizado para la API.

**Endpoints disponibles:**
- `/api/cuentas` - Gestion de cuentas
- `/api/operaciones` - Operaciones bancarias
- `/api/patrones` - Demo de patrones
- `/health` - Health checks

---

### 2. DTO (Data Transfer Object)
**Clase:** `ApiResponse<T>`
**Ubicacion:** `com.trabajo.api.pattern.microservices`
**Proposito:** Respuestas estandarizadas de la API.

```java
{
    "success": true,
    "data": { ... },
    "message": "Operacion exitosa",
    "timestamp": "2024-..."
}
```

---

### 3. Circuit Breaker
**Clase:** `CircuitBreaker`
**Ubicacion:** `com.trabajo.api.pattern.microservices`

**Estados:**
| Estado | Descripcion |
|--------|-------------|
| CERRADO | Normal - llamadas pasan |
| ABIERTO | Fallo - llamadas rechazadas |
| SEMI_ABIERTO | Prueba - algunas pasan |

**Configuracion:**
- Umbral: 5 fallos consecutivos
- Tiempo recuperacion: 30 segundos

**Beneficios:**
- Previene fallas en cascada
- Recuperacion automatica
- Fail-fast cuando hay problemas

---

### 4. Rate Limiter
**Clase:** `RateLimiter`
**Algoritmo:** Token Bucket
**Ubicacion:** `com.trabajo.api.pattern.microservices`

**Configuracion:**
- 100 solicitudes por minuto por cliente
- Tokens se recargan automaticamente

**Beneficios:**
- Protege contra sobrecarga
- Previene ataques DDoS
- Fair usage de recursos

---

### 5. Health Check
**Clase:** `HealthCheck`
**Ubicacion:** `com.trabajo.api.pattern.microservices`

**Endpoints Kubernetes-style:**
| Endpoint | Proposito |
|----------|-----------|
| `/health` | Estado detallado |
| `/health/live` | Liveness probe |
| `/health/ready` | Readiness probe |

**Checks realizados:**
- Base de datos activa
- Memoria disponible (>10%)
- Componentes criticos

---

## ESTRUCTURA DE ARCHIVOS

```
src/main/java/com/trabajo/api/
├── controller/
│   ├── BancoController.java      # API principal
│   ├── HealthController.java     # Health endpoints
│   └── PatronesController.java   # Demo patrones
│
├── model/
│   ├── Cuenta.java               # Entidad JPA
│   ├── Movimiento.java           # Entidad JPA
│   ├── TipoCuenta.java           # Enum
│   ├── TipoMovimiento.java       # Enum
│   └── EstadoCuenta.java         # Enum
│
├── repository/
│   ├── CuentaRepository.java     # JpaRepository
│   └── MovimientoRepository.java # JpaRepository
│
├── service/
│   ├── BancoService.java         # Implementacion
│   └── interfaces/
│       ├── OperacionesLectura.java   # SOLID - I
│       ├── OperacionesEscritura.java # SOLID - I
│       └── ServicioBancario.java     # SOLID - D
│
└── pattern/
    ├── creational/
    │   ├── CuentaFactory.java        # Factory Method
    │   ├── MovimientoBuilder.java    # Builder
    │   └── ServiceLocator.java       # Singleton
    │
    ├── behavioral/
    │   ├── InteresStrategy.java          # Strategy
    │   ├── InteresAhorroStrategy.java
    │   ├── InteresCorrienteStrategy.java
    │   ├── InteresPlazoFijoStrategy.java
    │   ├── InteresStrategyFactory.java
    │   ├── MovimientoObserver.java       # Observer
    │   ├── NotificacionObserver.java
    │   ├── AuditoriaObserver.java
    │   ├── FraudeObserver.java
    │   ├── MovimientoSubject.java
    │   ├── OperacionBancariaTemplate.java # Template Method
    │   ├── DepositoOperacion.java
    │   └── RetiroOperacion.java
    │
    └── microservices/
        ├── ApiResponse.java          # DTO
        ├── CircuitBreaker.java       # Circuit Breaker
        ├── RateLimiter.java          # Rate Limiter
        └── HealthCheck.java          # Health Check
```

---

## ENDPOINTS DE DEMOSTRACION

### Resumen de patrones
```
GET http://localhost:8080/api/patrones
```

### Demo Factory Method
```
GET http://localhost:8080/api/patrones/demo/factory
```

### Demo Builder
```
GET http://localhost:8080/api/patrones/demo/builder
```

### Demo Strategy
```
GET http://localhost:8080/api/patrones/demo/strategy
```

### Demo Observer
```
POST http://localhost:8080/api/patrones/demo/observer?monto=15000
```

### Demo Template Method
```
GET http://localhost:8080/api/patrones/demo/template
```

### Demo Circuit Breaker
```
GET http://localhost:8080/api/patrones/demo/circuit-breaker
```

### Demo Rate Limiter
```
GET http://localhost:8080/api/patrones/demo/rate-limiter
```

### Demo SOLID
```
GET http://localhost:8080/api/patrones/solid
```

---

## COMO EJECUTAR

```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# Acceder
http://localhost:8080/api/patrones
```

---

## RESUMEN PARA RUBRICA

| Criterio | Puntos | Implementado |
|----------|--------|--------------|
| **SOLID** | 4 | S, O, L, I, D - Todos con justificacion |
| **Patrones Creacionales** | 4 | Factory, Builder, Singleton |
| **Patrones Comportamiento** | 4 | Strategy, Observer, Template Method |
| **Patrones Microservicios** | 4 | API Gateway, DTO, Circuit Breaker, Rate Limiter, Health Check |
| **Presentacion clara** | 4 | Endpoints de demo + documentacion |

**Total esperado: 20/20 puntos (Sobresaliente)**
