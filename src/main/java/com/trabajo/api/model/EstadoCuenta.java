package com.trabajo.api.model;

/**
 * ENUM: Estados posibles de una cuenta bancaria.
 *
 * Utilizado con @Enumerated(EnumType.STRING) en la entidad Cuenta
 * para almacenar el valor como texto en la base de datos.
 */
public enum EstadoCuenta {

    ACTIVA("Cuenta activa y operativa"),
    BLOQUEADA("Cuenta temporalmente bloqueada"),
    CERRADA("Cuenta cerrada permanentemente"),
    SUSPENDIDA("Cuenta suspendida por revision");

    private final String descripcion;

    EstadoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Verifica si la cuenta puede realizar operaciones.
     */
    public boolean permiteOperaciones() {
        return this == ACTIVA;
    }
}
