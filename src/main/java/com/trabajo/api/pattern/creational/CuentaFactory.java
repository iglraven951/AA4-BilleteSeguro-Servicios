package com.trabajo.api.pattern.creational;

import com.trabajo.api.model.Cuenta;
import com.trabajo.api.model.TipoCuenta;
import com.trabajo.api.model.EstadoCuenta;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * PATRON CREACIONAL: FACTORY METHOD
 * ============================================================================
 *
 * PROPOSITO:
 * Define una interfaz para crear objetos, pero permite a las subclases
 * decidir que clase instanciar. Factory Method delega la instanciacion
 * a las subclases.
 *
 * APLICACION EN ESTE PROYECTO:
 * CuentaFactory crea diferentes tipos de cuentas bancarias (AHORRO, CORRIENTE,
 * PLAZO_FIJO) con configuraciones especificas sin exponer la logica de creacion.
 *
 * BENEFICIOS:
 * 1. Encapsula la logica de creacion de objetos
 * 2. Facilita agregar nuevos tipos de cuentas sin modificar codigo existente (OCP)
 * 3. Centraliza la validacion y configuracion inicial
 * 4. Promueve el principio de responsabilidad unica (SRP)
 *
 * PRINCIPIO SOLID APLICADO:
 * - Single Responsibility (S): Solo se encarga de crear cuentas
 * - Open/Closed (O): Abierto a extension, cerrado a modificacion
 *
 * @author Sistema Bancario
 * @version 1.0
 */
@Component
public class CuentaFactory {

    private static final double SALDO_MINIMO_CORRIENTE = 100.0;
    private static final double SALDO_MINIMO_PLAZO_FIJO = 500.0;
    private static long contadorCuentas = 1000000000L;

    /**
     * Metodo Factory principal que crea cuentas segun el tipo especificado.
     *
     * @param tipo Tipo de cuenta a crear
     * @param titular Nombre del titular
     * @param dni DNI del titular
     * @return Cuenta configurada segun el tipo
     */
    public Cuenta crearCuenta(TipoCuenta tipo, String titular, String dni) {
        validarDatosBasicos(titular, dni);

        return switch (tipo) {
            case AHORRO -> crearCuentaAhorro(titular, dni);
            case CORRIENTE -> crearCuentaCorriente(titular, dni);
            case PLAZO_FIJO -> crearCuentaPlazoFijo(titular, dni);
            case SUELDO -> crearCuentaSueldo(titular, dni);
        };
    }

    /**
     * Crea una cuenta de AHORRO con configuracion especifica.
     * - Sin saldo minimo requerido
     * - Genera intereses mensuales
     */
    private Cuenta crearCuentaAhorro(String titular, String dni) {
        Cuenta cuenta = new Cuenta();
        cuenta.setTitular(titular);
        cuenta.setDni(dni);
        cuenta.setTipoCuenta(TipoCuenta.AHORRO);
        cuenta.setSaldo(0.0);
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setNumeroCuenta(generarNumeroCuenta("AHO"));
        return cuenta;
    }

    /**
     * Crea una cuenta CORRIENTE con configuracion especifica.
     * - Requiere saldo minimo de mantenimiento
     * - Permite sobregiro limitado
     */
    private Cuenta crearCuentaCorriente(String titular, String dni) {
        Cuenta cuenta = new Cuenta();
        cuenta.setTitular(titular);
        cuenta.setDni(dni);
        cuenta.setTipoCuenta(TipoCuenta.CORRIENTE);
        cuenta.setSaldo(0.0);
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setNumeroCuenta(generarNumeroCuenta("COR"));
        return cuenta;
    }

    /**
     * Crea una cuenta PLAZO FIJO con configuracion especifica.
     * - Requiere monto minimo de apertura
     * - Mayor tasa de interes
     * - Restricciones de retiro
     */
    private Cuenta crearCuentaPlazoFijo(String titular, String dni) {
        Cuenta cuenta = new Cuenta();
        cuenta.setTitular(titular);
        cuenta.setDni(dni);
        cuenta.setTipoCuenta(TipoCuenta.PLAZO_FIJO);
        cuenta.setSaldo(0.0);
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setNumeroCuenta(generarNumeroCuenta("PFJ"));
        return cuenta;
    }

    /**
     * Crea una cuenta SUELDO con configuracion especifica.
     * - Sin comisiones de mantenimiento
     * - Deposito automatico de nomina
     */
    private Cuenta crearCuentaSueldo(String titular, String dni) {
        Cuenta cuenta = new Cuenta();
        cuenta.setTitular(titular);
        cuenta.setDni(dni);
        cuenta.setTipoCuenta(TipoCuenta.SUELDO);
        cuenta.setSaldo(0.0);
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setNumeroCuenta(generarNumeroCuenta("SUE"));
        return cuenta;
    }

    /**
     * Genera un numero de cuenta unico con prefijo segun el tipo.
     */
    private String generarNumeroCuenta(String prefijo) {
        return prefijo + String.format("%010d", ++contadorCuentas);
    }

    /**
     * Valida los datos basicos del titular.
     */
    private void validarDatosBasicos(String titular, String dni) {
        if (titular == null || titular.trim().isEmpty()) {
            throw new IllegalArgumentException("El titular es requerido");
        }
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI es requerido");
        }
    }

    /**
     * Obtiene el saldo minimo requerido segun el tipo de cuenta.
     */
    public double obtenerSaldoMinimo(TipoCuenta tipo) {
        return switch (tipo) {
            case AHORRO -> 0.0;
            case CORRIENTE -> SALDO_MINIMO_CORRIENTE;
            case PLAZO_FIJO -> SALDO_MINIMO_PLAZO_FIJO;
            case SUELDO -> 0.0;
        };
    }
}
