package com.trabajo.api.pattern.behavioral;

import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * STRATEGY: Interes para Cuenta de Ahorro
 * ============================================================================
 *
 * Implementa la estrategia de calculo de interes para cuentas de ahorro.
 * Tasa: 3% anual, capitalizable mensualmente.
 *
 * FORMULA: Interes = Saldo * (TasaAnual/365) * DiasTranscurridos
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component("interesAhorroStrategy")
public class InteresAhorroStrategy implements InteresStrategy {

    private static final double TASA_ANUAL = 0.03; // 3% anual
    private static final int DIAS_AÑO = 365;

    @Override
    public double calcularInteres(double saldo, int diasTranscurridos) {
        if (saldo <= 0 || diasTranscurridos <= 0) {
            return 0.0;
        }

        // Interes simple diario
        double tasaDiaria = TASA_ANUAL / DIAS_AÑO;
        double interes = saldo * tasaDiaria * diasTranscurridos;

        // Redondear a 2 decimales
        return Math.round(interes * 100.0) / 100.0;
    }

    @Override
    public double getTasaAnual() {
        return TASA_ANUAL;
    }

    @Override
    public String getNombre() {
        return "Interes Ahorro Basico (3% anual)";
    }
}
