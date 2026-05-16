package com.trabajo.api.pattern.microservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * ============================================================================
 * PATRON DE MICROSERVICIOS: CIRCUIT BREAKER
 * ============================================================================
 *
 * PROPOSITO:
 * Previene fallas en cascada cuando un servicio externo falla. El Circuit
 * Breaker monitorea las llamadas y "abre" el circuito cuando detecta
 * demasiados fallos, evitando llamadas innecesarias.
 *
 * ESTADOS:
 * - CERRADO: Funcionamiento normal, las llamadas pasan
 * - ABIERTO: Demasiados fallos, las llamadas se rechazan inmediatamente
 * - SEMI_ABIERTO: Periodo de prueba, algunas llamadas pasan para probar
 *
 * APLICACION EN ESTE PROYECTO:
 * Protege llamadas a servicios externos (APIs de terceros, servicios de
 * notificacion, etc.) evitando que fallos externos afecten al sistema.
 *
 * BENEFICIOS:
 * 1. Previene fallas en cascada
 * 2. Permite recuperacion gradual
 * 3. Mejora la resiliencia del sistema
 * 4. Proporciona feedback rapido cuando hay problemas
 *
 * PRINCIPIO SOLID APLICADO:
 * - Single Responsibility (S): Solo maneja el estado del circuito
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class CircuitBreaker {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);

    /**
     * Estados posibles del Circuit Breaker.
     */
    public enum Estado {
        CERRADO,      // Normal - llamadas pasan
        ABIERTO,      // Fallo - llamadas rechazadas
        SEMI_ABIERTO  // Prueba - algunas llamadas pasan
    }

    /**
     * Configuracion del Circuit Breaker.
     */
    private static final int UMBRAL_FALLOS = 5;           // Fallos para abrir
    private static final Duration TIEMPO_ESPERA = Duration.ofSeconds(30);  // Tiempo abierto
    private static final int LLAMADAS_PRUEBA = 3;         // Llamadas en semi-abierto

    // Estado por servicio
    private final Map<String, CircuitState> circuitos = new ConcurrentHashMap<>();

    /**
     * Ejecuta una operacion protegida por el Circuit Breaker.
     *
     * @param serviceName Nombre del servicio/operacion
     * @param operacion Operacion a ejecutar
     * @param fallback Respuesta alternativa si el circuito esta abierto
     * @return Resultado de la operacion o fallback
     */
    public <T> T ejecutar(String serviceName, Supplier<T> operacion, Supplier<T> fallback) {
        CircuitState estado = circuitos.computeIfAbsent(serviceName, k -> new CircuitState());

        // Verificar si debemos cambiar de ABIERTO a SEMI_ABIERTO
        if (estado.estado == Estado.ABIERTO && estado.debeCambiarASemiAbierto()) {
            estado.estado = Estado.SEMI_ABIERTO;
            estado.llamadasPrueba.set(0);
            logger.info("⚡ Circuit Breaker [{}]: ABIERTO -> SEMI_ABIERTO", serviceName);
        }

        // Si esta ABIERTO, usar fallback
        if (estado.estado == Estado.ABIERTO) {
            logger.warn("🔴 Circuit Breaker [{}]: ABIERTO - Usando fallback", serviceName);
            return fallback.get();
        }

        // Si esta SEMI_ABIERTO, limitar llamadas de prueba
        if (estado.estado == Estado.SEMI_ABIERTO) {
            if (estado.llamadasPrueba.incrementAndGet() > LLAMADAS_PRUEBA) {
                logger.warn("🟡 Circuit Breaker [{}]: SEMI_ABIERTO - Limite alcanzado", serviceName);
                return fallback.get();
            }
        }

        // Intentar ejecutar la operacion
        try {
            T resultado = operacion.get();
            registrarExito(serviceName, estado);
            return resultado;
        } catch (Exception e) {
            registrarFallo(serviceName, estado, e);
            return fallback.get();
        }
    }

    /**
     * Registra una llamada exitosa.
     */
    private void registrarExito(String serviceName, CircuitState estado) {
        if (estado.estado == Estado.SEMI_ABIERTO) {
            // Exito en semi-abierto -> cerrar circuito
            estado.estado = Estado.CERRADO;
            estado.fallosConsecutivos.set(0);
            logger.info("🟢 Circuit Breaker [{}]: SEMI_ABIERTO -> CERRADO", serviceName);
        } else {
            // Reset contador de fallos
            estado.fallosConsecutivos.set(0);
        }
    }

    /**
     * Registra una llamada fallida.
     */
    private void registrarFallo(String serviceName, CircuitState estado, Exception e) {
        int fallos = estado.fallosConsecutivos.incrementAndGet();
        logger.error("❌ Circuit Breaker [{}]: Fallo #{} - {}", serviceName, fallos, e.getMessage());

        if (fallos >= UMBRAL_FALLOS) {
            estado.estado = Estado.ABIERTO;
            estado.momentoApertura = LocalDateTime.now();
            logger.warn("🔴 Circuit Breaker [{}]: CERRADO -> ABIERTO (umbral alcanzado)", serviceName);
        }
    }

    /**
     * Obtiene el estado actual de un circuito.
     */
    public Estado getEstado(String serviceName) {
        CircuitState estado = circuitos.get(serviceName);
        return estado != null ? estado.estado : Estado.CERRADO;
    }

    /**
     * Fuerza el reset de un circuito (para testing/admin).
     */
    public void resetCircuito(String serviceName) {
        circuitos.remove(serviceName);
        logger.info("🔄 Circuit Breaker [{}]: Reset manual", serviceName);
    }

    /**
     * Estado interno de cada circuito.
     */
    private class CircuitState {
        Estado estado = Estado.CERRADO;
        AtomicInteger fallosConsecutivos = new AtomicInteger(0);
        AtomicInteger llamadasPrueba = new AtomicInteger(0);
        LocalDateTime momentoApertura;

        boolean debeCambiarASemiAbierto() {
            if (momentoApertura == null) return false;
            return Duration.between(momentoApertura, LocalDateTime.now()).compareTo(TIEMPO_ESPERA) > 0;
        }
    }
}
