package com.trabajo.api.pattern.behavioral;

import com.trabajo.api.model.Movimiento;
import com.trabajo.api.model.TipoMovimiento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * OBSERVER: Deteccion de Fraude
 * ============================================================================
 *
 * Observador que analiza movimientos para detectar patrones sospechosos
 * de fraude o actividad inusual.
 *
 * REGLAS DE DETECCION:
 * - Retiros mayores a S/. 5,000
 * - Transferencias mayores a S/. 10,000
 * - Multiples movimientos en corto tiempo (futuro)
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class FraudeObserver implements MovimientoObserver {

    private static final Logger logger = LoggerFactory.getLogger(FraudeObserver.class);
    private static final double UMBRAL_RETIRO = 5000.0;
    private static final double UMBRAL_TRANSFERENCIA = 10000.0;

    @Override
    public void onMovimientoRegistrado(Movimiento movimiento) {
        verificarRetiroGrande(movimiento);
        verificarTransferenciaGrande(movimiento);
        verificarPatronesInusuales(movimiento);
    }

    /**
     * Verifica retiros que superan el umbral.
     */
    private void verificarRetiroGrande(Movimiento movimiento) {
        if (movimiento.getTipo() == TipoMovimiento.RETIRO &&
            movimiento.getMonto() >= UMBRAL_RETIRO) {

            logger.warn("⚠️ ALERTA FRAUDE: Retiro grande detectado - Cuenta: {}, Monto: S/. {}",
                movimiento.getNumeroCuenta(),
                movimiento.getMonto());

            // En produccion: alertaService.enviarAlerta(movimiento);
        }
    }

    /**
     * Verifica transferencias que superan el umbral.
     */
    private void verificarTransferenciaGrande(Movimiento movimiento) {
        if (movimiento.getTipo() == TipoMovimiento.TRANSFERENCIA_ENVIADA &&
            movimiento.getMonto() >= UMBRAL_TRANSFERENCIA) {

            logger.warn("⚠️ ALERTA FRAUDE: Transferencia grande detectada - " +
                "Origen: {}, Destino: {}, Monto: S/. {}",
                movimiento.getNumeroCuenta(),
                movimiento.getCuentaDestino(),
                movimiento.getMonto());

            // En produccion: requeriria autorizacion adicional
        }
    }

    /**
     * Verifica patrones inusuales en el comportamiento.
     */
    private void verificarPatronesInusuales(Movimiento movimiento) {
        // Verificar si el saldo queda en cero (posible vaciado de cuenta)
        if (movimiento.getSaldoPosterior() != null &&
            movimiento.getSaldoPosterior() == 0 &&
            movimiento.getMonto() > 1000) {

            logger.warn("⚠️ ALERTA FRAUDE: Cuenta vaciada - Cuenta: {}, Monto retirado: S/. {}",
                movimiento.getNumeroCuenta(),
                movimiento.getMonto());
        }
    }

    @Override
    public String getNombre() {
        return "FraudeObserver";
    }

    @Override
    public int getPrioridad() {
        return 1; // Prioridad critica - verificar fraude inmediatamente
    }
}
