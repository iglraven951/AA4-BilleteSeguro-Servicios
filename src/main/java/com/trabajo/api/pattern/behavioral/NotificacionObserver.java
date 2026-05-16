package com.trabajo.api.pattern.behavioral;

import com.trabajo.api.model.Movimiento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * OBSERVER: Notificaciones al Cliente
 * ============================================================================
 *
 * Observador que envia notificaciones al cliente cuando se realizan
 * movimientos en su cuenta. Simula envio de SMS, email o push notifications.
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class NotificacionObserver implements MovimientoObserver {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionObserver.class);

    @Override
    public void onMovimientoRegistrado(Movimiento movimiento) {
        String mensaje = construirMensaje(movimiento);
        enviarNotificacion(movimiento.getNumeroCuenta(), mensaje);
    }

    /**
     * Construye el mensaje de notificacion segun el tipo de movimiento.
     */
    private String construirMensaje(Movimiento movimiento) {
        String tipoOperacion = movimiento.getTipo().getDescripcion();
        double monto = movimiento.getMonto();
        double saldoActual = movimiento.getSaldoPosterior();

        return String.format(
            "[BILLETERA SEGURO] %s por S/. %.2f. Nuevo saldo: S/. %.2f. Fecha: %s",
            tipoOperacion,
            monto,
            saldoActual,
            movimiento.getFecha()
        );
    }

    /**
     * Simula el envio de notificacion al cliente.
     * En produccion, esto conectaria con un servicio de SMS/Email/Push.
     */
    private void enviarNotificacion(String numeroCuenta, String mensaje) {
        // Simulacion de envio de notificacion
        logger.info("📱 NOTIFICACION enviada a cuenta {}: {}", numeroCuenta, mensaje);

        // Aqui iria la integracion con servicio real:
        // - smsService.enviar(telefono, mensaje);
        // - emailService.enviar(email, mensaje);
        // - pushService.enviar(token, mensaje);
    }

    @Override
    public String getNombre() {
        return "NotificacionObserver";
    }

    @Override
    public int getPrioridad() {
        return 10; // Alta prioridad - notificar primero
    }
}
