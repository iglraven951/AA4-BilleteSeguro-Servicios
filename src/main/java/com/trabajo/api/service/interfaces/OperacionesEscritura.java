package com.trabajo.api.service.interfaces;

import com.trabajo.api.model.Cuenta;
import com.trabajo.api.model.Movimiento;
import com.trabajo.api.model.TransferenciaRequest;

import java.util.Optional;

/**
 * ============================================================================
 * PRINCIPIO SOLID: INTERFACE SEGREGATION (I)
 * ============================================================================
 *
 * PROPOSITO:
 * Interfaz segregada para operaciones de ESCRITURA (modificaciones).
 * Complementa a OperacionesLectura para una separacion clara de responsabilidades.
 *
 * APLICACION EN ESTE PROYECTO:
 * Define todas las operaciones que modifican el estado del sistema:
 * - Crear/Actualizar cuentas
 * - Depositos, Retiros, Transferencias
 * - Cambios de estado
 *
 * BENEFICIOS:
 * 1. Servicios de reportes solo necesitan OperacionesLectura
 * 2. Facilita auditorias (todas las escrituras en una interfaz)
 * 3. Permite transaccionalidad granular
 * 4. Mejor control de permisos
 *
 * PRINCIPIOS SOLID APLICADOS:
 * - Interface Segregation (I): Separacion lectura/escritura
 * - Single Responsibility (S): Solo operaciones de escritura
 * - Open/Closed (O): Nuevas operaciones sin modificar implementaciones
 *
 * @author Sistema Bancario
 * @version 1.0
 */
public interface OperacionesEscritura {

    /**
     * Crea una nueva cuenta bancaria.
     */
    Cuenta crearCuenta(Cuenta cuenta);

    /**
     * Actualiza los datos de una cuenta existente.
     */
    Optional<Cuenta> actualizarCuenta(Long id, Cuenta cuenta);

    /**
     * Cambia el estado de una cuenta.
     */
    Optional<Cuenta> cambiarEstadoCuenta(String numeroCuenta, String nuevoEstado);

    /**
     * Realiza un deposito en una cuenta.
     */
    Optional<Movimiento> depositar(String numeroCuenta, Double monto, String descripcion);

    /**
     * Realiza un retiro de una cuenta.
     */
    Optional<Movimiento> retirar(String numeroCuenta, Double monto, String descripcion);

    /**
     * Realiza una transferencia entre cuentas.
     */
    Optional<Movimiento> transferir(TransferenciaRequest request);
}
