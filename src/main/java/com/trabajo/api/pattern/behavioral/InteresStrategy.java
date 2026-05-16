package com.trabajo.api.pattern.behavioral;

import com.trabajo.api.model.TipoCuenta;

/**
 * ============================================================================
 * PATRON DE COMPORTAMIENTO: STRATEGY
 * ============================================================================
 *
 * PROPOSITO:
 * Define una familia de algoritmos, encapsula cada uno de ellos y los hace
 * intercambiables. Strategy permite que el algoritmo varie independientemente
 * de los clientes que lo utilizan.
 *
 * APLICACION EN ESTE PROYECTO:
 * Diferentes estrategias de calculo de intereses segun el tipo de cuenta:
 * - CuentaAhorro: Interes basico (3% anual)
 * - CuentaCorriente: Sin intereses
 * - PlazoFijo: Interes premium (8% anual)
 *
 * BENEFICIOS:
 * 1. Elimina condicionales complejos (switch/if-else)
 * 2. Facilita agregar nuevas estrategias sin modificar codigo existente
 * 3. Permite cambiar algoritmos en tiempo de ejecucion
 * 4. Promueve el principio Open/Closed
 *
 * PRINCIPIO SOLID APLICADO:
 * - Open/Closed (O): Abierto a nuevas estrategias, cerrado a modificacion
 * - Liskov Substitution (L): Cualquier estrategia puede sustituir a otra
 * - Interface Segregation (I): Interface especifica para calculo de interes
 *
 * @author Sistema Bancario
 * @version 1.0
 */
public interface InteresStrategy {

    /**
     * Calcula el interes a aplicar sobre un saldo.
     *
     * @param saldo Saldo actual de la cuenta
     * @param diasTranscurridos Dias desde la ultima aplicacion de interes
     * @return Monto de interes calculado
     */
    double calcularInteres(double saldo, int diasTranscurridos);

    /**
     * Obtiene la tasa de interes anual de esta estrategia.
     *
     * @return Tasa de interes anual (ej: 0.03 para 3%)
     */
    double getTasaAnual();

    /**
     * Obtiene el nombre descriptivo de la estrategia.
     *
     * @return Nombre de la estrategia
     */
    String getNombre();

    /**
     * Indica si esta estrategia aplica intereses.
     *
     * @return true si genera intereses, false si no
     */
    default boolean aplicaIntereses() {
        return getTasaAnual() > 0;
    }
}
