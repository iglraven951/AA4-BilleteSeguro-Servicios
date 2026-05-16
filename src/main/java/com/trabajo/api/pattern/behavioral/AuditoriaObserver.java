package com.trabajo.api.pattern.behavioral;

import com.trabajo.api.model.Movimiento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================================
 * OBSERVER: Auditoria de Movimientos
 * ============================================================================
 *
 * Observador que registra todos los movimientos en un log de auditoria
 * para cumplimiento normativo y trazabilidad.
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class AuditoriaObserver implements MovimientoObserver {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDITORIA");
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onMovimientoRegistrado(Movimiento movimiento) {
        String registroAuditoria = construirRegistroAuditoria(movimiento);
        registrarAuditoria(registroAuditoria);
    }

    /**
     * Construye un registro de auditoria estructurado.
     */
    private String construirRegistroAuditoria(Movimiento movimiento) {
        return String.format(
            "AUDIT|%s|CUENTA:%s|TIPO:%s|MONTO:%.2f|SALDO_ANT:%.2f|SALDO_POST:%.2f|DESC:%s",
            LocalDateTime.now().format(FORMATO),
            movimiento.getNumeroCuenta(),
            movimiento.getTipo(),
            movimiento.getMonto(),
            movimiento.getSaldoAnterior(),
            movimiento.getSaldoPosterior(),
            movimiento.getDescripcion()
        );
    }

    /**
     * Registra la entrada de auditoria.
     * En produccion, esto escribiria a un sistema de auditoria dedicado.
     */
    private void registrarAuditoria(String registro) {
        auditLogger.info("📋 {}", registro);

        // En produccion:
        // - auditoriaRepository.save(registro);
        // - elasticSearchClient.index(registro);
        // - kafkaProducer.send("auditoria-topic", registro);
    }

    @Override
    public String getNombre() {
        return "AuditoriaObserver";
    }

    @Override
    public int getPrioridad() {
        return 5; // Maxima prioridad - auditoria primero
    }
}
