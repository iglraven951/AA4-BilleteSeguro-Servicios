package com.trabajo.api.pattern.behavioral;

import com.trabajo.api.model.Movimiento;

/**
 * ============================================================================
 * PATRON DE COMPORTAMIENTO: OBSERVER
 * ============================================================================
 *
 * PROPOSITO:
 * Define una dependencia de uno a muchos entre objetos, de modo que cuando
 * un objeto cambia de estado, todos sus dependientes son notificados y
 * actualizados automaticamente.
 *
 * APLICACION EN ESTE PROYECTO:
 * Cuando se realiza un movimiento bancario (deposito, retiro, transferencia),
 * multiples observadores son notificados para realizar diferentes acciones:
 * - Enviar notificaciones al cliente
 * - Registrar en log de auditoria
 * - Verificar alertas de fraude
 * - Actualizar estadisticas
 *
 * BENEFICIOS:
 * 1. Bajo acoplamiento entre el sujeto y los observadores
 * 2. Permite agregar observadores dinamicamente
 * 3. Soporte para broadcast de eventos
 * 4. Facilita la implementacion de eventos del sistema
 *
 * PRINCIPIO SOLID APLICADO:
 * - Open/Closed (O): Nuevos observadores sin modificar el sujeto
 * - Single Responsibility (S): Cada observador hace una cosa
 * - Dependency Inversion (D): Depende de la abstraccion Observer
 *
 * @author Sistema Bancario
 * @version 1.0
 */
public interface MovimientoObserver {

    /**
     * Metodo invocado cuando se registra un nuevo movimiento.
     *
     * @param movimiento El movimiento bancario realizado
     */
    void onMovimientoRegistrado(Movimiento movimiento);

    /**
     * Obtiene el nombre del observador para logging.
     *
     * @return Nombre identificador del observador
     */
    String getNombre();

    /**
     * Indica la prioridad del observador (menor = mas prioritario).
     * Util para ordenar la ejecucion de observadores.
     *
     * @return Numero de prioridad
     */
    default int getPrioridad() {
        return 100;
    }
}
