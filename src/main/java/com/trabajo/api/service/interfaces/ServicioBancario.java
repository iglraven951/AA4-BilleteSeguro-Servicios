package com.trabajo.api.service.interfaces;

/**
 * ============================================================================
 * PRINCIPIO SOLID: DEPENDENCY INVERSION (D)
 * ============================================================================
 *
 * PROPOSITO:
 * Los modulos de alto nivel no deben depender de modulos de bajo nivel.
 * Ambos deben depender de abstracciones (interfaces).
 *
 * APLICACION EN ESTE PROYECTO:
 * ServicioBancario es la abstraccion principal que combina todas las
 * operaciones bancarias. Los controladores dependen de esta interfaz,
 * no de la implementacion concreta (BancoService).
 *
 * BENEFICIOS:
 * 1. Facilita el testing (mock de interfaces)
 * 2. Permite cambiar implementaciones sin afectar clientes
 * 3. Reduce acoplamiento entre capas
 * 4. Facilita la inyeccion de dependencias
 *
 * PRINCIPIOS SOLID APLICADOS:
 * - Dependency Inversion (D): Depender de abstracciones
 * - Interface Segregation (I): Combina interfaces segregadas
 * - Liskov Substitution (L): Cualquier implementacion es sustituible
 *
 * @author Sistema Bancario
 * @version 1.0
 */
public interface ServicioBancario extends OperacionesLectura, OperacionesEscritura {

    /**
     * Obtiene el nombre del servicio.
     * Util para logging y monitoreo.
     */
    default String getNombreServicio() {
        return "ServicioBancario";
    }

    /**
     * Verifica si el servicio esta disponible.
     * Util para health checks.
     */
    default boolean estaDisponible() {
        return true;
    }
}
