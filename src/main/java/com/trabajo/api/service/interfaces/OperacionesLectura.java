package com.trabajo.api.service.interfaces;

import com.trabajo.api.model.Cuenta;
import com.trabajo.api.model.EstadoCuenta;
import com.trabajo.api.model.Movimiento;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * PRINCIPIO SOLID: INTERFACE SEGREGATION (I)
 * ============================================================================
 *
 * PROPOSITO:
 * Los clientes no deben verse forzados a depender de interfaces que no usan.
 * Es mejor tener muchas interfaces especificas que una interfaz general.
 *
 * APLICACION EN ESTE PROYECTO:
 * Separamos las operaciones de LECTURA de las de ESCRITURA.
 * - OperacionesLectura: Solo consultas (GET)
 * - OperacionesEscritura: Solo modificaciones (POST, PUT, DELETE)
 *
 * BENEFICIOS:
 * 1. Clientes que solo leen no dependen de metodos de escritura
 * 2. Facilita la implementacion de cache (lecturas son cacheables)
 * 3. Permite diferentes politicas de seguridad por tipo de operacion
 * 4. Mejora la testeabilidad
 *
 * PRINCIPIOS SOLID APLICADOS:
 * - Interface Segregation (I): Interfaces especificas por responsabilidad
 * - Single Responsibility (S): Solo operaciones de lectura
 *
 * @author Sistema Bancario
 * @version 1.0
 */
public interface OperacionesLectura {

    /**
     * Obtiene todas las cuentas del sistema.
     */
    List<Cuenta> obtenerTodasLasCuentas();

    /**
     * Obtiene una cuenta por su ID.
     */
    Optional<Cuenta> obtenerCuentaPorId(Long id);

    /**
     * Obtiene una cuenta por su numero.
     */
    Optional<Cuenta> obtenerCuentaPorNumero(String numeroCuenta);

    /**
     * Consulta el saldo de una cuenta.
     */
    Optional<Double> consultarSaldo(String numeroCuenta);

    /**
     * Obtiene los movimientos de una cuenta.
     */
    List<Movimiento> obtenerMovimientos(String numeroCuenta);

    /**
     * Obtiene los ultimos N movimientos de una cuenta.
     */
    List<Movimiento> obtenerUltimosMovimientos(String numeroCuenta, int cantidad);

    /**
     * Obtiene cuentas por estado.
     */
    List<Cuenta> obtenerCuentasPorEstado(EstadoCuenta estado);

    /**
     * Calcula el total de depositos de una cuenta.
     */
    Double obtenerTotalDepositos(String numeroCuenta);

    /**
     * Calcula el total de retiros de una cuenta.
     */
    Double obtenerTotalRetiros(String numeroCuenta);
}
