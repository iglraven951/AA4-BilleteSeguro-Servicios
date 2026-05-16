package com.trabajo.api.pattern.behavioral;

import com.trabajo.api.model.Movimiento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ============================================================================
 * SUBJECT del patron Observer
 * ============================================================================
 *
 * El Subject (Sujeto) mantiene una lista de observadores y los notifica
 * cuando ocurre un evento relevante (nuevo movimiento bancario).
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class MovimientoSubject {

    private static final Logger logger = LoggerFactory.getLogger(MovimientoSubject.class);
    private final List<MovimientoObserver> observers;

    @Autowired
    public MovimientoSubject(List<MovimientoObserver> observers) {
        // Spring inyecta automaticamente todos los beans que implementan MovimientoObserver
        this.observers = new ArrayList<>(observers);
        // Ordenar por prioridad
        this.observers.sort(Comparator.comparingInt(MovimientoObserver::getPrioridad));

        logger.info("📢 MovimientoSubject inicializado con {} observadores:", observers.size());
        observers.forEach(o -> logger.info("   - {} (prioridad: {})", o.getNombre(), o.getPrioridad()));
    }

    /**
     * Notifica a todos los observadores sobre un nuevo movimiento.
     * Los observadores se ejecutan en orden de prioridad.
     *
     * @param movimiento El movimiento registrado
     */
    public void notificarMovimiento(Movimiento movimiento) {
        logger.info("🔔 Notificando movimiento a {} observadores...", observers.size());

        for (MovimientoObserver observer : observers) {
            try {
                observer.onMovimientoRegistrado(movimiento);
            } catch (Exception e) {
                // Un observador fallido no debe detener a los demas
                logger.error("Error en observador {}: {}", observer.getNombre(), e.getMessage());
            }
        }
    }

    /**
     * Registra un nuevo observador dinamicamente.
     */
    public void registrarObserver(MovimientoObserver observer) {
        observers.add(observer);
        observers.sort(Comparator.comparingInt(MovimientoObserver::getPrioridad));
        logger.info("➕ Observador registrado: {}", observer.getNombre());
    }

    /**
     * Elimina un observador.
     */
    public void eliminarObserver(MovimientoObserver observer) {
        observers.remove(observer);
        logger.info("➖ Observador eliminado: {}", observer.getNombre());
    }

    /**
     * Obtiene la cantidad de observadores registrados.
     */
    public int getCantidadObservers() {
        return observers.size();
    }
}
