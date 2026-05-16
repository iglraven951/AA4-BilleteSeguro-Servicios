package com.trabajo.api.pattern.behavioral;

import com.trabajo.api.model.Cuenta;
import com.trabajo.api.model.Movimiento;
import com.trabajo.api.model.TipoMovimiento;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * TEMPLATE METHOD: Implementacion de Deposito
 * ============================================================================
 *
 * Implementa la operacion de deposito usando el patron Template Method.
 * Solo necesita definir los pasos especificos de deposito.
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class DepositoOperacion extends OperacionBancariaTemplate {

    @Override
    protected Movimiento ejecutar(Cuenta cuenta, Double monto, String descripcion) {
        // Guardar saldo anterior
        Double saldoAnterior = cuenta.getSaldo();

        // Actualizar saldo
        cuenta.setSaldo(saldoAnterior + monto);

        // Crear movimiento
        Movimiento movimiento = new Movimiento();
        movimiento.setCuenta(cuenta);
        movimiento.setNumeroCuenta(cuenta.getNumeroCuenta());
        movimiento.setTipo(TipoMovimiento.DEPOSITO);
        movimiento.setMonto(monto);
        movimiento.setSaldoAnterior(saldoAnterior);
        movimiento.setSaldoPosterior(cuenta.getSaldo());
        movimiento.setDescripcion(descripcion != null ? descripcion : "Deposito en cuenta");

        return movimiento;
    }

    @Override
    protected String getNombreOperacion() {
        return "DEPOSITO";
    }

    @Override
    protected void postProcesarOperacion(Cuenta cuenta, Movimiento movimiento) {
        // Hook: Verificar si el deposito activa alguna promocion
        if (movimiento.getMonto() >= 1000) {
            logger.info("🎁 Deposito grande detectado! Cliente elegible para promociones.");
        }
    }
}
