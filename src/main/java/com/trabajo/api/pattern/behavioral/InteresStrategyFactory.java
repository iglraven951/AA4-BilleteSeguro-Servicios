package com.trabajo.api.pattern.behavioral;

import com.trabajo.api.model.TipoCuenta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * FACTORY para Strategy de Intereses
 * ============================================================================
 *
 * Combina los patrones Factory y Strategy para seleccionar la estrategia
 * correcta basada en el tipo de cuenta.
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class InteresStrategyFactory {

    private final InteresAhorroStrategy ahorroStrategy;
    private final InteresCorrienteStrategy corrienteStrategy;
    private final InteresPlazoFijoStrategy plazoFijoStrategy;

    @Autowired
    public InteresStrategyFactory(
            InteresAhorroStrategy ahorroStrategy,
            InteresCorrienteStrategy corrienteStrategy,
            InteresPlazoFijoStrategy plazoFijoStrategy) {
        this.ahorroStrategy = ahorroStrategy;
        this.corrienteStrategy = corrienteStrategy;
        this.plazoFijoStrategy = plazoFijoStrategy;
    }

    /**
     * Obtiene la estrategia de interes apropiada segun el tipo de cuenta.
     *
     * @param tipoCuenta Tipo de cuenta bancaria
     * @return Estrategia de interes correspondiente
     */
    public InteresStrategy getStrategy(TipoCuenta tipoCuenta) {
        return switch (tipoCuenta) {
            case AHORRO -> ahorroStrategy;
            case CORRIENTE -> corrienteStrategy;
            case PLAZO_FIJO -> plazoFijoStrategy;
            case SUELDO -> corrienteStrategy; // Sueldo usa misma estrategia (0% interes)
        };
    }

    /**
     * Calcula el interes para una cuenta especifica.
     * Metodo de conveniencia que combina seleccion y calculo.
     */
    public double calcularInteres(TipoCuenta tipoCuenta, double saldo, int dias) {
        InteresStrategy strategy = getStrategy(tipoCuenta);
        return strategy.calcularInteres(saldo, dias);
    }
}
