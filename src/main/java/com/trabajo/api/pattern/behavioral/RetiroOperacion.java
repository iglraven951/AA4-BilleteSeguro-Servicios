package com.trabajo.api.pattern.behavioral;

import com.trabajo.api.model.Cuenta;
import com.trabajo.api.model.Movimiento;
import com.trabajo.api.model.TipoMovimiento;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * TEMPLATE METHOD: Implementacion de Retiro
 * ============================================================================
 *
 * Implementa la operacion de retiro usando el patron Template Method.
 * Incluye validacion especifica de saldo suficiente.
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class RetiroOperacion extends OperacionBancariaTemplate {

    @Override
    protected void validarOperacionEspecifica(Cuenta cuenta, Double monto) {
        // Validacion especifica de retiro: verificar saldo suficiente
        if (!cuenta.tieneSaldoSuficiente(monto)) {
            throw new OperacionInvalidaException(
                String.format("Saldo insuficiente. Disponible: %.2f, Solicitado: %.2f",
                    cuenta.getSaldo(), monto)
            );
        }
    }

    @Override
    protected Movimiento ejecutar(Cuenta cuenta, Double monto, String descripcion) {
        // Guardar saldo anterior
        Double saldoAnterior = cuenta.getSaldo();

        // Actualizar saldo
        cuenta.setSaldo(saldoAnterior - monto);

        // Crear movimiento
        Movimiento movimiento = new Movimiento();
        movimiento.setCuenta(cuenta);
        movimiento.setNumeroCuenta(cuenta.getNumeroCuenta());
        movimiento.setTipo(TipoMovimiento.RETIRO);
        movimiento.setMonto(monto);
        movimiento.setSaldoAnterior(saldoAnterior);
        movimiento.setSaldoPosterior(cuenta.getSaldo());
        movimiento.setDescripcion(descripcion != null ? descripcion : "Retiro de cuenta");

        return movimiento;
    }

    @Override
    protected String getNombreOperacion() {
        return "RETIRO";
    }

    @Override
    protected void postProcesarOperacion(Cuenta cuenta, Movimiento movimiento) {
        // Hook: Alerta si el saldo queda bajo
        if (cuenta.getSaldo() < 100) {
            logger.warn("⚠️ ALERTA: Saldo bajo en cuenta {}. Saldo actual: {}",
                cuenta.getNumeroCuenta(), cuenta.getSaldo());
        }
    }
}
