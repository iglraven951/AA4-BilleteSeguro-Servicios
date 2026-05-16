package com.trabajo.api.pattern.microservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ============================================================================
 * PATRON DE MICROSERVICIOS: RATE LIMITER
 * ============================================================================
 *
 * PROPOSITO:
 * Controla la tasa de solicitudes que un cliente puede hacer en un periodo
 * de tiempo. Previene abuso y protege los recursos del servidor.
 *
 * ALGORITMO: Token Bucket
 * - Cada cliente tiene un "bucket" de tokens
 * - Cada solicitud consume un token
 * - Los tokens se regeneran periodicamente
 * - Si no hay tokens, la solicitud se rechaza
 *
 * APLICACION EN ESTE PROYECTO:
 * - Limite: 100 solicitudes por minuto por IP/cliente
 * - Protege contra ataques de fuerza bruta
 * - Evita sobrecarga del servidor
 *
 * BENEFICIOS:
 * 1. Protege contra abuso de la API
 * 2. Garantiza disponibilidad para todos los clientes
 * 3. Previene ataques DDoS simples
 * 4. Permite politicas de uso justo
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class RateLimiter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);

    // Configuracion
    private static final int MAX_REQUESTS = 100;         // Solicitudes maximas
    private static final Duration VENTANA = Duration.ofMinutes(1);  // Ventana de tiempo

    // Almacenamiento de buckets por cliente
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    /**
     * Verifica si un cliente puede realizar una solicitud.
     *
     * @param clientId Identificador del cliente (IP, token, etc.)
     * @return true si la solicitud esta permitida, false si excede el limite
     */
    public boolean permitirSolicitud(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId, k -> new TokenBucket());
        return bucket.consumirToken();
    }

    /**
     * Obtiene informacion del limite para un cliente.
     *
     * @param clientId Identificador del cliente
     * @return Mapa con informacion del limite
     */
    public Map<String, Object> getLimiteInfo(String clientId) {
        TokenBucket bucket = buckets.get(clientId);
        if (bucket == null) {
            return Map.of(
                "limit", MAX_REQUESTS,
                "remaining", MAX_REQUESTS,
                "resetIn", "N/A"
            );
        }
        return bucket.getInfo();
    }

    /**
     * Resetea el limite de un cliente (para testing/admin).
     */
    public void resetCliente(String clientId) {
        buckets.remove(clientId);
        logger.info("🔄 Rate Limiter: Reset para cliente {}", clientId);
    }

    /**
     * Implementacion del Token Bucket.
     */
    private class TokenBucket {
        private AtomicInteger tokens;
        private LocalDateTime ultimoReset;

        TokenBucket() {
            this.tokens = new AtomicInteger(MAX_REQUESTS);
            this.ultimoReset = LocalDateTime.now();
        }

        synchronized boolean consumirToken() {
            // Verificar si debemos resetear
            if (Duration.between(ultimoReset, LocalDateTime.now()).compareTo(VENTANA) > 0) {
                tokens.set(MAX_REQUESTS);
                ultimoReset = LocalDateTime.now();
            }

            // Intentar consumir token
            int tokensActuales = tokens.get();
            if (tokensActuales > 0) {
                tokens.decrementAndGet();
                return true;
            }

            logger.warn("⚠️ Rate Limiter: Limite excedido");
            return false;
        }

        Map<String, Object> getInfo() {
            Duration tiempoRestante = Duration.between(LocalDateTime.now(),
                ultimoReset.plus(VENTANA));

            return Map.of(
                "limit", MAX_REQUESTS,
                "remaining", tokens.get(),
                "resetIn", Math.max(0, tiempoRestante.getSeconds()) + " segundos"
            );
        }
    }
}
