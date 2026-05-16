package com.trabajo.api.pattern.behavioral;

import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * STRATEGY: Interes para Plazo Fijo
 * ============================================================================
 *
 * Implementa la estrategia de calculo de interes para cuentas de plazo fijo.
 * Tasa: 8% anual - La mas alta por el compromiso de tiempo.
 *
 * FORMULA: Interes = Saldo * (TasaAnual/365) * DiasTranscurridos
 * BONUS: +0.5% adicional si el saldo supera los 10,000
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component("interesPlazoFijoStrategy")
public class InteresPlazoFijoStrategy implements InteresStrategy {

    private static final double TASA_ANUAL = 0.08; // 8% anual
    private static final double TASA_BONUS = 0.005; // 0.5% adicional
    private static final double UMBRAL_BONUS = 10000.0;
    private static final int DIAS_AÑO = 365;

    @Override
    public double calcularInteres(double saldo, int diasTranscurridos) {
        if (saldo <= 0 || diasTranscurridos <= 0) {
            return 0.0;
        }

        // Calcular tasa efectiva (con bonus si aplica)
        double tasaEfectiva = TASA_ANUAL;
        if (saldo >= UMBRAL_BONUS) {
            tasaEfectiva += TASA_BONUS;
        }

        // Interes con tasa premium
        double tasaDiaria = tasaEfectiva / DIAS_AÑO;
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
        return "Interes Plazo Fijo Premium (8% anual + bonus)";
    }

    /**
     * Calcula la tasa efectiva considerando bonificaciones.
     */
    public double getTasaEfectiva(double saldo) {
        if (saldo >= UMBRAL_BONUS) {
            return TASA_ANUAL + TASA_BONUS;
        }
        return TASA_ANUAL;
    }
}
