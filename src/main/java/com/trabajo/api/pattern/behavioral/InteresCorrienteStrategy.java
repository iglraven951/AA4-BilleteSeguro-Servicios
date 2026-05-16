package com.trabajo.api.pattern.behavioral;

import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * STRATEGY: Interes para Cuenta Corriente
 * ============================================================================
 *
 * Implementa la estrategia de calculo de interes para cuentas corrientes.
 * Las cuentas corrientes NO generan intereses (tasa 0%).
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component("interesCorrienteStrategy")
public class InteresCorrienteStrategy implements InteresStrategy {

    private static final double TASA_ANUAL = 0.0; // 0% - Sin intereses

    @Override
    public double calcularInteres(double saldo, int diasTranscurridos) {
        // Las cuentas corrientes no generan intereses
        return 0.0;
    }

    @Override
    public double getTasaAnual() {
        return TASA_ANUAL;
    }

    @Override
    public String getNombre() {
        return "Sin Interes (Cuenta Corriente)";
    }

    @Override
    public boolean aplicaIntereses() {
        return false;
    }
}
