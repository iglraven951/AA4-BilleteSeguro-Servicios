package com.trabajo.api.model;

/**
 * DTO: Request para realizar transferencias entre cuentas.
 */
public class TransferenciaRequest {

    private String cuentaOrigen;
    private String cuentaDestino;
    private Double monto;
    private String descripcion;

    // Constructor vacío
    public TransferenciaRequest() {
    }

    // Constructor completo
    public TransferenciaRequest(String cuentaOrigen, String cuentaDestino, Double monto, String descripcion) {
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.monto = monto;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public String getCuentaOrigen() {
        return cuentaOrigen;
    }

    public void setCuentaOrigen(String cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
