package com.trabajo.api.service;

import com.trabajo.api.model.*;
import com.trabajo.api.pattern.behavioral.MovimientoSubject;
import com.trabajo.api.pattern.creational.CuentaFactory;
import com.trabajo.api.pattern.creational.MovimientoBuilder;
import com.trabajo.api.repository.CuentaRepository;
import com.trabajo.api.repository.MovimientoRepository;
import com.trabajo.api.service.interfaces.ServicioBancario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================================
 * SERVICIO PRINCIPAL: Implementacion de ServicioBancario
 * ============================================================================
 *
 * PRINCIPIOS SOLID APLICADOS:
 *
 * (S) Single Responsibility:
 *     Esta clase se encarga SOLO de la logica de negocio bancaria.
 *     La persistencia la delega a los Repositories.
 *     Las notificaciones las delega a los Observers.
 *     La construccion de objetos la delega a Factories/Builders.
 *
 * (O) Open/Closed:
 *     Abierta a extension: Nuevas operaciones se agregan via Strategy pattern.
 *     Cerrada a modificacion: Los algoritmos de interes estan encapsulados.
 *
 * (L) Liskov Substitution:
 *     Implementa ServicioBancario, puede sustituirse por cualquier
 *     implementacion de la misma interfaz.
 *
 * (I) Interface Segregation:
 *     ServicioBancario extiende interfaces segregadas:
 *     - OperacionesLectura: Solo consultas
 *     - OperacionesEscritura: Solo modificaciones
 *
 * (D) Dependency Inversion:
 *     Depende de abstracciones (Repository interfaces, Observer interfaces)
 *     No de implementaciones concretas.
 *
 * PATRONES UTILIZADOS:
 * - Factory: CuentaFactory para crear cuentas
 * - Builder: MovimientoBuilder para construir movimientos
 * - Observer: MovimientoSubject para notificaciones
 * - Singleton: Spring @Service ya lo implementa
 *
 * @author Sistema Bancario
 * @version 2.0
 */
@Service
@Transactional
public class BancoService implements ServicioBancario {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final CuentaFactory cuentaFactory;
    private final MovimientoBuilder movimientoBuilder;
    private final MovimientoSubject movimientoSubject;

    /**
     * Constructor con inyeccion de dependencias.
     * Spring inyecta automaticamente los repositorios y patrones.
     *
     * PRINCIPIO SOLID - Dependency Inversion (D):
     * Todas las dependencias son inyectadas, no creadas internamente.
     */
    @Autowired
    public BancoService(CuentaRepository cuentaRepository,
                        MovimientoRepository movimientoRepository,
                        CuentaFactory cuentaFactory,
                        MovimientoBuilder movimientoBuilder,
                        MovimientoSubject movimientoSubject) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
        this.cuentaFactory = cuentaFactory;
        this.movimientoBuilder = movimientoBuilder;
        this.movimientoSubject = movimientoSubject;
    }

    // ==================== OPERACIONES DE CUENTA ====================

    /**
     * Obtiene todas las cuentas bancarias.
     * Usa: JpaRepository.findAll()
     */
    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> obtenerTodasLasCuentas() {
        return cuentaRepository.findAll();
    }

    /**
     * Obtiene una cuenta por su ID.
     * Usa: JpaRepository.findById()
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Cuenta> obtenerCuentaPorId(Long id) {
        return cuentaRepository.findById(id);
    }

    /**
     * Obtiene una cuenta por su numero.
     * Usa: Derived Query Method personalizado
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Cuenta> obtenerCuentaPorNumero(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta);
    }

    /**
     * Crea una nueva cuenta bancaria.
     * PATRON CREACIONAL: Factory Method
     * Usa CuentaFactory para crear la cuenta con configuracion apropiada.
     */
    @Override
    public Cuenta crearCuenta(Cuenta cuenta) {
        // Usar Factory para crear la cuenta (PATRON FACTORY)
        TipoCuenta tipo = cuenta.getTipoCuenta() != null ?
            cuenta.getTipoCuenta() : TipoCuenta.AHORRO;

        Cuenta nuevaCuenta = cuentaFactory.crearCuenta(
            tipo,
            cuenta.getTitular(),
            cuenta.getDni()
        );

        // Generar numero de cuenta unico si no lo tiene
        if (nuevaCuenta.getNumeroCuenta() == null ||
            nuevaCuenta.getNumeroCuenta().isEmpty()) {
            nuevaCuenta.setNumeroCuenta(generarNumeroCuenta());
        }

        return cuentaRepository.save(nuevaCuenta);
    }

    /**
     * Genera un numero de cuenta unico basado en el ID maximo.
     */
    private String generarNumeroCuenta() {
        Long maxId = cuentaRepository.findMaxId();
        long siguiente = (maxId == null) ? 1 : maxId + 1;
        return String.format("100000%04d", siguiente);
    }

    /**
     * Actualiza los datos de una cuenta existente.
     */
    @Override
    public Optional<Cuenta> actualizarCuenta(Long id, Cuenta cuenta) {
        Optional<Cuenta> existente = cuentaRepository.findById(id);
        if (existente.isPresent()) {
            Cuenta cuentaActual = existente.get();
            cuentaActual.setTitular(cuenta.getTitular());
            cuentaActual.setDni(cuenta.getDni());
            if (cuenta.getTipoCuenta() != null) {
                cuentaActual.setTipoCuenta(cuenta.getTipoCuenta());
            }
            // El @PreUpdate establecera la fecha de actualizacion
            return Optional.of(cuentaRepository.save(cuentaActual));
        }
        return Optional.empty();
    }

    /**
     * Cambia el estado de una cuenta.
     */
    @Override
    public Optional<Cuenta> cambiarEstadoCuenta(String numeroCuenta, String nuevoEstado) {
        Optional<Cuenta> cuentaOpt = cuentaRepository.findByNumeroCuenta(numeroCuenta);
        if (cuentaOpt.isPresent()) {
            Cuenta cuenta = cuentaOpt.get();
            cuenta.setEstado(nuevoEstado);
            return Optional.of(cuentaRepository.save(cuenta));
        }
        return Optional.empty();
    }

    // ==================== OPERACIONES BANCARIAS ====================

    /**
     * Realiza un deposito en una cuenta.
     * PATRON CREACIONAL: Builder - Usa MovimientoBuilder para construir el movimiento.
     * PATRON COMPORTAMIENTO: Observer - Notifica a los observadores del movimiento.
     */
    @Override
    public Optional<Movimiento> depositar(String numeroCuenta, Double monto, String descripcion) {
        if (monto <= 0) {
            return Optional.empty();
        }

        Optional<Cuenta> cuentaOpt = cuentaRepository.findByNumeroCuenta(numeroCuenta);
        if (cuentaOpt.isEmpty() || !cuentaOpt.get().estaActiva()) {
            return Optional.empty();
        }

        Cuenta cuenta = cuentaOpt.get();
        Double saldoAnterior = cuenta.getSaldo();
        Double saldoPosterior = saldoAnterior + monto;

        // Actualizar saldo de la cuenta
        cuenta.setSaldo(saldoPosterior);
        cuentaRepository.save(cuenta);

        // PATRON BUILDER: Construir movimiento usando Builder
        Movimiento movimiento = movimientoBuilder.nuevo()
            .conCuenta(cuenta)
            .conTipo(TipoMovimiento.DEPOSITO)
            .conMonto(monto)
            .conDescripcion(descripcion != null ? descripcion : "Deposito en cuenta")
            .build();

        // Ajustar saldos (el builder calcula automaticamente pero confirmamos)
        movimiento.setSaldoAnterior(saldoAnterior);
        movimiento.setSaldoPosterior(saldoPosterior);

        Movimiento guardado = movimientoRepository.save(movimiento);

        // PATRON OBSERVER: Notificar a todos los observadores
        movimientoSubject.notificarMovimiento(guardado);

        return Optional.of(guardado);
    }

    /**
     * Realiza un retiro de una cuenta.
     * PATRON CREACIONAL: Builder - Usa MovimientoBuilder para construir el movimiento.
     * PATRON COMPORTAMIENTO: Observer - Notifica a los observadores del movimiento.
     */
    @Override
    public Optional<Movimiento> retirar(String numeroCuenta, Double monto, String descripcion) {
        if (monto <= 0) {
            return Optional.empty();
        }

        Optional<Cuenta> cuentaOpt = cuentaRepository.findByNumeroCuenta(numeroCuenta);
        if (cuentaOpt.isEmpty() || !cuentaOpt.get().estaActiva()) {
            return Optional.empty();
        }

        Cuenta cuenta = cuentaOpt.get();
        if (!cuenta.tieneSaldoSuficiente(monto)) {
            return Optional.empty(); // Saldo insuficiente
        }

        Double saldoAnterior = cuenta.getSaldo();
        Double saldoPosterior = saldoAnterior - monto;

        // Actualizar saldo
        cuenta.setSaldo(saldoPosterior);
        cuentaRepository.save(cuenta);

        // PATRON BUILDER: Construir movimiento usando Builder
        Movimiento movimiento = movimientoBuilder.nuevo()
            .conCuenta(cuenta)
            .conTipo(TipoMovimiento.RETIRO)
            .conMonto(monto)
            .conDescripcion(descripcion != null ? descripcion : "Retiro de cuenta")
            .build();

        movimiento.setSaldoAnterior(saldoAnterior);
        movimiento.setSaldoPosterior(saldoPosterior);

        Movimiento guardado = movimientoRepository.save(movimiento);

        // PATRON OBSERVER: Notificar a todos los observadores
        movimientoSubject.notificarMovimiento(guardado);

        return Optional.of(guardado);
    }

    /**
     * Realiza una transferencia entre cuentas.
     * PATRON COMPORTAMIENTO: Observer - Notifica ambos movimientos.
     * Operacion atomica gracias a @Transactional.
     */
    @Override
    public Optional<Movimiento> transferir(TransferenciaRequest request) {
        if (request.getMonto() <= 0) {
            return Optional.empty();
        }

        if (request.getCuentaOrigen().equals(request.getCuentaDestino())) {
            return Optional.empty();
        }

        Optional<Cuenta> origenOpt = cuentaRepository.findByNumeroCuenta(request.getCuentaOrigen());
        Optional<Cuenta> destinoOpt = cuentaRepository.findByNumeroCuenta(request.getCuentaDestino());

        if (origenOpt.isEmpty() || destinoOpt.isEmpty()) {
            return Optional.empty();
        }

        Cuenta origen = origenOpt.get();
        Cuenta destino = destinoOpt.get();

        if (!origen.estaActiva() || !destino.estaActiva()) {
            return Optional.empty();
        }

        if (!origen.tieneSaldoSuficiente(request.getMonto())) {
            return Optional.empty();
        }

        // Actualizar cuenta origen
        Double saldoAnteriorOrigen = origen.getSaldo();
        origen.setSaldo(saldoAnteriorOrigen - request.getMonto());
        cuentaRepository.save(origen);

        // Actualizar cuenta destino
        Double saldoAnteriorDestino = destino.getSaldo();
        destino.setSaldo(saldoAnteriorDestino + request.getMonto());
        cuentaRepository.save(destino);

        // Movimiento en cuenta origen (TRANSFERENCIA_ENVIADA)
        Movimiento movOrigen = new Movimiento();
        movOrigen.setCuenta(origen);
        movOrigen.setNumeroCuenta(request.getCuentaOrigen());
        movOrigen.setTipo(TipoMovimiento.TRANSFERENCIA_ENVIADA);
        movOrigen.setMonto(request.getMonto());
        movOrigen.setSaldoAnterior(saldoAnteriorOrigen);
        movOrigen.setSaldoPosterior(origen.getSaldo());
        movOrigen.setCuentaDestino(request.getCuentaDestino());
        movOrigen.setDescripcion(request.getDescripcion() != null ?
                request.getDescripcion() : "Transferencia a " + request.getCuentaDestino());
        movimientoRepository.save(movOrigen);

        // Movimiento en cuenta destino (TRANSFERENCIA_RECIBIDA)
        Movimiento movDestino = new Movimiento();
        movDestino.setCuenta(destino);
        movDestino.setNumeroCuenta(request.getCuentaDestino());
        movDestino.setTipo(TipoMovimiento.TRANSFERENCIA_RECIBIDA);
        movDestino.setMonto(request.getMonto());
        movDestino.setSaldoAnterior(saldoAnteriorDestino);
        movDestino.setSaldoPosterior(destino.getSaldo());
        movDestino.setCuentaOrigen(request.getCuentaOrigen());
        movDestino.setDescripcion("Transferencia de " + request.getCuentaOrigen());
        movimientoRepository.save(movDestino);

        // PATRON OBSERVER: Notificar ambos movimientos
        movimientoSubject.notificarMovimiento(movOrigen);
        movimientoSubject.notificarMovimiento(movDestino);

        return Optional.of(movOrigen);
    }

    /**
     * Consulta el saldo de una cuenta.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Double> consultarSaldo(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .map(Cuenta::getSaldo);
    }

    // ==================== OPERACIONES DE MOVIMIENTOS ====================

    /**
     * Obtiene el historial de movimientos de una cuenta.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> obtenerMovimientos(String numeroCuenta) {
        return movimientoRepository.findByNumeroCuentaOrderByFechaDesc(numeroCuenta);
    }

    /**
     * Obtiene los ultimos N movimientos.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Movimiento> obtenerUltimosMovimientos(String numeroCuenta, int cantidad) {
        return movimientoRepository.findTop10ByNumeroCuentaOrderByFechaDesc(numeroCuenta);
    }

    // ==================== CONSULTAS ADICIONALES ====================

    /**
     * Obtiene cuentas por estado.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> obtenerCuentasPorEstado(EstadoCuenta estado) {
        return cuentaRepository.findByEstado(estado);
    }

    /**
     * Calcula el total de depositos de una cuenta.
     */
    @Override
    @Transactional(readOnly = true)
    public Double obtenerTotalDepositos(String numeroCuenta) {
        return movimientoRepository.calcularTotalDepositos(numeroCuenta);
    }

    /**
     * Calcula el total de retiros de una cuenta.
     */
    @Override
    @Transactional(readOnly = true)
    public Double obtenerTotalRetiros(String numeroCuenta) {
        return movimientoRepository.calcularTotalRetiros(numeroCuenta);
    }
}
