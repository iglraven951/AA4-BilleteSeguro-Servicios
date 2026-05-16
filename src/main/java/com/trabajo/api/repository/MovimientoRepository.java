package com.trabajo.api.repository;

import com.trabajo.api.model.Cuenta;
import com.trabajo.api.model.Movimiento;
import com.trabajo.api.model.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REPOSITORIO JPA: Interface de acceso a datos para la entidad Movimiento.
 *
 * Extiende JpaRepository que proporciona:
 * - Metodos CRUD basicos heredados
 * - Soporte para paginacion (Page, Pageable)
 * - Ordenamiento automatico
 *
 * Demuestra:
 * - Derived Query Methods con relaciones
 * - Consultas JPQL con @Query
 * - Paginacion
 * - Consultas con parametros nombrados
 */
@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // ==================== DERIVED QUERY METHODS ====================

    /**
     * Busca movimientos por numero de cuenta, ordenados por fecha descendente.
     */
    List<Movimiento> findByNumeroCuentaOrderByFechaDesc(String numeroCuenta);

    /**
     * Busca movimientos usando la RELACION con Cuenta.
     * Spring resuelve automaticamente la navegacion cuenta.id
     */
    List<Movimiento> findByCuentaOrderByFechaDesc(Cuenta cuenta);

    /**
     * Busca movimientos por cuenta (ID) ordenados por fecha.
     */
    List<Movimiento> findByCuentaIdOrderByFechaDesc(Long cuentaId);

    /**
     * Obtiene los ultimos N movimientos de una cuenta.
     * Top10 limita los resultados a 10.
     */
    List<Movimiento> findTop10ByNumeroCuentaOrderByFechaDesc(String numeroCuenta);

    /**
     * Busca movimientos por tipo.
     */
    List<Movimiento> findByTipo(TipoMovimiento tipo);

    /**
     * Busca movimientos de una cuenta por tipo.
     */
    List<Movimiento> findByNumeroCuentaAndTipo(String numeroCuenta, TipoMovimiento tipo);

    /**
     * Busca movimientos en un rango de fechas.
     */
    List<Movimiento> findByFechaBetweenOrderByFechaDesc(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Busca movimientos de una cuenta en un rango de fechas.
     */
    List<Movimiento> findByNumeroCuentaAndFechaBetweenOrderByFechaDesc(
            String numeroCuenta, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Cuenta los movimientos por tipo.
     */
    long countByTipo(TipoMovimiento tipo);

    /**
     * Cuenta los movimientos de una cuenta.
     */
    long countByNumeroCuenta(String numeroCuenta);

    // ==================== @QUERY - CONSULTAS JPQL ====================

    /**
     * Calcula el total de depositos de una cuenta.
     */
    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM Movimiento m WHERE m.numeroCuenta = :cuenta AND m.tipo = 'DEPOSITO'")
    Double calcularTotalDepositos(@Param("cuenta") String numeroCuenta);

    /**
     * Calcula el total de retiros de una cuenta.
     */
    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM Movimiento m WHERE m.numeroCuenta = :cuenta AND m.tipo = 'RETIRO'")
    Double calcularTotalRetiros(@Param("cuenta") String numeroCuenta);

    /**
     * Obtiene el resumen de movimientos por tipo para una cuenta.
     */
    @Query("SELECT m.tipo, COUNT(m), SUM(m.monto) FROM Movimiento m WHERE m.numeroCuenta = :cuenta GROUP BY m.tipo")
    List<Object[]> obtenerResumenPorTipo(@Param("cuenta") String numeroCuenta);

    /**
     * Busca movimientos con monto mayor al especificado.
     */
    @Query("SELECT m FROM Movimiento m WHERE m.monto >= :monto ORDER BY m.fecha DESC")
    List<Movimiento> findMovimientosGrandes(@Param("monto") Double montoMinimo);

    /**
     * Obtiene los ultimos movimientos de todas las cuentas (para auditoria).
     */
    @Query("SELECT m FROM Movimiento m ORDER BY m.fecha DESC")
    Page<Movimiento> findUltimosMovimientos(Pageable pageable);

    // ==================== CONSULTAS CON RELACIONES ====================

    /**
     * Busca movimientos usando la relacion @ManyToOne con Cuenta.
     * Navega: Movimiento -> Cuenta -> titular
     */
    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.titular LIKE %:titular% ORDER BY m.fecha DESC")
    List<Movimiento> findByTitularCuenta(@Param("titular") String titular);

    /**
     * Obtiene movimientos de cuentas activas.
     */
    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.estado = 'ACTIVA' ORDER BY m.fecha DESC")
    List<Movimiento> findMovimientosCuentasActivas();
}
