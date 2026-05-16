package com.trabajo.api.controller;

import com.trabajo.api.model.Cuenta;
import com.trabajo.api.model.EstadoCuenta;
import com.trabajo.api.repository.MovimientoRepository;
import com.trabajo.api.service.BancoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * CONTROLLER: Controlador de Vistas (Thymeleaf).
 * Sirve las paginas HTML del frontend.
 */
@Controller
public class VistaController {

    private final BancoService bancoService;
    private final MovimientoRepository movimientoRepository;

    public VistaController(BancoService bancoService, MovimientoRepository movimientoRepository) {
        this.bancoService = bancoService;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Pagina principal - Dashboard con estadisticas
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Cuenta> cuentas = bancoService.obtenerTodasLasCuentas();

        // Estadisticas para el dashboard
        int totalCuentas = cuentas.size();
        long cuentasActivas = cuentas.stream()
                .filter(c -> c.getEstado() == EstadoCuenta.ACTIVA)
                .count();
        double saldoTotal = cuentas.stream()
                .mapToDouble(Cuenta::getSaldo)
                .sum();
        long totalMovimientos = movimientoRepository.count();

        model.addAttribute("cuentas", cuentas);
        model.addAttribute("totalCuentas", totalCuentas);
        model.addAttribute("cuentasActivas", cuentasActivas);
        model.addAttribute("saldoTotal", saldoTotal);
        model.addAttribute("totalMovimientos", totalMovimientos);

        return "index";
    }

    /**
     * Pagina de gestion de cuentas
     */
    @GetMapping("/cuentas")
    public String cuentas(Model model) {
        model.addAttribute("cuentas", bancoService.obtenerTodasLasCuentas());
        return "cuentas";
    }

    /**
     * Pagina de operaciones bancarias
     */
    @GetMapping("/operaciones")
    public String operaciones(Model model) {
        model.addAttribute("cuentas", bancoService.obtenerTodasLasCuentas());
        return "operaciones";
    }

    /**
     * Pagina de movimientos/historial
     */
    @GetMapping("/movimientos")
    public String movimientos(Model model) {
        model.addAttribute("cuentas", bancoService.obtenerTodasLasCuentas());
        return "movimientos";
    }

    /**
     * Pagina de intereses y plazos fijos
     */
    @GetMapping("/intereses")
    public String intereses(Model model) {
        model.addAttribute("cuentas", bancoService.obtenerTodasLasCuentas());
        return "intereses";
    }

    /**
     * Pagina de demos interactivas de patrones de diseño
     * Demuestra en tiempo real: Strategy, Circuit Breaker, Observer, Health Check
     */
    @GetMapping("/demos")
    public String demos(Model model) {
        return "demos";
    }
}
