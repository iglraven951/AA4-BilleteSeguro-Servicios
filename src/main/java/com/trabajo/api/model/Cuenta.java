package com.trabajo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD JPA: Representa una cuenta bancaria persistente en la base de datos.
 *
 * Anotaciones JPA utilizadas:
 * - @Entity: Marca la clase como entidad JPA (tabla en BD)
 * - @Table: Define el nombre de la tabla en la base de datos
 * - @Id: Define la clave primaria
 * - @GeneratedValue: Estrategia de generacion automatica del ID
 * - @Column: Configuracion de columnas (unique, nullable, length)
 * - @Enumerated: Para mapear enumeraciones a la BD
 * - @OneToMany: Relacion uno a muchos con Movimiento
 * - @PrePersist: Metodo ejecutado antes de insertar
 * - @PreUpdate: Metodo ejecutado antes de actualizar
 */
@Entity
@Table(name = "cuentas")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cuenta", unique = true, nullable = false, length = 20)
    private String numeroCuenta;

    @Column(nullable = false, length = 100)
    private String titular;

    @Column(nullable = false, length = 15)
    private String dni;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", length = 20)
    private TipoCuenta tipoCuenta = TipoCuenta.AHORRO;

    @Column(nullable = false, precision = 2)
    private Double saldo = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoCuenta estado = EstadoCuenta.ACTIVA;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * RELACION UNO A MUCHOS: Una cuenta puede tener muchos movimientos.
     *
     * - mappedBy: Indica que Movimiento es el lado propietario de la relacion
     * - cascade: Las operaciones en Cuenta se propagan a sus Movimientos
     * - fetch LAZY: Los movimientos se cargan solo cuando se acceden
     * - orphanRemoval: Si se elimina un movimiento de la lista, se borra de BD
     */
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Movimiento> movimientos = new ArrayList<>();

    // ==================== CONSTRUCTORES ====================

    public Cuenta() {
        // Constructor vacio requerido por JPA
    }

    public Cuenta(String titular, String dni, TipoCuenta tipoCuenta) {
        this.titular = titular;
        this.dni = dni;
        this.tipoCuenta = tipoCuenta;
        this.saldo = 0.0;
        this.estado = EstadoCuenta.ACTIVA;
    }

    // ==================== CALLBACKS JPA ====================

    /**
     * @PrePersist: Se ejecuta automaticamente ANTES de insertar en la BD.
     * Establece la fecha de creacion automaticamente.
     */
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.saldo == null) {
            this.saldo = 0.0;
        }
        if (this.estado == null) {
            this.estado = EstadoCuenta.ACTIVA;
        }
    }

    /**
     * @PreUpdate: Se ejecuta automaticamente ANTES de actualizar en la BD.
     * Actualiza la fecha de modificacion automaticamente.
     */
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ==================== METODOS DE NEGOCIO ====================

    /**
     * Agrega un movimiento a esta cuenta (lado bidireccional).
     */
    public void agregarMovimiento(Movimiento movimiento) {
        movimientos.add(movimiento);
        movimiento.setCuenta(this);
    }

    /**
     * Verifica si la cuenta esta activa para operaciones.
     */
    public boolean estaActiva() {
        return this.estado == EstadoCuenta.ACTIVA;
    }

    /**
     * Verifica si tiene saldo suficiente para un retiro.
     */
    public boolean tieneSaldoSuficiente(Double monto) {
        return this.saldo >= monto;
    }

    // ==================== GETTERS Y SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    // Metodo adicional para compatibilidad con String
    public void setTipoCuenta(String tipoCuenta) {
        if (tipoCuenta != null) {
            this.tipoCuenta = TipoCuenta.valueOf(tipoCuenta.toUpperCase());
        }
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public EstadoCuenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoCuenta estado) {
        this.estado = estado;
    }

    // Metodo adicional para compatibilidad con String
    public void setEstado(String estado) {
        if (estado != null) {
            this.estado = EstadoCuenta.valueOf(estado.toUpperCase());
        }
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }

    @Override
    public String toString() {
        return "Cuenta{" +
                "id=" + id +
                ", numeroCuenta='" + numeroCuenta + '\'' +
                ", titular='" + titular + '\'' +
                ", tipoCuenta=" + tipoCuenta +
                ", saldo=" + saldo +
                ", estado=" + estado +
                '}';
    }
}
