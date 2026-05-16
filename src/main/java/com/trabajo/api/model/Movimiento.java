package com.trabajo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ENTIDAD JPA: Representa un movimiento/transaccion bancaria persistente en BD.
 *
 * Anotaciones JPA utilizadas:
 * - @Entity: Marca la clase como entidad JPA
 * - @Table: Define el nombre de la tabla y sus indices
 * - @Id, @GeneratedValue: Clave primaria auto-generada
 * - @Column: Configuracion de columnas
 * - @Enumerated: Para mapear el tipo de movimiento
 * - @ManyToOne: Relacion muchos a uno con Cuenta
 * - @JoinColumn: Define la columna de clave foranea
 * - @PrePersist: Callback antes de insertar
 */
@Entity
@Table(name = "movimientos", indexes = {
    @Index(name = "idx_movimiento_cuenta", columnList = "cuenta_id"),
    @Index(name = "idx_movimiento_fecha", columnList = "fecha"),
    @Index(name = "idx_movimiento_tipo", columnList = "tipo")
})
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * RELACION MUCHOS A UNO: Muchos movimientos pertenecen a una cuenta.
     *
     * - @ManyToOne: Define la relacion con Cuenta
     * - @JoinColumn: Especifica la columna FK en la tabla movimientos
     * - @JsonIgnore: Evita recursion infinita al serializar a JSON
     * - fetch LAZY: La cuenta se carga solo cuando se accede
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    @JsonIgnore
    private Cuenta cuenta;

    // Guardamos tambien el numero para consultas rapidas
    @Column(name = "numero_cuenta", nullable = false, length = 20)
    private String numeroCuenta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMovimiento tipo;

    @Column(nullable = false, precision = 2)
    private Double monto;

    @Column(name = "saldo_anterior", precision = 2)
    private Double saldoAnterior;

    @Column(name = "saldo_posterior", precision = 2)
    private Double saldoPosterior;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "cuenta_destino", length = 20)
    private String cuentaDestino;

    @Column(name = "cuenta_origen", length = 20)
    private String cuentaOrigen;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    // ==================== CONSTRUCTORES ====================

    public Movimiento() {
        // Constructor vacio requerido por JPA
    }

    public Movimiento(Cuenta cuenta, TipoMovimiento tipo, Double monto, String descripcion) {
        this.cuenta = cuenta;
        this.numeroCuenta = cuenta.getNumeroCuenta();
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.saldoAnterior = cuenta.getSaldo();
    }

    // ==================== CALLBACKS JPA ====================

    /**
     * @PrePersist: Se ejecuta automaticamente ANTES de insertar.
     * Establece la fecha del movimiento automaticamente.
     */
    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();
        if (this.cuenta != null && this.numeroCuenta == null) {
            this.numeroCuenta = this.cuenta.getNumeroCuenta();
        }
    }

    // ==================== METODOS DE NEGOCIO ====================

    /**
     * Calcula y establece el saldo posterior basado en el tipo de movimiento.
     */
    public void calcularSaldoPosterior() {
        if (this.saldoAnterior != null && this.monto != null && this.tipo != null) {
            if (this.tipo.esIngreso()) {
                this.saldoPosterior = this.saldoAnterior + this.monto;
            } else {
                this.saldoPosterior = this.saldoAnterior - this.monto;
            }
        }
    }

    // ==================== GETTERS Y SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
        if (cuenta != null) {
            this.numeroCuenta = cuenta.getNumeroCuenta();
        }
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    // Metodo adicional para compatibilidad con String
    public void setTipo(String tipo) {
        if (tipo != null) {
            this.tipo = TipoMovimiento.valueOf(tipo.toUpperCase());
        }
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Double getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(Double saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public Double getSaldoPosterior() {
        return saldoPosterior;
    }

    public void setSaldoPosterior(Double saldoPosterior) {
        this.saldoPosterior = saldoPosterior;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public String getCuentaOrigen() {
        return cuentaOrigen;
    }

    public void setCuentaOrigen(String cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Movimiento{" +
                "id=" + id +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                ", tipo=" + tipo +
                ", monto=" + monto +
                ", fecha=" + fecha +
                '}';
    }
}
