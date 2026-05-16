package com.trabajo.api.controller;

import com.trabajo.api.model.*;
import com.trabajo.api.pattern.behavioral.*;
import com.trabajo.api.pattern.creational.*;
import com.trabajo.api.pattern.microservices.*;
import com.trabajo.api.service.interfaces.ServicioBancario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * ============================================================================
 * CONTROLADOR DE DEMOSTRACION DE PATRONES DE DISEÑO
 * ============================================================================
 *
 * Este controlador expone endpoints para demostrar la implementacion de:
 *
 * PATRONES CREACIONALES (3):
 * 1. Factory Method - CuentaFactory
 * 2. Builder - MovimientoBuilder
 * 3. Singleton - ServiceLocator (+ Spring @Service)
 *
 * PATRONES DE COMPORTAMIENTO (3):
 * 1. Strategy - InteresStrategy (Ahorro, Corriente, PlazoFijo)
 * 2. Observer - MovimientoObserver (Notificacion, Auditoria, Fraude)
 * 3. Template Method - OperacionBancariaTemplate
 *
 * PATRONES DE MICROSERVICIOS (5):
 * 1. API Gateway - Este controlador centralizado
 * 2. DTO - ApiResponse<T>
 * 3. Circuit Breaker - CircuitBreaker
 * 4. Rate Limiter - RateLimiter (Token Bucket)
 * 5. Health Check - HealthCheck (Kubernetes-style)
 *
 * PRINCIPIOS SOLID (5):
 * S - Single Responsibility: Cada clase una responsabilidad
 * O - Open/Closed: Extensible sin modificar
 * L - Liskov Substitution: Intercambiables via interfaces
 * I - Interface Segregation: OperacionesLectura/Escritura
 * D - Dependency Inversion: Inyeccion de dependencias
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@RestController
@RequestMapping("/api/patrones")
@CrossOrigin(origins = "*")
public class PatronesController {

    private final CuentaFactory cuentaFactory;
    private final MovimientoBuilder movimientoBuilder;
    private final InteresStrategyFactory interesStrategyFactory;
    private final MovimientoSubject movimientoSubject;
    private final CircuitBreaker circuitBreaker;
    private final RateLimiter rateLimiter;
    private final HealthCheck healthCheck;
    private final ServicioBancario servicioBancario;

    @Autowired
    public PatronesController(
            CuentaFactory cuentaFactory,
            MovimientoBuilder movimientoBuilder,
            InteresStrategyFactory interesStrategyFactory,
            MovimientoSubject movimientoSubject,
            CircuitBreaker circuitBreaker,
            RateLimiter rateLimiter,
            HealthCheck healthCheck,
            ServicioBancario servicioBancario) {
        this.cuentaFactory = cuentaFactory;
        this.movimientoBuilder = movimientoBuilder;
        this.interesStrategyFactory = interesStrategyFactory;
        this.movimientoSubject = movimientoSubject;
        this.circuitBreaker = circuitBreaker;
        this.rateLimiter = rateLimiter;
        this.healthCheck = healthCheck;
        this.servicioBancario = servicioBancario;
    }

    // ==================== RESUMEN GENERAL ====================

    /**
     * GET /api/patrones
     * Muestra un resumen de todos los patrones implementados.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> resumenPatrones() {
        Map<String, Object> resumen = new LinkedHashMap<>();

        // Principios SOLID
        resumen.put("principios_solid", Map.of(
            "S_Single_Responsibility", "Cada clase tiene una sola responsabilidad (BancoService, CuentaFactory, etc.)",
            "O_Open_Closed", "Clases abiertas a extension (Strategy pattern) cerradas a modificacion",
            "L_Liskov_Substitution", "ServicioBancario implementable por cualquier clase que cumpla el contrato",
            "I_Interface_Segregation", "OperacionesLectura y OperacionesEscritura separadas",
            "D_Dependency_Inversion", "Inyeccion de dependencias via @Autowired, no instanciacion directa"
        ));

        // Patrones Creacionales
        resumen.put("patrones_creacionales", Map.of(
            "Factory_Method", "CuentaFactory - Crea cuentas segun tipo (AHORRO, CORRIENTE, PLAZO_FIJO, SUELDO)",
            "Builder", "MovimientoBuilder - Construye objetos Movimiento paso a paso",
            "Singleton", "ServiceLocator + Spring @Service - Una sola instancia por servicio"
        ));

        // Patrones de Comportamiento
        resumen.put("patrones_comportamiento", Map.of(
            "Strategy", "InteresStrategy - Diferentes algoritmos de calculo de interes por tipo de cuenta",
            "Observer", "MovimientoObserver - Notificacion, Auditoria, Fraude detectados en cada movimiento",
            "Template_Method", "OperacionBancariaTemplate - Estructura comun para depositos y retiros"
        ));

        // Patrones de Microservicios
        resumen.put("patrones_microservicios", Map.of(
            "API_Gateway", "PatronesController/BancoController - Punto de entrada centralizado",
            "DTO", "ApiResponse<T> - Respuestas estandarizadas con success, data, message",
            "Circuit_Breaker", "CircuitBreaker - Proteccion contra fallos en cascada (CERRADO/ABIERTO/SEMI_ABIERTO)",
            "Rate_Limiter", "RateLimiter - Control de trafico (Token Bucket, 100 req/min)",
            "Health_Check", "HealthCheck - Endpoints /health, /health/live, /health/ready para Kubernetes"
        ));

        resumen.put("endpoints_demo", Map.of(
            "Factory", "GET /api/patrones/demo/factory",
            "Builder", "GET /api/patrones/demo/builder",
            "Strategy", "GET /api/patrones/demo/strategy",
            "Observer", "POST /api/patrones/demo/observer",
            "Template_Method", "GET /api/patrones/demo/template",
            "Circuit_Breaker", "GET /api/patrones/demo/circuit-breaker",
            "Rate_Limiter", "GET /api/patrones/demo/rate-limiter"
        ));

        return ResponseEntity.ok(ApiResponse.success(resumen));
    }

    // ==================== DEMO PATRONES CREACIONALES ====================

    /**
     * GET /api/patrones/demo/factory
     * Demuestra el patron Factory Method.
     */
    @GetMapping("/demo/factory")
    public ResponseEntity<ApiResponse<Map<String, Object>>> demoFactory() {
        Map<String, Object> demo = new LinkedHashMap<>();

        demo.put("patron", "Factory Method");
        demo.put("clase", "CuentaFactory");
        demo.put("ubicacion", "com.trabajo.api.pattern.creational.CuentaFactory");

        // Crear diferentes tipos de cuentas
        List<Map<String, Object>> cuentasCreadas = new ArrayList<>();

        for (TipoCuenta tipo : TipoCuenta.values()) {
            Cuenta cuenta = cuentaFactory.crearCuenta(tipo, "Demo Usuario", "12345678");
            double saldoMinimo = cuentaFactory.obtenerSaldoMinimo(tipo);

            cuentasCreadas.add(Map.of(
                "tipo", tipo.name(),
                "descripcion", tipo.getDescripcion(),
                "numeroCuenta", cuenta.getNumeroCuenta(),
                "estado", cuenta.getEstado().name(),
                "saldoMinimo", saldoMinimo
            ));
        }

        demo.put("cuentas_creadas", cuentasCreadas);
        demo.put("beneficios", List.of(
            "Encapsula logica de creacion",
            "Facilita agregar nuevos tipos",
            "Centraliza validacion inicial",
            "Aplica SRP y OCP"
        ));

        return ResponseEntity.ok(ApiResponse.success(demo));
    }

    /**
     * GET /api/patrones/demo/builder
     * Demuestra el patron Builder.
     */
    @GetMapping("/demo/builder")
    public ResponseEntity<ApiResponse<Map<String, Object>>> demoBuilder() {
        Map<String, Object> demo = new LinkedHashMap<>();

        demo.put("patron", "Builder");
        demo.put("clase", "MovimientoBuilder");
        demo.put("ubicacion", "com.trabajo.api.pattern.creational.MovimientoBuilder");

        // Crear cuenta temporal para demo
        Cuenta cuentaDemo = cuentaFactory.crearCuenta(TipoCuenta.AHORRO, "Demo Builder", "87654321");
        cuentaDemo.setSaldo(1000.0);

        // Construir movimiento con Builder
        Movimiento movimiento = movimientoBuilder.nuevo()
            .conCuenta(cuentaDemo)
            .conTipo(TipoMovimiento.DEPOSITO)
            .conMonto(500.0)
            .conDescripcion("Deposito demo via Builder pattern")
            .build();

        demo.put("movimiento_construido", Map.of(
            "tipo", movimiento.getTipo().name(),
            "monto", movimiento.getMonto(),
            "descripcion", movimiento.getDescripcion(),
            "saldoAnterior", movimiento.getSaldoAnterior(),
            "saldoPosterior", movimiento.getSaldoPosterior(),
            "fecha", movimiento.getFecha().toString()
        ));

        demo.put("fluent_api_ejemplo", List.of(
            "movimientoBuilder.nuevo()",
            ".conCuenta(cuenta)",
            ".conTipo(TipoMovimiento.DEPOSITO)",
            ".conMonto(500.0)",
            ".conDescripcion(\"...\")",
            ".build()"
        ));

        demo.put("beneficios", List.of(
            "Construccion paso a paso",
            "API fluida y legible",
            "Validacion en build()",
            "Inmutabilidad opcional"
        ));

        return ResponseEntity.ok(ApiResponse.success(demo));
    }

    // ==================== DEMO PATRONES DE COMPORTAMIENTO ====================

    /**
     * GET /api/patrones/demo/strategy
     * Demuestra el patron Strategy.
     */
    @GetMapping("/demo/strategy")
    public ResponseEntity<ApiResponse<Map<String, Object>>> demoStrategy() {
        Map<String, Object> demo = new LinkedHashMap<>();

        demo.put("patron", "Strategy");
        demo.put("interfaz", "InteresStrategy");
        demo.put("ubicacion", "com.trabajo.api.pattern.behavioral");

        double saldo = 10000.0;
        int dias = 365;

        List<Map<String, Object>> estrategias = new ArrayList<>();

        for (TipoCuenta tipo : TipoCuenta.values()) {
            InteresStrategy strategy = interesStrategyFactory.getStrategy(tipo);
            double interes = strategy.calcularInteres(saldo, dias);

            estrategias.add(Map.of(
                "tipoCuenta", tipo.name(),
                "estrategia", strategy.getNombre(),
                "tasaAnual", strategy.getTasaAnual() + "%",
                "saldoBase", saldo,
                "diasCalculados", dias,
                "interesGenerado", String.format("%.2f", interes)
            ));
        }

        demo.put("calculo_interes", estrategias);
        demo.put("implementaciones", List.of(
            "InteresAhorroStrategy (3% anual)",
            "InteresCorrienteStrategy (0% anual)",
            "InteresPlazoFijoStrategy (8% anual + bonificacion)"
        ));

        demo.put("beneficios", List.of(
            "Algoritmos intercambiables",
            "Facil agregar nuevas estrategias (OCP)",
            "Elimina condicionales complejos",
            "Cada estrategia es testeable"
        ));

        return ResponseEntity.ok(ApiResponse.success(demo));
    }

    /**
     * POST /api/patrones/demo/observer
     * Demuestra el patron Observer.
     */
    @PostMapping("/demo/observer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> demoObserver(
            @RequestParam(defaultValue = "1000") double monto) {
        Map<String, Object> demo = new LinkedHashMap<>();

        demo.put("patron", "Observer");
        demo.put("subject", "MovimientoSubject");
        demo.put("ubicacion", "com.trabajo.api.pattern.behavioral");

        // Crear movimiento de prueba
        Cuenta cuentaDemo = cuentaFactory.crearCuenta(TipoCuenta.AHORRO, "Observer Demo", "11111111");
        cuentaDemo.setSaldo(5000.0);

        Movimiento movimiento = movimientoBuilder.nuevo()
            .conCuenta(cuentaDemo)
            .conTipo(TipoMovimiento.DEPOSITO)
            .conMonto(monto)
            .conDescripcion("Movimiento para demo Observer")
            .build();

        // Notificar observers
        movimientoSubject.notificarMovimiento(movimiento);

        demo.put("movimiento_notificado", Map.of(
            "tipo", movimiento.getTipo().name(),
            "monto", movimiento.getMonto(),
            "descripcion", movimiento.getDescripcion()
        ));

        demo.put("observers_activos", movimientoSubject.getCantidadObservers() + " observadores registrados");

        demo.put("observers_implementados", List.of(
            "NotificacionObserver - Envia notificaciones al usuario",
            "AuditoriaObserver - Registra log de auditoria",
            "FraudeObserver - Detecta montos sospechosos (>10,000)"
        ));

        demo.put("nota", monto > 10000 ?
            "ALERTA: Monto mayor a 10,000 detectado por FraudeObserver" :
            "Monto normal, sin alertas de fraude");

        demo.put("beneficios", List.of(
            "Desacoplamiento subject-observers",
            "Agregar observers sin modificar subject",
            "Notificacion automatica de cambios",
            "Facilita logging y auditoria"
        ));

        return ResponseEntity.ok(ApiResponse.success(demo));
    }

    /**
     * GET /api/patrones/demo/template
     * Demuestra el patron Template Method.
     */
    @GetMapping("/demo/template")
    public ResponseEntity<ApiResponse<Map<String, Object>>> demoTemplate() {
        Map<String, Object> demo = new LinkedHashMap<>();

        demo.put("patron", "Template Method");
        demo.put("clase_abstracta", "OperacionBancariaTemplate");
        demo.put("ubicacion", "com.trabajo.api.pattern.behavioral");

        // Crear instancias de operaciones
        DepositoOperacion deposito = new DepositoOperacion();
        RetiroOperacion retiro = new RetiroOperacion();

        Cuenta cuentaDemo = cuentaFactory.crearCuenta(TipoCuenta.AHORRO, "Template Demo", "22222222");
        cuentaDemo.setSaldo(1000.0);

        demo.put("estructura_template", Map.of(
            "metodo_template", "ejecutar(cuenta, monto)",
            "pasos_fijos", List.of(
                "1. validarCuenta(cuenta)",
                "2. validarMonto(monto)",
                "3. validacionEspecifica(cuenta, monto) [ABSTRACTO]",
                "4. realizarOperacion(cuenta, monto) [ABSTRACTO]",
                "5. registrarOperacion(cuenta, monto)"
            )
        ));

        demo.put("implementaciones", List.of(
            Map.of(
                "clase", "DepositoOperacion",
                "metodo_template", "ejecutarOperacion(cuenta, monto, descripcion)",
                "pasos_especificos", "Suma monto al saldo, detecta depositos grandes"
            ),
            Map.of(
                "clase", "RetiroOperacion",
                "metodo_template", "ejecutarOperacion(cuenta, monto, descripcion)",
                "pasos_especificos", "Valida saldo suficiente, resta monto, alerta saldo bajo"
            )
        ));

        demo.put("beneficios", List.of(
            "Estructura comun reutilizable",
            "Subclases solo implementan pasos especificos",
            "Evita duplicacion de codigo",
            "Facilita mantenimiento"
        ));

        return ResponseEntity.ok(ApiResponse.success(demo));
    }

    // ==================== DEMO PATRONES DE MICROSERVICIOS ====================

    /**
     * GET /api/patrones/demo/circuit-breaker
     * Demuestra el patron Circuit Breaker.
     */
    @GetMapping("/demo/circuit-breaker")
    public ResponseEntity<ApiResponse<Map<String, Object>>> demoCircuitBreaker() {
        Map<String, Object> demo = new LinkedHashMap<>();

        demo.put("patron", "Circuit Breaker");
        demo.put("clase", "CircuitBreaker");
        demo.put("ubicacion", "com.trabajo.api.pattern.microservices.CircuitBreaker");

        String servicio = "servicio-demo";

        // Obtener estado actual
        CircuitBreaker.Estado estadoActual = circuitBreaker.getEstado(servicio);

        demo.put("servicio", servicio);
        demo.put("estado_actual", estadoActual.name());

        demo.put("estados_posibles", Map.of(
            "CERRADO", "Normal - Las llamadas pasan al servicio",
            "ABIERTO", "Fallo detectado - Las llamadas son rechazadas inmediatamente",
            "SEMI_ABIERTO", "Probando recuperacion - Algunas llamadas pasan"
        ));

        demo.put("configuracion", Map.of(
            "umbralFallos", "5 fallos consecutivos abren el circuito",
            "tiempoRecuperacion", "30 segundos antes de pasar a SEMI_ABIERTO"
        ));

        // Simular llamada protegida por Circuit Breaker
        String resultado = circuitBreaker.ejecutar(
            servicio,
            () -> "Operacion exitosa - Circuito funcionando",
            () -> "Fallback activado - Circuito abierto"
        );

        demo.put("llamada_simulada", Map.of(
            "servicio", servicio,
            "resultado", resultado,
            "estado_final", circuitBreaker.getEstado(servicio).name()
        ));

        demo.put("beneficios", List.of(
            "Previene fallos en cascada",
            "Recuperacion automatica",
            "Mejora resiliencia del sistema",
            "Fail-fast cuando servicio no disponible"
        ));

        return ResponseEntity.ok(ApiResponse.success(demo));
    }

    /**
     * GET /api/patrones/demo/rate-limiter
     * Demuestra el patron Rate Limiter.
     */
    @GetMapping("/demo/rate-limiter")
    public ResponseEntity<ApiResponse<Map<String, Object>>> demoRateLimiter(
            @RequestHeader(value = "X-Client-Id", defaultValue = "demo-client") String clientId) {
        Map<String, Object> demo = new LinkedHashMap<>();

        demo.put("patron", "Rate Limiter (Token Bucket)");
        demo.put("clase", "RateLimiter");
        demo.put("ubicacion", "com.trabajo.api.pattern.microservices.RateLimiter");

        // Obtener info del cliente
        Map<String, Object> limiteInfo = rateLimiter.getLimiteInfo(clientId);

        demo.put("cliente", clientId);
        demo.put("limite_actual", limiteInfo);

        // Intentar consumir token
        boolean permitido = rateLimiter.permitirSolicitud(clientId);

        demo.put("request_actual", Map.of(
            "permitido", permitido,
            "mensaje", permitido ? "Token consumido exitosamente" : "Limite excedido, espere"
        ));

        demo.put("configuracion", Map.of(
            "tokensMaximos", 100,
            "tokensRecargaPorMinuto", 100,
            "algoritmo", "Token Bucket"
        ));

        demo.put("funcionamiento", List.of(
            "1. Cada cliente tiene un bucket con tokens",
            "2. Cada request consume 1 token",
            "3. Tokens se recargan con el tiempo",
            "4. Sin tokens = request rechazado (429)"
        ));

        demo.put("beneficios", List.of(
            "Protege contra sobrecarga",
            "Control granular por cliente",
            "Prevencion de ataques DDoS",
            "Fair usage de recursos"
        ));

        return ResponseEntity.ok(ApiResponse.success(demo));
    }

    /**
     * GET /api/patrones/demo/health-check
     * Demuestra el patron Health Check.
     */
    @GetMapping("/demo/health-check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> demoHealthCheck() {
        Map<String, Object> demo = new LinkedHashMap<>();

        demo.put("patron", "Health Check");
        demo.put("clase", "HealthCheck");
        demo.put("ubicacion", "com.trabajo.api.pattern.microservices.HealthCheck");

        // Obtener estado de salud
        Map<String, Object> healthStatus = healthCheck.getHealthStatus();

        demo.put("estado_actual", healthStatus);

        demo.put("endpoints_kubernetes", Map.of(
            "/health", "Estado detallado completo",
            "/health/live", "Liveness probe - ¿Esta vivo el proceso?",
            "/health/ready", "Readiness probe - ¿Puede recibir trafico?"
        ));

        demo.put("checks_realizados", List.of(
            "Base de datos (conexion activa)",
            "Memoria disponible (>10%)",
            "Componentes criticos"
        ));

        demo.put("uso_kubernetes", Map.of(
            "livenessProbe", "Si falla, Kubernetes reinicia el pod",
            "readinessProbe", "Si falla, el pod no recibe trafico"
        ));

        demo.put("beneficios", List.of(
            "Monitoreo automatizado",
            "Auto-recuperacion con Kubernetes",
            "Deteccion temprana de problemas",
            "Routing inteligente de trafico"
        ));

        return ResponseEntity.ok(ApiResponse.success(demo));
    }

    // ==================== SOLID PRINCIPLES DEMO ====================

    /**
     * GET /api/patrones/solid
     * Muestra como se aplican los principios SOLID.
     */
    @GetMapping("/solid")
    public ResponseEntity<ApiResponse<Map<String, Object>>> demoSolid() {
        Map<String, Object> solid = new LinkedHashMap<>();

        solid.put("S_Single_Responsibility", Map.of(
            "principio", "Una clase debe tener una sola razon para cambiar",
            "aplicacion", List.of(
                "BancoService: Solo logica de negocio bancaria",
                "CuentaRepository: Solo acceso a datos de cuentas",
                "CuentaFactory: Solo creacion de cuentas",
                "NotificacionObserver: Solo envio de notificaciones"
            ),
            "archivo_ejemplo", "BancoService.java - Lineas 17-55"
        ));

        solid.put("O_Open_Closed", Map.of(
            "principio", "Abierto a extension, cerrado a modificacion",
            "aplicacion", List.of(
                "InteresStrategy: Nuevas estrategias sin modificar existentes",
                "MovimientoObserver: Nuevos observers sin cambiar Subject",
                "OperacionBancariaTemplate: Nuevas operaciones extendiendo"
            ),
            "archivo_ejemplo", "InteresStrategy.java y sus implementaciones"
        ));

        solid.put("L_Liskov_Substitution", Map.of(
            "principio", "Objetos de superclase sustituibles por subclases",
            "aplicacion", List.of(
                "Cualquier InteresStrategy es intercambiable",
                "BancoService puede sustituirse por cualquier ServicioBancario",
                "Observers intercambiables en MovimientoSubject"
            ),
            "archivo_ejemplo", "ServicioBancario.java"
        ));

        solid.put("I_Interface_Segregation", Map.of(
            "principio", "Interfaces especificas mejor que una general",
            "aplicacion", List.of(
                "OperacionesLectura: Solo metodos GET/consultas",
                "OperacionesEscritura: Solo metodos POST/modificacion",
                "ServicioBancario: Combina ambas cuando se necesitan todas"
            ),
            "archivos_ejemplo", List.of(
                "OperacionesLectura.java",
                "OperacionesEscritura.java",
                "ServicioBancario.java"
            )
        ));

        solid.put("D_Dependency_Inversion", Map.of(
            "principio", "Depender de abstracciones, no de implementaciones",
            "aplicacion", List.of(
                "BancoService depende de interfaces Repository",
                "PatronesController inyecta ServicioBancario (interfaz)",
                "InteresStrategyFactory inyecta estrategias via interfaz",
                "@Autowired para inyeccion de dependencias"
            ),
            "archivo_ejemplo", "BancoService.java constructor - Lineas 73-84"
        ));

        return ResponseEntity.ok(ApiResponse.success(solid));
    }

    // ==================== ENDPOINTS INTERACTIVOS ====================

    /**
     * POST /api/patrones/interactivo/calcular-interes
     * Calcula el interes en tiempo real segun tipo de cuenta, monto y dias.
     */
    @PostMapping("/interactivo/calcular-interes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calcularInteresInteractivo(
            @RequestParam String tipoCuenta,
            @RequestParam double monto,
            @RequestParam(defaultValue = "365") int dias) {

        Map<String, Object> resultado = new LinkedHashMap<>();

        try {
            TipoCuenta tipo = TipoCuenta.valueOf(tipoCuenta.toUpperCase());
            InteresStrategy strategy = interesStrategyFactory.getStrategy(tipo);
            double interes = strategy.calcularInteres(monto, dias);
            double montoFinal = monto + interes;

            resultado.put("exito", true);
            resultado.put("tipoCuenta", tipo.name());
            resultado.put("descripcionCuenta", tipo.getDescripcion());
            resultado.put("estrategia", strategy.getNombre());
            resultado.put("tasaAnual", strategy.getTasaAnual());
            resultado.put("montoInicial", monto);
            resultado.put("diasCalculados", dias);
            resultado.put("interesGenerado", Math.round(interes * 100.0) / 100.0);
            resultado.put("montoFinal", Math.round(montoFinal * 100.0) / 100.0);
            resultado.put("formulaUsada", String.format("%.2f × %.2f%% × %d/365", monto, strategy.getTasaAnual(), dias));

        } catch (IllegalArgumentException e) {
            resultado.put("exito", false);
            resultado.put("error", "Tipo de cuenta invalido: " + tipoCuenta);
            resultado.put("tiposValidos", List.of("AHORRO", "CORRIENTE", "PLAZO_FIJO", "SUELDO"));
        }

        return ResponseEntity.ok(ApiResponse.success(resultado));
    }

    /**
     * POST /api/patrones/interactivo/circuit-breaker/simular-fallo
     * Simula un fallo en el circuit breaker para demostrar el cambio de estado.
     */
    @PostMapping("/interactivo/circuit-breaker/simular-fallo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> simularFalloCircuitBreaker(
            @RequestParam(defaultValue = "servicio-demo") String servicio) {

        Map<String, Object> resultado = new LinkedHashMap<>();

        CircuitBreaker.Estado estadoAntes = circuitBreaker.getEstado(servicio);

        // Simular un fallo (lanzar excepcion)
        try {
            circuitBreaker.ejecutar(
                servicio,
                () -> { throw new RuntimeException("Fallo simulado para demo"); },
                () -> "Fallback activado"
            );
        } catch (Exception ignored) {}

        CircuitBreaker.Estado estadoDespues = circuitBreaker.getEstado(servicio);

        resultado.put("servicio", servicio);
        resultado.put("estadoAntes", estadoAntes.name());
        resultado.put("estadoDespues", estadoDespues.name());
        resultado.put("falloSimulado", true);
        resultado.put("mensaje", estadoDespues == CircuitBreaker.Estado.ABIERTO
            ? "Circuit Breaker ABIERTO - Se alcanzó el umbral de fallos"
            : "Fallo registrado - Estado: " + estadoDespues.name());

        return ResponseEntity.ok(ApiResponse.success(resultado));
    }

    /**
     * POST /api/patrones/interactivo/circuit-breaker/resetear
     * Resetea el estado del circuit breaker.
     */
    @PostMapping("/interactivo/circuit-breaker/resetear")
    public ResponseEntity<ApiResponse<Map<String, Object>>> resetearCircuitBreaker(
            @RequestParam(defaultValue = "servicio-demo") String servicio) {

        Map<String, Object> resultado = new LinkedHashMap<>();

        CircuitBreaker.Estado estadoAntes = circuitBreaker.getEstado(servicio);
        circuitBreaker.resetCircuito(servicio);
        CircuitBreaker.Estado estadoDespues = circuitBreaker.getEstado(servicio);

        resultado.put("servicio", servicio);
        resultado.put("estadoAntes", estadoAntes.name());
        resultado.put("estadoDespues", estadoDespues.name());
        resultado.put("mensaje", "Circuit Breaker reseteado exitosamente");

        return ResponseEntity.ok(ApiResponse.success(resultado));
    }

    /**
     * GET /api/patrones/interactivo/circuit-breaker/estado
     * Obtiene el estado actual del circuit breaker.
     */
    @GetMapping("/interactivo/circuit-breaker/estado")
    public ResponseEntity<ApiResponse<Map<String, Object>>> estadoCircuitBreaker(
            @RequestParam(defaultValue = "servicio-demo") String servicio) {

        Map<String, Object> resultado = new LinkedHashMap<>();

        CircuitBreaker.Estado estado = circuitBreaker.getEstado(servicio);

        resultado.put("servicio", servicio);
        resultado.put("estado", estado.name());
        resultado.put("descripcion", switch(estado) {
            case CERRADO -> "Normal - Las llamadas pasan al servicio";
            case ABIERTO -> "Protegido - Las llamadas son rechazadas (fail-fast)";
            case SEMI_ABIERTO -> "Probando - Algunas llamadas de prueba pasan";
        });
        resultado.put("color", switch(estado) {
            case CERRADO -> "success";
            case ABIERTO -> "danger";
            case SEMI_ABIERTO -> "warning";
        });

        return ResponseEntity.ok(ApiResponse.success(resultado));
    }

    /**
     * GET /api/patrones/interactivo/health
     * Obtiene el estado de salud del sistema en formato simple.
     */
    @GetMapping("/interactivo/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthInteractivo() {
        Map<String, Object> health = healthCheck.getHealthStatus();
        return ResponseEntity.ok(ApiResponse.success(health));
    }

    /**
     * POST /api/patrones/interactivo/observer/simular
     * Simula un movimiento y notifica a los observers.
     */
    @PostMapping("/interactivo/observer/simular")
    public ResponseEntity<ApiResponse<Map<String, Object>>> simularObserver(
            @RequestParam(defaultValue = "DEPOSITO") String tipoMovimiento,
            @RequestParam(defaultValue = "1000") double monto) {

        Map<String, Object> resultado = new LinkedHashMap<>();

        try {
            TipoMovimiento tipo = TipoMovimiento.valueOf(tipoMovimiento.toUpperCase());

            Cuenta cuentaDemo = cuentaFactory.crearCuenta(TipoCuenta.AHORRO, "Observer Demo", "99999999");
            cuentaDemo.setSaldo(20000.0);

            Movimiento movimiento = movimientoBuilder.nuevo()
                .conCuenta(cuentaDemo)
                .conTipo(tipo)
                .conMonto(monto)
                .conDescripcion("Movimiento simulado para demo Observer")
                .build();

            // Notificar observers
            movimientoSubject.notificarMovimiento(movimiento);

            resultado.put("exito", true);
            resultado.put("tipoMovimiento", tipo.name());
            resultado.put("monto", monto);
            resultado.put("observersNotificados", movimientoSubject.getCantidadObservers());

            List<String> notificaciones = new ArrayList<>();
            notificaciones.add("✅ NotificacionObserver: Usuario notificado del " + tipo.name());
            notificaciones.add("📋 AuditoriaObserver: Log registrado en auditoria");

            if (monto > 10000) {
                notificaciones.add("🚨 FraudeObserver: ALERTA - Monto sospechoso detectado (>" + 10000 + ")");
                resultado.put("alertaFraude", true);
            } else {
                resultado.put("alertaFraude", false);
            }

            resultado.put("notificaciones", notificaciones);

        } catch (IllegalArgumentException e) {
            resultado.put("exito", false);
            resultado.put("error", "Tipo de movimiento invalido");
            resultado.put("tiposValidos", List.of("DEPOSITO", "RETIRO", "TRANSFERENCIA_ENVIADA", "TRANSFERENCIA_RECIBIDA"));
        }

        return ResponseEntity.ok(ApiResponse.success(resultado));
    }
}
