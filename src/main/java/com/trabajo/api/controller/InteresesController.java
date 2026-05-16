package com.trabajo.api.controller;

import com.trabajo.api.model.*;
import com.trabajo.api.repository.CuentaRepository;
import com.trabajo.api.repository.PlazoFijoRepository;
import com.trabajo.api.repository.MovimientoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/intereses")
public class InteresesController {

    private final PlazoFijoRepository plazoFijoRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    private static final Map<Integer, Double> TASAS_INTERES = Map.of(
        3, 4.5,    // 3 meses: 4.5% anual
        6, 5.5,    // 6 meses: 5.5% anual
        12, 7.0,   // 12 meses: 7.0% anual
        18, 8.0,   // 18 meses: 8.0% anual
        24, 9.0    // 24 meses: 9.0% anual
    );

    public InteresesController(PlazoFijoRepository plazoFijoRepository,
                               CuentaRepository cuentaRepository,
                               MovimientoRepository movimientoRepository) {
        this.plazoFijoRepository = plazoFijoRepository;
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @GetMapping("/tasas")
    public ResponseEntity<List<Map<String, Object>>> getTasas() {
        List<Map<String, Object>> tasas = new ArrayList<>();
        TASAS_INTERES.forEach((plazo, tasa) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("plazoMeses", plazo);
            item.put("tasaAnual", tasa);
            item.put("tasaMensual", tasa / 12);
            item.put("descripcion", plazo + " meses - " + tasa + "% TEA");
            tasas.add(item);
        });
        tasas.sort(Comparator.comparingInt(a -> (Integer) a.get("plazoMeses")));
        return ResponseEntity.ok(tasas);
    }

    @PostMapping("/simular")
    public ResponseEntity<Map<String, Object>> simularInteres(@RequestBody Map<String, Object> request) {
        Double monto = ((Number) request.get("monto")).doubleValue();
        Integer plazoMeses = ((Number) request.get("plazoMeses")).intValue();

        Double tasaAnual = TASAS_INTERES.getOrDefault(plazoMeses, 5.0);
        Double tasaMensual = tasaAnual / 100 / 12;

        List<Map<String, Object>> proyeccion = new ArrayList<>();
        double saldoActual = monto;

        for (int mes = 1; mes <= plazoMeses; mes++) {
            double interesMes = saldoActual * tasaMensual;
            saldoActual += interesMes;

            Map<String, Object> mesData = new HashMap<>();
            mesData.put("mes", mes);
            mesData.put("interesMes", Math.round(interesMes * 100.0) / 100.0);
            mesData.put("saldoAcumulado", Math.round(saldoActual * 100.0) / 100.0);
            proyeccion.add(mesData);
        }

        double interesTotal = saldoActual - monto;
        double gananciaAnualizada = (interesTotal / monto) * (12.0 / plazoMeses) * 100;

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("montoInicial", monto);
        resultado.put("plazoMeses", plazoMeses);
        resultado.put("tasaAnual", tasaAnual);
        resultado.put("interesTotal", Math.round(interesTotal * 100.0) / 100.0);
        resultado.put("montoFinal", Math.round(saldoActual * 100.0) / 100.0);
        resultado.put("gananciaAnualizada", Math.round(gananciaAnualizada * 100.0) / 100.0);
        resultado.put("proyeccionMensual", proyeccion);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/crear-plazo-fijo")
    @Transactional
    public ResponseEntity<?> crearPlazoFijo(@RequestBody PlazoFijoRequest request) {
        Optional<Cuenta> cuentaOpt = cuentaRepository.findByNumeroCuenta(request.getNumeroCuenta());
        if (cuentaOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cuenta no encontrada"));
        }

        Cuenta cuenta = cuentaOpt.get();
        if (cuenta.getSaldo() < request.getMonto()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Saldo insuficiente"));
        }

        if (request.getMonto() < 1000) {
            return ResponseEntity.badRequest().body(Map.of("error", "Monto minimo: S/. 1,000"));
        }

        Double tasaAnual = TASAS_INTERES.getOrDefault(request.getPlazoMeses(), 5.0);

        PlazoFijo plazoFijo = new PlazoFijo();
        plazoFijo.setCuenta(cuenta);
        plazoFijo.setMontoInicial(request.getMonto());
        plazoFijo.setTasaInteres(tasaAnual);
        plazoFijo.setPlazoMeses(request.getPlazoMeses());
        plazoFijo.setFechaInicio(LocalDateTime.now());

        cuenta.setSaldo(cuenta.getSaldo() - request.getMonto());
        cuentaRepository.save(cuenta);

        Movimiento movimiento = new Movimiento();
        movimiento.setCuenta(cuenta);
        movimiento.setTipo(TipoMovimiento.RETIRO);
        movimiento.setMonto(request.getMonto());
        movimiento.setSaldoAnterior(cuenta.getSaldo() + request.getMonto());
        movimiento.setSaldoPosterior(cuenta.getSaldo());
        movimiento.setDescripcion("Apertura Plazo Fijo - " + request.getPlazoMeses() + " meses");
        movimientoRepository.save(movimiento);

        plazoFijoRepository.save(plazoFijo);

        Map<String, Object> response = new HashMap<>();
        response.put("id", plazoFijo.getId());
        response.put("mensaje", "Plazo fijo creado exitosamente");
        response.put("montoInicial", plazoFijo.getMontoInicial());
        response.put("tasaInteres", plazoFijo.getTasaInteres());
        response.put("interesGenerado", plazoFijo.getInteresGenerado());
        response.put("montoFinal", plazoFijo.getMontoFinal());
        response.put("fechaVencimiento", plazoFijo.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/plazos-fijos")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Map<String, Object>>> listarPlazosFijos() {
        List<PlazoFijo> plazos = plazoFijoRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<Map<String, Object>> resultado = plazos.stream().map(p -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("numeroCuenta", p.getCuenta().getNumeroCuenta());
            item.put("titular", p.getCuenta().getTitular());
            item.put("montoInicial", p.getMontoInicial());
            item.put("tasaInteres", p.getTasaInteres());
            item.put("plazoMeses", p.getPlazoMeses());
            item.put("interesGenerado", p.getInteresGenerado());
            item.put("montoFinal", p.getMontoFinal());
            item.put("fechaInicio", p.getFechaInicio().format(formatter));
            item.put("fechaVencimiento", p.getFechaVencimiento().format(formatter));
            item.put("estado", p.getEstado().toString());
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticas() {
        List<PlazoFijo> plazosActivos = plazoFijoRepository.findByEstado(EstadoPlazoFijo.ACTIVO);

        double montoTotal = plazosActivos.stream().mapToDouble(PlazoFijo::getMontoInicial).sum();
        double interesTotal = plazosActivos.stream().mapToDouble(PlazoFijo::getInteresGenerado).sum();
        double montoFinalTotal = plazosActivos.stream().mapToDouble(PlazoFijo::getMontoFinal).sum();

        Map<Integer, Long> distribucionPorPlazo = plazosActivos.stream()
            .collect(Collectors.groupingBy(PlazoFijo::getPlazoMeses, Collectors.counting()));

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("totalPlazosActivos", plazosActivos.size());
        resultado.put("montoTotalInvertido", montoTotal);
        resultado.put("interesTotalGenerado", interesTotal);
        resultado.put("montoFinalEsperado", montoFinalTotal);
        resultado.put("distribucionPorPlazo", distribucionPorPlazo);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/grafico-proyeccion")
    public ResponseEntity<Map<String, Object>> getGraficoProyeccion() {
        List<PlazoFijo> plazosActivos = plazoFijoRepository.findByEstado(EstadoPlazoFijo.ACTIVO);

        List<String> labels = new ArrayList<>();
        List<Double> montoInicial = new ArrayList<>();
        List<Double> intereses = new ArrayList<>();
        List<Double> montoFinal = new ArrayList<>();

        for (int i = 0; i < Math.min(plazosActivos.size(), 10); i++) {
            PlazoFijo p = plazosActivos.get(i);
            labels.add("PF-" + p.getId());
            montoInicial.add(p.getMontoInicial());
            intereses.add(p.getInteresGenerado());
            montoFinal.add(p.getMontoFinal());
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("labels", labels);
        resultado.put("montoInicial", montoInicial);
        resultado.put("intereses", intereses);
        resultado.put("montoFinal", montoFinal);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/cobrar/{id}")
    @Transactional
    public ResponseEntity<?> cobrarPlazoFijo(@PathVariable Long id) {
        Optional<PlazoFijo> plazoOpt = plazoFijoRepository.findById(id);
        if (plazoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Plazo fijo no encontrado"));
        }

        PlazoFijo plazo = plazoOpt.get();
        if (plazo.getEstado() != EstadoPlazoFijo.ACTIVO) {
            return ResponseEntity.badRequest().body(Map.of("error", "El plazo fijo no esta activo"));
        }

        Cuenta cuenta = plazo.getCuenta();
        double montoACobrar = plazo.getMontoFinal();

        if (LocalDateTime.now().isBefore(plazo.getFechaVencimiento())) {
            montoACobrar = plazo.getMontoInicial() * 0.99;
            plazo.setEstado(EstadoPlazoFijo.CANCELADO);
        } else {
            plazo.setEstado(EstadoPlazoFijo.COBRADO);
        }

        plazo.setFechaCancelacion(LocalDateTime.now());
        cuenta.setSaldo(cuenta.getSaldo() + montoACobrar);

        Movimiento movimiento = new Movimiento();
        movimiento.setCuenta(cuenta);
        movimiento.setTipo(TipoMovimiento.DEPOSITO);
        movimiento.setMonto(montoACobrar);
        movimiento.setSaldoAnterior(cuenta.getSaldo() - montoACobrar);
        movimiento.setSaldoPosterior(cuenta.getSaldo());
        movimiento.setDescripcion("Cobro Plazo Fijo #" + plazo.getId());

        cuentaRepository.save(cuenta);
        movimientoRepository.save(movimiento);
        plazoFijoRepository.save(plazo);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Plazo fijo cobrado exitosamente");
        response.put("montoCobrado", montoACobrar);
        response.put("estado", plazo.getEstado().toString());

        return ResponseEntity.ok(response);
    }
}
