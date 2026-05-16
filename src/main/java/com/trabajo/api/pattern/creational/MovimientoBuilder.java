package com.trabajo.api.pattern.creational;

import com.trabajo.api.model.Cuenta;
import com.trabajo.api.model.Movimiento;
import com.trabajo.api.model.TipoMovimiento;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * PATRON CREACIONAL: BUILDER
 * ============================================================================
 *
 * PROPOSITO:
 * Separa la construccion de un objeto complejo de su representacion,
 * permitiendo que el mismo proceso de construccion pueda crear diferentes
 * representaciones.
 *
 * APLICACION EN ESTE PROYECTO:
 * MovimientoBuilder permite construir objetos Movimiento paso a paso,
 * con validaciones en cada etapa y calculo automatico de saldos.
 *
 * BENEFICIOS:
 * 1. Permite construir objetos complejos paso a paso
 * 2. Permite diferentes representaciones usando el mismo codigo de construccion
 * 3. Aisla el codigo de construccion complejo del codigo de negocio
 * 4. Mayor control sobre el proceso de construccion
 *
 * PRINCIPIO SOLID APLICADO:
 * - Single Responsibility (S): Solo construye movimientos
 * - Interface Segregation (I): API fluida con metodos especificos
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class MovimientoBuilder {

    private Movimiento movimiento;

    public MovimientoBuilder() {
        this.movimiento = new Movimiento();
    }

    /**
     * Inicia la construccion de un nuevo movimiento.
     * Reinicia el builder para permitir multiples construcciones.
     */
    public MovimientoBuilder nuevo() {
        this.movimiento = new Movimiento();
        return this;
    }

    /**
     * Establece la cuenta asociada al movimiento.
     */
    public MovimientoBuilder conCuenta(Cuenta cuenta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }
        this.movimiento.setCuenta(cuenta);
        this.movimiento.setNumeroCuenta(cuenta.getNumeroCuenta());
        this.movimiento.setSaldoAnterior(cuenta.getSaldo());
        return this;
    }

    /**
     * Establece el tipo de movimiento.
     */
    public MovimientoBuilder conTipo(TipoMovimiento tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de movimiento es requerido");
        }
        this.movimiento.setTipo(tipo);
        return this;
    }

    /**
     * Establece el monto del movimiento.
     */
    public MovimientoBuilder conMonto(Double monto) {
        if (monto == null || monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }
        this.movimiento.setMonto(monto);
        return this;
    }

    /**
     * Establece la descripcion del movimiento.
     */
    public MovimientoBuilder conDescripcion(String descripcion) {
        this.movimiento.setDescripcion(descripcion);
        return this;
    }

    /**
     * Establece la cuenta destino (para transferencias).
     */
    public MovimientoBuilder conCuentaDestino(String cuentaDestino) {
        this.movimiento.setCuentaDestino(cuentaDestino);
        return this;
    }

    /**
     * Establece la cuenta origen (para transferencias recibidas).
     */
    public MovimientoBuilder conCuentaOrigen(String cuentaOrigen) {
        this.movimiento.setCuentaOrigen(cuentaOrigen);
        return this;
    }

    /**
     * Calcula y establece el saldo posterior automaticamente.
     */
    public MovimientoBuilder calcularSaldoPosterior() {
        if (movimiento.getSaldoAnterior() != null &&
            movimiento.getMonto() != null &&
            movimiento.getTipo() != null) {

            double saldoPosterior;
            if (movimiento.getTipo().esIngreso()) {
                saldoPosterior = movimiento.getSaldoAnterior() + movimiento.getMonto();
            } else {
                saldoPosterior = movimiento.getSaldoAnterior() - movimiento.getMonto();
            }
            movimiento.setSaldoPosterior(saldoPosterior);
        }
        return this;
    }

    /**
     * Construye y retorna el movimiento final.
     * Realiza validaciones finales antes de retornar.
     */
    public Movimiento build() {
        validarMovimiento();
        calcularSaldoPosterior();
        Movimiento resultado = this.movimiento;
        this.movimiento = new Movimiento(); // Reset para siguiente uso
        return resultado;
    }

    /**
     * Valida que el movimiento tenga todos los campos requeridos.
     */
    private void validarMovimiento() {
        if (movimiento.getCuenta() == null) {
            throw new IllegalStateException("La cuenta es requerida");
        }
        if (movimiento.getTipo() == null) {
            throw new IllegalStateException("El tipo de movimiento es requerido");
        }
        if (movimiento.getMonto() == null || movimiento.getMonto() <= 0) {
            throw new IllegalStateException("El monto debe ser positivo");
        }
    }

    // =============== METODOS DE CONSTRUCCION RAPIDA ===============

    /**
     * Builder rapido para deposito.
     */
    public Movimiento buildDeposito(Cuenta cuenta, Double monto, String descripcion) {
        return nuevo()
            .conCuenta(cuenta)
            .conTipo(TipoMovimiento.DEPOSITO)
            .conMonto(monto)
            .conDescripcion(descripcion != null ? descripcion : "Deposito en cuenta")
            .build();
    }

    /**
     * Builder rapido para retiro.
     */
    public Movimiento buildRetiro(Cuenta cuenta, Double monto, String descripcion) {
        return nuevo()
            .conCuenta(cuenta)
            .conTipo(TipoMovimiento.RETIRO)
            .conMonto(monto)
            .conDescripcion(descripcion != null ? descripcion : "Retiro de cuenta")
            .build();
    }

    /**
     * Builder rapido para transferencia enviada.
     */
    public Movimiento buildTransferenciaEnviada(Cuenta cuenta, Double monto,
                                                  String cuentaDestino, String descripcion) {
        return nuevo()
            .conCuenta(cuenta)
            .conTipo(TipoMovimiento.TRANSFERENCIA_ENVIADA)
            .conMonto(monto)
            .conCuentaDestino(cuentaDestino)
            .conDescripcion(descripcion != null ? descripcion : "Transferencia a " + cuentaDestino)
            .build();
    }

    /**
     * Builder rapido para transferencia recibida.
     */
    public Movimiento buildTransferenciaRecibida(Cuenta cuenta, Double monto,
                                                   String cuentaOrigen, String descripcion) {
        return nuevo()
            .conCuenta(cuenta)
            .conTipo(TipoMovimiento.TRANSFERENCIA_RECIBIDA)
            .conMonto(monto)
            .conCuentaOrigen(cuentaOrigen)
            .conDescripcion(descripcion != null ? descripcion : "Transferencia de " + cuentaOrigen)
            .build();
    }
}
