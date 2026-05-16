package com.trabajo.api.controller;

import com.trabajo.api.model.Movimiento;
import com.trabajo.api.model.TipoMovimiento;
import com.trabajo.api.model.Cuenta;
import com.trabajo.api.model.TipoCuenta;
import com.trabajo.api.repository.MovimientoRepository;
import com.trabajo.api.repository.CuentaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    public EstadisticasController(MovimientoRepository movimientoRepository, CuentaRepository cuentaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
    }

    @GetMapping("/movimientos-por-mes")
    public ResponseEntity<Map<String, Object>> getMovimientosPorMes() {
        List<Movimiento> movimientos = movimientoRepository.findAll();

        Map<String, Double> depositosPorMes = new LinkedHashMap<>();
        Map<String, Double> retirosPorMes = new LinkedHashMap<>();
        Map<String, Double> transferenciasPorMes = new LinkedHashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es", "PE"));

        LocalDateTime ahora = LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            LocalDateTime mes = ahora.minusMonths(i);
            String mesKey = mes.format(formatter);
            depositosPorMes.put(mesKey, 0.0);
            retirosPorMes.put(mesKey, 0.0);
            transferenciasPorMes.put(mesKey, 0.0);
        }

        for (Movimiento mov : movimientos) {
            String mesKey = mov.getFecha().format(formatter);
            if (depositosPorMes.containsKey(mesKey)) {
                switch (mov.getTipo()) {
                    case DEPOSITO:
                        depositosPorMes.merge(mesKey, mov.getMonto(), Double::sum);
                        break;
                    case RETIRO:
                        retirosPorMes.merge(mesKey, mov.getMonto(), Double::sum);
                        break;
                    case TRANSFERENCIA_ENVIADA:
                        transferenciasPorMes.merge(mesKey, mov.getMonto(), Double::sum);
                        break;
                    default:
                        break;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", new ArrayList<>(depositosPorMes.keySet()));
        result.put("depositos", new ArrayList<>(depositosPorMes.values()));
        result.put("retiros", new ArrayList<>(retirosPorMes.values()));
        result.put("transferencias", new ArrayList<>(transferenciasPorMes.values()));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/movimientos-ultimos-dias")
    public ResponseEntity<Map<String, Object>> getMovimientosUltimosDias() {
        List<Movimiento> movimientos = movimientoRepository.findAll();

        Map<String, Integer> movimientosPorDia = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        LocalDateTime ahora = LocalDateTime.now();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime dia = ahora.minusDays(i);
            String diaKey = dia.format(formatter);
            movimientosPorDia.put(diaKey, 0);
        }

        for (Movimiento mov : movimientos) {
            String diaKey = mov.getFecha().format(formatter);
            if (movimientosPorDia.containsKey(diaKey)) {
                movimientosPorDia.merge(diaKey, 1, Integer::sum);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", new ArrayList<>(movimientosPorDia.keySet()));
        result.put("data", new ArrayList<>(movimientosPorDia.values()));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/tipos-cuenta")
    public ResponseEntity<Map<String, Object>> getTiposCuenta() {
        List<Cuenta> cuentas = cuentaRepository.findAll();

        long ahorro = cuentas.stream().filter(c -> c.getTipoCuenta() == TipoCuenta.AHORRO).count();
        long corriente = cuentas.stream().filter(c -> c.getTipoCuenta() == TipoCuenta.CORRIENTE).count();

        Map<String, Object> result = new HashMap<>();
        result.put("labels", Arrays.asList("Ahorro", "Corriente"));
        result.put("data", Arrays.asList(ahorro, corriente));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/tipos-movimiento")
    public ResponseEntity<Map<String, Object>> getTiposMovimiento() {
        List<Movimiento> movimientos = movimientoRepository.findAll();

        Map<String, Long> conteo = movimientos.stream()
            .collect(Collectors.groupingBy(m -> {
                switch (m.getTipo()) {
                    case DEPOSITO: return "Depositos";
                    case RETIRO: return "Retiros";
                    case TRANSFERENCIA_ENVIADA: return "Transferencias Enviadas";
                    case TRANSFERENCIA_RECIBIDA: return "Transferencias Recibidas";
                    default: return "Otros";
                }
            }, Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        result.put("labels", new ArrayList<>(conteo.keySet()));
        result.put("data", new ArrayList<>(conteo.values()));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> getResumen() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        List<Movimiento> movimientos = movimientoRepository.findAll();

        double totalDepositos = movimientos.stream()
            .filter(m -> m.getTipo() == TipoMovimiento.DEPOSITO)
            .mapToDouble(Movimiento::getMonto)
            .sum();

        double totalRetiros = movimientos.stream()
            .filter(m -> m.getTipo() == TipoMovimiento.RETIRO)
            .mapToDouble(Movimiento::getMonto)
            .sum();

        double totalTransferencias = movimientos.stream()
            .filter(m -> m.getTipo() == TipoMovimiento.TRANSFERENCIA_ENVIADA)
            .mapToDouble(Movimiento::getMonto)
            .sum();

        double saldoTotal = cuentas.stream().mapToDouble(Cuenta::getSaldo).sum();

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioMes = ahora.withDayOfMonth(1).withHour(0).withMinute(0);

        long transaccionesEsteMes = movimientos.stream()
            .filter(m -> m.getFecha().isAfter(inicioMes))
            .count();

        Map<String, Object> result = new HashMap<>();
        result.put("totalDepositos", totalDepositos);
        result.put("totalRetiros", totalRetiros);
        result.put("totalTransferencias", totalTransferencias);
        result.put("saldoTotal", saldoTotal);
        result.put("totalCuentas", cuentas.size());
        result.put("totalMovimientos", movimientos.size());
        result.put("transaccionesEsteMes", transaccionesEsteMes);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/ultimos-movimientos")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> getUltimosMovimientos() {
        List<Movimiento> movimientos = movimientoRepository.findAll();

        List<Map<String, Object>> ultimos = movimientos.stream()
            .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
            .limit(10)
            .map(m -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", m.getId());
                map.put("tipo", m.getTipo().toString());
                map.put("monto", m.getMonto());
                map.put("fecha", m.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                map.put("cuenta", m.getCuenta().getNumeroCuenta());
                map.put("descripcion", m.getDescripcion());
                return map;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(ultimos);
    }
}
