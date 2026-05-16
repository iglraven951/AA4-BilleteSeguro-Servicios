package com.trabajo.api.repository;

import com.trabajo.api.model.Cuenta;
import com.trabajo.api.model.EstadoCuenta;
import com.trabajo.api.model.TipoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO JPA: Interface de acceso a datos para la entidad Cuenta.
 *
 * Extiende JpaRepository que proporciona:
 * - Metodos CRUD basicos (save, findById, findAll, delete, etc.)
 * - Paginacion y ordenamiento
 * - Metodos de conteo y existencia
 *
 * Metodos personalizados:
 * - Derived Query Methods: Spring genera la consulta basada en el nombre
 * - @Query: Consultas JPQL o SQL nativas personalizadas
 * - @Modifying: Para consultas UPDATE o DELETE
 */
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    // ==================== DERIVED QUERY METHODS ====================
    // Spring Data JPA genera la implementacion automaticamente

    /**
     * Busca una cuenta por su numero unico.
     * Equivale a: SELECT c FROM Cuenta c WHERE c.numeroCuenta = ?1
     */
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    /**
     * Busca todas las cuentas de un titular por su DNI.
     * Equivale a: SELECT c FROM Cuenta c WHERE c.dni = ?1
     */
    List<Cuenta> findByDni(String dni);

    /**
     * Busca cuentas por estado.
     * Equivale a: SELECT c FROM Cuenta c WHERE c.estado = ?1
     */
    List<Cuenta> findByEstado(EstadoCuenta estado);

    /**
     * Busca cuentas por tipo.
     * Equivale a: SELECT c FROM Cuenta c WHERE c.tipoCuenta = ?1
     */
    List<Cuenta> findByTipoCuenta(TipoCuenta tipoCuenta);

    /**
     * Busca cuentas por estado y tipo.
     */
    List<Cuenta> findByEstadoAndTipoCuenta(EstadoCuenta estado, TipoCuenta tipoCuenta);

    /**
     * Verifica si existe una cuenta con ese numero.
     */
    boolean existsByNumeroCuenta(String numeroCuenta);

    /**
     * Busca cuentas cuyo titular contenga el texto (LIKE).
     */
    List<Cuenta> findByTitularContainingIgnoreCase(String nombre);

    /**
     * Busca cuentas con saldo mayor o igual al especificado.
     */
    List<Cuenta> findBySaldoGreaterThanEqual(Double saldo);

    /**
     * Cuenta las cuentas por estado.
     */
    long countByEstado(EstadoCuenta estado);

    // ==================== @QUERY - CONSULTAS JPQL ====================

    /**
     * Obtiene el maximo ID para generar numeros de cuenta.
     */
    @Query("SELECT MAX(c.id) FROM Cuenta c")
    Long findMaxId();

    /**
     * Busca cuentas activas con saldo positivo.
     */
    @Query("SELECT c FROM Cuenta c WHERE c.estado = 'ACTIVA' AND c.saldo > 0 ORDER BY c.saldo DESC")
    List<Cuenta> findCuentasActivasConSaldo();

    /**
     * Calcula el saldo total de todas las cuentas activas.
     */
    @Query("SELECT SUM(c.saldo) FROM Cuenta c WHERE c.estado = 'ACTIVA'")
    Double calcularSaldoTotalActivas();

    /**
     * Busca cuentas por rango de saldo.
     */
    @Query("SELECT c FROM Cuenta c WHERE c.saldo BETWEEN :min AND :max ORDER BY c.saldo")
    List<Cuenta> findBySaldoBetween(@Param("min") Double saldoMinimo, @Param("max") Double saldoMaximo);

    // ==================== @MODIFYING - CONSULTAS DE ACTUALIZACION ====================

    /**
     * Actualiza el estado de una cuenta por su numero.
     * @Modifying indica que es una operacion de escritura.
     */
    @Modifying
    @Query("UPDATE Cuenta c SET c.estado = :estado WHERE c.numeroCuenta = :numero")
    int actualizarEstadoPorNumero(@Param("numero") String numeroCuenta, @Param("estado") EstadoCuenta estado);

    /**
     * Bloquea todas las cuentas con saldo negativo (seguridad).
     */
    @Modifying
    @Query("UPDATE Cuenta c SET c.estado = 'BLOQUEADA' WHERE c.saldo < 0")
    int bloquearCuentasConSaldoNegativo();
}
