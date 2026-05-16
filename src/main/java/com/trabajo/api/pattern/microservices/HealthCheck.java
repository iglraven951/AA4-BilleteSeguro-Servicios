package com.trabajo.api.pattern.microservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * PATRON DE MICROSERVICIOS: HEALTH CHECK
 * ============================================================================
 *
 * PROPOSITO:
 * Proporciona endpoints para verificar el estado de salud del servicio.
 * Esencial para orquestadores (Kubernetes, Docker Swarm) y balanceadores
 * de carga para determinar si una instancia esta saludable.
 *
 * TIPOS DE CHECKS:
 * - Liveness: ¿El servicio esta vivo?
 * - Readiness: ¿El servicio puede recibir trafico?
 * - Health: Estado detallado de todos los componentes
 *
 * APLICACION EN ESTE PROYECTO:
 * Verifica el estado de:
 * - Base de datos H2
 * - Servicios internos
 * - Memoria disponible
 * - Conexiones activas
 *
 * BENEFICIOS:
 * 1. Permite auto-recuperacion en cloud
 * 2. Facilita el monitoreo
 * 3. Mejora la disponibilidad
 * 4. Diagnostico rapido de problemas
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class HealthCheck {

    private final JdbcTemplate jdbcTemplate;
    private final LocalDateTime startTime;

    @Autowired
    public HealthCheck(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.startTime = LocalDateTime.now();
    }

    /**
     * Verificacion basica de vida (liveness).
     * Retorna true si el servicio esta corriendo.
     */
    public boolean isAlive() {
        return true; // Si llegamos aqui, el servicio esta vivo
    }

    /**
     * Verificacion de disponibilidad (readiness).
     * Retorna true si el servicio puede recibir trafico.
     */
    public boolean isReady() {
        return checkDatabase();
    }

    /**
     * Estado detallado de salud.
     * Incluye informacion de todos los componentes.
     */
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();

        health.put("status", isReady() ? "UP" : "DOWN");
        health.put("timestamp", LocalDateTime.now());
        health.put("uptime", getUptime());

        // Componentes individuales
        Map<String, Object> components = new HashMap<>();
        components.put("database", getDatabaseHealth());
        components.put("memory", getMemoryHealth());
        components.put("application", getApplicationHealth());

        health.put("components", components);

        return health;
    }

    /**
     * Verifica la conexion a la base de datos.
     */
    private boolean checkDatabase() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene el estado detallado de la base de datos.
     */
    private Map<String, Object> getDatabaseHealth() {
        Map<String, Object> dbHealth = new HashMap<>();
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            dbHealth.put("status", "UP");
            dbHealth.put("database", "H2");

            // Contar registros como indicador
            Integer cuentas = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM cuentas", Integer.class);
            Integer movimientos = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM movimientos", Integer.class);

            dbHealth.put("cuentas", cuentas);
            dbHealth.put("movimientos", movimientos);

        } catch (Exception e) {
            dbHealth.put("status", "DOWN");
            dbHealth.put("error", e.getMessage());
        }
        return dbHealth;
    }

    /**
     * Obtiene el estado de la memoria.
     */
    private Map<String, Object> getMemoryHealth() {
        Map<String, Object> memHealth = new HashMap<>();

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        double usedPercentage = (double) usedMemory / maxMemory * 100;

        memHealth.put("status", usedPercentage < 90 ? "UP" : "WARNING");
        memHealth.put("max", formatBytes(maxMemory));
        memHealth.put("used", formatBytes(usedMemory));
        memHealth.put("free", formatBytes(freeMemory));
        memHealth.put("usedPercentage", String.format("%.2f%%", usedPercentage));

        return memHealth;
    }

    /**
     * Obtiene informacion de la aplicacion.
     */
    private Map<String, Object> getApplicationHealth() {
        Map<String, Object> appHealth = new HashMap<>();

        appHealth.put("status", "UP");
        appHealth.put("name", "Billetera Seguro");
        appHealth.put("version", "1.0.0");
        appHealth.put("javaVersion", System.getProperty("java.version"));
        appHealth.put("startTime", startTime);
        appHealth.put("uptime", getUptime());

        return appHealth;
    }

    /**
     * Calcula el tiempo de actividad.
     */
    private String getUptime() {
        java.time.Duration uptime = java.time.Duration.between(startTime, LocalDateTime.now());
        long days = uptime.toDays();
        long hours = uptime.toHours() % 24;
        long minutes = uptime.toMinutes() % 60;
        long seconds = uptime.getSeconds() % 60;

        return String.format("%d dias, %d horas, %d minutos, %d segundos",
            days, hours, minutes, seconds);
    }

    /**
     * Formatea bytes a formato legible.
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
