package com.trabajo.api.controller;

import com.trabajo.api.pattern.microservices.ApiResponse;
import com.trabajo.api.pattern.microservices.CircuitBreaker;
import com.trabajo.api.pattern.microservices.HealthCheck;
import com.trabajo.api.pattern.microservices.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ============================================================================
 * PATRON DE MICROSERVICIOS: API GATEWAY / HEALTH ENDPOINTS
 * ============================================================================
 *
 * PROPOSITO:
 * Proporciona endpoints estandar para monitoreo y operaciones de
 * microservicios segun las mejores practicas de la industria.
 *
 * ENDPOINTS:
 * - GET /health         : Estado detallado de salud
 * - GET /health/live    : Liveness probe (para Kubernetes)
 * - GET /health/ready   : Readiness probe (para Kubernetes)
 * - GET /api/info       : Informacion de la API
 *
 * APLICACION EN ESTE PROYECTO:
 * Demuestra el patron API Gateway con endpoints de monitoreo,
 * rate limiting y circuit breaker.
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@RestController
@CrossOrigin(origins = "*")
public class HealthController {

    private final HealthCheck healthCheck;
    private final RateLimiter rateLimiter;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public HealthController(HealthCheck healthCheck,
                           RateLimiter rateLimiter,
                           CircuitBreaker circuitBreaker) {
        this.healthCheck = healthCheck;
        this.rateLimiter = rateLimiter;
        this.circuitBreaker = circuitBreaker;
    }

    // ==================== HEALTH ENDPOINTS ====================

    /**
     * GET /health
     * Estado detallado de salud del servicio.
     * Incluye estado de BD, memoria, y componentes.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthStatus = healthCheck.getHealthStatus();
        boolean isHealthy = "UP".equals(healthStatus.get("status"));

        return ResponseEntity
            .status(isHealthy ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE)
            .body(healthStatus);
    }

    /**
     * GET /health/live
     * Liveness probe - verifica si el servicio esta vivo.
     * Usado por Kubernetes para reiniciar pods muertos.
     */
    @GetMapping("/health/live")
    public ResponseEntity<Map<String, String>> liveness() {
        if (healthCheck.isAlive()) {
            return ResponseEntity.ok(Map.of("status", "UP"));
        }
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of("status", "DOWN"));
    }

    /**
     * GET /health/ready
     * Readiness probe - verifica si el servicio puede recibir trafico.
     * Usado por Kubernetes para routing de trafico.
     */
    @GetMapping("/health/ready")
    public ResponseEntity<Map<String, String>> readiness() {
        if (healthCheck.isReady()) {
            return ResponseEntity.ok(Map.of("status", "READY"));
        }
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of("status", "NOT_READY"));
    }

    // ==================== API INFO ====================

    /**
     * GET /api/info
     * Informacion general de la API.
     */
    @GetMapping("/api/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> apiInfo() {
        Map<String, Object> info = Map.of(
            "name", "Billetera Seguro API",
            "version", "1.0.0",
            "description", "Sistema Bancario con Spring Boot y JPA",
            "endpoints", Map.of(
                "cuentas", "/api/cuentas",
                "operaciones", "/api/operaciones",
                "movimientos", "/api/movimientos",
                "health", "/health",
                "patrones", "/api/patrones"
            ),
            "patrones_implementados", Map.of(
                "creacionales", "Factory, Builder, Singleton",
                "comportamiento", "Strategy, Observer, Template Method",
                "microservicios", "API Gateway, Circuit Breaker, Rate Limiter, DTO, Health Check"
            ),
            "principios_solid", "S, O, L, I, D - Todos implementados"
        );

        return ResponseEntity.ok(ApiResponse.success(info));
    }

    // ==================== RATE LIMITER ====================

    /**
     * GET /api/rate-limit-info
     * Informacion del rate limiter para un cliente.
     */
    @GetMapping("/api/rate-limit-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> rateLimitInfo(
            @RequestHeader(value = "X-Client-Id", defaultValue = "anonymous") String clientId) {

        Map<String, Object> info = rateLimiter.getLimiteInfo(clientId);
        return ResponseEntity.ok(ApiResponse.success(info));
    }

    // ==================== CIRCUIT BREAKER STATUS ====================

    /**
     * GET /api/circuit-status/{service}
     * Estado del circuit breaker para un servicio.
     */
    @GetMapping("/api/circuit-status/{service}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> circuitStatus(
            @PathVariable String service) {

        CircuitBreaker.Estado estado = circuitBreaker.getEstado(service);
        Map<String, Object> info = Map.of(
            "service", service,
            "status", estado.name(),
            "description", getDescripcionEstado(estado)
        );

        return ResponseEntity.ok(ApiResponse.success(info));
    }

    private String getDescripcionEstado(CircuitBreaker.Estado estado) {
        return switch (estado) {
            case CERRADO -> "Normal - Las llamadas pasan";
            case ABIERTO -> "Fallo detectado - Las llamadas son rechazadas";
            case SEMI_ABIERTO -> "Probando recuperacion - Algunas llamadas pasan";
        };
    }
}
