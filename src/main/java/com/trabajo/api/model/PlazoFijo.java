package com.trabajo.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "plazos_fijos")
public class PlazoFijo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;

    @Column(nullable = false)
    private Double montoInicial;

    @Column(nullable = false)
    private Double tasaInteres;

    @Column(nullable = false)
    private Integer plazoMeses;

    @Column(nullable = false)
    private Double interesGenerado;

    @Column(nullable = false)
    private Double montoFinal;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPlazoFijo estado;

    @Column
    private LocalDateTime fechaCancelacion;

    @PrePersist
    protected void onCreate() {
        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPlazoFijo.ACTIVO;
        }
        calcularInteres();
    }

    public void calcularInteres() {
        double tasaMensual = tasaInteres / 100 / 12;
        this.interesGenerado = montoInicial * tasaMensual * plazoMeses;
        this.montoFinal = montoInicial + interesGenerado;
        this.fechaVencimiento = fechaInicio.plusMonths(plazoMeses);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cuenta getCuenta() { return cuenta; }
    public void setCuenta(Cuenta cuenta) { this.cuenta = cuenta; }

    public Double getMontoInicial() { return montoInicial; }
    public void setMontoInicial(Double montoInicial) { this.montoInicial = montoInicial; }

    public Double getTasaInteres() { return tasaInteres; }
    public void setTasaInteres(Double tasaInteres) { this.tasaInteres = tasaInteres; }

    public Integer getPlazoMeses() { return plazoMeses; }
    public void setPlazoMeses(Integer plazoMeses) { this.plazoMeses = plazoMeses; }

    public Double getInteresGenerado() { return interesGenerado; }
    public void setInteresGenerado(Double interesGenerado) { this.interesGenerado = interesGenerado; }

    public Double getMontoFinal() { return montoFinal; }
    public void setMontoFinal(Double montoFinal) { this.montoFinal = montoFinal; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDateTime fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public EstadoPlazoFijo getEstado() { return estado; }
    public void setEstado(EstadoPlazoFijo estado) { this.estado = estado; }

    public LocalDateTime getFechaCancelacion() { return fechaCancelacion; }
    public void setFechaCancelacion(LocalDateTime fechaCancelacion) { this.fechaCancelacion = fechaCancelacion; }
}
