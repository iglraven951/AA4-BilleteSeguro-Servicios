package com.trabajo.api.pattern.behavioral;

import com.trabajo.api.model.Cuenta;
import com.trabajo.api.model.Movimiento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ============================================================================
 * PATRON DE COMPORTAMIENTO: TEMPLATE METHOD
 * ============================================================================
 *
 * PROPOSITO:
 * Define el esqueleto de un algoritmo en una operacion, delegando algunos
 * pasos a las subclases. Template Method permite que las subclases redefinan
 * ciertos pasos de un algoritmo sin cambiar su estructura.
 *
 * APLICACION EN ESTE PROYECTO:
 * Define el flujo estandar para todas las operaciones bancarias:
 * 1. Validar la operacion
 * 2. Preparar la operacion
 * 3. Ejecutar la operacion (paso abstracto - cada operacion lo implementa)
 * 4. Registrar el movimiento
 * 5. Notificar (opcional)
 *
 * BENEFICIOS:
 * 1. Reutiliza codigo comun en la clase base
 * 2. Las subclases solo implementan los pasos especificos
 * 3. Controla los puntos de extension
 * 4. Evita duplicacion de codigo
 *
 * PRINCIPIO SOLID APLICADO:
 * - Open/Closed (O): Nuevas operaciones sin modificar el template
 * - Single Responsibility (S): Cada subclase implementa una operacion
 * - Liskov Substitution (L): Cualquier operacion puede ejecutarse
 *
 * @author Sistema Bancario
 * @version 1.0
 */
public abstract class OperacionBancariaTemplate {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * METODO TEMPLATE: Define el algoritmo de la operacion bancaria.
     * Este metodo es FINAL - no puede ser sobrescrito por subclases.
     * Las subclases solo pueden sobrescribir los pasos especificos.
     *
     * @param cuenta Cuenta sobre la que se realiza la operacion
     * @param monto Monto de la operacion
     * @param descripcion Descripcion de la operacion
     * @return Movimiento generado por la operacion
     * @throws OperacionInvalidaException Si la operacion no puede realizarse
     */
    public final Movimiento ejecutarOperacion(Cuenta cuenta, Double monto, String descripcion) {
        logger.info("🏦 Iniciando operacion: {} para cuenta {}", getNombreOperacion(), cuenta.getNumeroCuenta());

        // Paso 1: Validar (comun + especifico)
        validarOperacionBase(cuenta, monto);
        validarOperacionEspecifica(cuenta, monto);

        // Paso 2: Preparar (hook - opcional)
        prepararOperacion(cuenta, monto);

        // Paso 3: Ejecutar (abstracto - cada operacion lo implementa)
        Movimiento movimiento = ejecutar(cuenta, monto, descripcion);

        // Paso 4: Post-proceso (hook - opcional)
        postProcesarOperacion(cuenta, movimiento);

        // Paso 5: Registrar
        registrarOperacion(movimiento);

        logger.info("✅ Operacion completada: {} - Nuevo saldo: {}",
            getNombreOperacion(), cuenta.getSaldo());

        return movimiento;
    }

    // ==================== METODOS COMUNES (NO SOBRESCRIBIR) ====================

    /**
     * Validaciones basicas comunes a todas las operaciones.
     */
    private void validarOperacionBase(Cuenta cuenta, Double monto) {
        if (cuenta == null) {
            throw new OperacionInvalidaException("La cuenta es requerida");
        }
        if (!cuenta.estaActiva()) {
            throw new OperacionInvalidaException("La cuenta no esta activa");
        }
        if (monto == null || monto <= 0) {
            throw new OperacionInvalidaException("El monto debe ser positivo");
        }
    }

    /**
     * Registro comun de la operacion.
     */
    private void registrarOperacion(Movimiento movimiento) {
        logger.info("📝 Registrado: {} | Monto: {} | Saldo anterior: {} | Saldo posterior: {}",
            movimiento.getTipo(),
            movimiento.getMonto(),
            movimiento.getSaldoAnterior(),
            movimiento.getSaldoPosterior());
    }

    // ==================== METODOS ABSTRACTOS (DEBEN IMPLEMENTAR) ====================

    /**
     * Ejecuta la operacion especifica.
     * ESTE METODO DEBE SER IMPLEMENTADO por cada tipo de operacion.
     *
     * @param cuenta Cuenta sobre la que opera
     * @param monto Monto de la operacion
     * @param descripcion Descripcion
     * @return Movimiento generado
     */
    protected abstract Movimiento ejecutar(Cuenta cuenta, Double monto, String descripcion);

    /**
     * Retorna el nombre de la operacion para logging.
     */
    protected abstract String getNombreOperacion();

    // ==================== HOOKS (PUEDEN SOBRESCRIBIR - OPCIONALES) ====================

    /**
     * Validacion especifica de cada tipo de operacion.
     * Las subclases pueden sobrescribir para agregar validaciones.
     */
    protected void validarOperacionEspecifica(Cuenta cuenta, Double monto) {
        // Hook vacio - las subclases pueden sobrescribir
    }

    /**
     * Preparacion antes de ejecutar.
     * Las subclases pueden sobrescribir para preparar datos.
     */
    protected void prepararOperacion(Cuenta cuenta, Double monto) {
        // Hook vacio - las subclases pueden sobrescribir
    }

    /**
     * Post-proceso despues de ejecutar.
     * Las subclases pueden sobrescribir para acciones adicionales.
     */
    protected void postProcesarOperacion(Cuenta cuenta, Movimiento movimiento) {
        // Hook vacio - las subclases pueden sobrescribir
    }

    // ==================== EXCEPCION PERSONALIZADA ====================

    /**
     * Excepcion para operaciones bancarias invalidas.
     */
    public static class OperacionInvalidaException extends RuntimeException {
        public OperacionInvalidaException(String message) {
            super(message);
        }
    }
}
