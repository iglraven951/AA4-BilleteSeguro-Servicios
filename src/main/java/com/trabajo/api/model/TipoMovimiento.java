package com.trabajo.api.model;

/**
 * ENUM: Tipos de movimiento/transaccion bancaria.
 *
 * Utilizado con @Enumerated(EnumType.STRING) en la entidad Movimiento
 * para almacenar el valor como texto en la base de datos.
 */
public enum TipoMovimiento {

    DEPOSITO("Deposito en cuenta", true),
    RETIRO("Retiro de cuenta", false),
    TRANSFERENCIA_ENVIADA("Transferencia enviada", false),
    TRANSFERENCIA_RECIBIDA("Transferencia recibida", true),
    PAGO_SERVICIO("Pago de servicio", false),
    INTERES("Interes generado", true);

    private final String descripcion;
    private final boolean esIngreso;

    TipoMovimiento(String descripcion, boolean esIngreso) {
        this.descripcion = descripcion;
        this.esIngreso = esIngreso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Indica si este tipo de movimiento suma dinero a la cuenta.
     */
    public boolean esIngreso() {
        return esIngreso;
    }

    /**
     * Indica si este tipo de movimiento resta dinero de la cuenta.
     */
    public boolean esEgreso() {
        return !esIngreso;
    }
}
