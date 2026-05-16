package com.trabajo.api.model;

/**
 * ENUM: Tipos de cuenta bancaria disponibles.
 *
 * Utilizado con @Enumerated(EnumType.STRING) en la entidad Cuenta
 * para almacenar el valor como texto en la base de datos.
 */
public enum TipoCuenta {

    AHORRO("Cuenta de Ahorro"),
    CORRIENTE("Cuenta Corriente"),
    PLAZO_FIJO("Cuenta a Plazo Fijo"),
    SUELDO("Cuenta Sueldo");

    private final String descripcion;

    TipoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
