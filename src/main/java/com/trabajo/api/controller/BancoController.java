package com.trabajo.api.controller;

import com.trabajo.api.model.Cuenta;
import com.trabajo.api.model.Movimiento;
import com.trabajo.api.model.TransferenciaRequest;
import com.trabajo.api.service.BancoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLLER: API REST del Sistema Bancario.
 *
 * ENDPOINTS DISPONIBLES:
 *
 * CUENTAS:
 * GET    /api/cuentas                    - Listar todas las cuentas
 * GET    /api/cuentas/{id}               - Obtener cuenta por ID
 * GET    /api/cuentas/numero/{numero}    - Obtener cuenta por número
 * POST   /api/cuentas                    - Crear nueva cuenta
 * PUT    /api/cuentas/{id}               - Actualizar cuenta
 * PATCH  /api/cuentas/{numero}/estado    - Cambiar estado de cuenta
 *
 * OPERACIONES BANCARIAS:
 * GET    /api/cuentas/{numero}/saldo     - Consultar saldo
 * POST   /api/operaciones/deposito       - Depositar dinero
 * POST   /api/operaciones/retiro         - Retirar dinero
 * POST   /api/operaciones/transferencia  - Transferir entre cuentas
 *
 * MOVIMIENTOS:
 * GET    /api/movimientos/{numero}       - Historial de movimientos
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BancoController {

    private final BancoService bancoService;

    public BancoController(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    // ==================== ENDPOINTS DE CUENTAS ====================

    /**
     * GET /api/cuentas
     * Obtiene todas las cuentas bancarias
     */
    @GetMapping("/cuentas")
    public ResponseEntity<List<Cuenta>> obtenerTodasLasCuentas() {
        return ResponseEntity.ok(bancoService.obtenerTodasLasCuentas());
    }

    /**
     * GET /api/cuentas/{id}
     * Obtiene una cuenta por su ID
     */
    @GetMapping("/cuentas/{id}")
    public ResponseEntity<Cuenta> obtenerCuentaPorId(@PathVariable Long id) {
        return bancoService.obtenerCuentaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/cuentas/numero/{numero}
     * Obtiene una cuenta por su número
     */
    @GetMapping("/cuentas/numero/{numero}")
    public ResponseEntity<Cuenta> obtenerCuentaPorNumero(@PathVariable String numero) {
        return bancoService.obtenerCuentaPorNumero(numero)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/cuentas
     * Crea una nueva cuenta bancaria
     */
    @PostMapping("/cuentas")
    public ResponseEntity<Cuenta> crearCuenta(@RequestBody Cuenta cuenta) {
        Cuenta nuevaCuenta = bancoService.crearCuenta(cuenta);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCuenta);
    }

    /**
     * PUT /api/cuentas/{id}
     * Actualiza una cuenta existente
     */
    @PutMapping("/cuentas/{id}")
    public ResponseEntity<Cuenta> actualizarCuenta(@PathVariable Long id, @RequestBody Cuenta cuenta) {
        return bancoService.actualizarCuenta(id, cuenta)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PATCH /api/cuentas/{numero}/estado
     * Cambia el estado de una cuenta (ACTIVA, BLOQUEADA, CERRADA)
     */
    @PatchMapping("/cuentas/{numero}/estado")
    public ResponseEntity<Cuenta> cambiarEstado(@PathVariable String numero, @RequestParam String estado) {
        return bancoService.cambiarEstadoCuenta(numero, estado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== ENDPOINTS DE OPERACIONES ====================

    /**
     * GET /api/cuentas/{numero}/saldo
     * Consulta el saldo de una cuenta
     */
    @GetMapping("/cuentas/{numero}/saldo")
    public ResponseEntity<Map<String, Object>> consultarSaldo(@PathVariable String numero) {
        return bancoService.consultarSaldo(numero)
                .map(saldo -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("numeroCuenta", numero);
                    response.put("saldo", saldo);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/operaciones/deposito
     * Realiza un depósito en una cuenta
     * Body: { "numeroCuenta": "xxx", "monto": 100.0, "descripcion": "..." }
     */
    @PostMapping("/operaciones/deposito")
    public ResponseEntity<Map<String, Object>> depositar(@RequestBody Map<String, Object> request) {
        String numeroCuenta = (String) request.get("numeroCuenta");
        Double monto = Double.valueOf(request.get("monto").toString());
        String descripcion = (String) request.get("descripcion");

        return bancoService.depositar(numeroCuenta, monto, descripcion)
                .map(mov -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("mensaje", "Deposito realizado exitosamente");
                    response.put("movimiento", mov);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "No se pudo realizar el deposito. Verifique la cuenta y el monto.");
                    return ResponseEntity.badRequest().body(error);
                });
    }

    /**
     * POST /api/operaciones/retiro
     * Realiza un retiro de una cuenta
     * Body: { "numeroCuenta": "xxx", "monto": 100.0, "descripcion": "..." }
     */
    @PostMapping("/operaciones/retiro")
    public ResponseEntity<Map<String, Object>> retirar(@RequestBody Map<String, Object> request) {
        String numeroCuenta = (String) request.get("numeroCuenta");
        Double monto = Double.valueOf(request.get("monto").toString());
        String descripcion = (String) request.get("descripcion");

        return bancoService.retirar(numeroCuenta, monto, descripcion)
                .map(mov -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("mensaje", "Retiro realizado exitosamente");
                    response.put("movimiento", mov);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "No se pudo realizar el retiro. Verifique el saldo disponible.");
                    return ResponseEntity.badRequest().body(error);
                });
    }

    /**
     * POST /api/operaciones/transferencia
     * Realiza una transferencia entre cuentas
     * Body: { "cuentaOrigen": "xxx", "cuentaDestino": "yyy", "monto": 100.0, "descripcion": "..." }
     */
    @PostMapping("/operaciones/transferencia")
    public ResponseEntity<Map<String, Object>> transferir(@RequestBody TransferenciaRequest request) {
        return bancoService.transferir(request)
                .map(mov -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("mensaje", "Transferencia realizada exitosamente");
                    response.put("movimiento", mov);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "No se pudo realizar la transferencia. Verifique las cuentas y el saldo.");
                    return ResponseEntity.badRequest().body(error);
                });
    }

    // ==================== ENDPOINTS DE MOVIMIENTOS ====================

    /**
     * GET /api/movimientos/{numero}
     * Obtiene el historial de movimientos de una cuenta
     */
    @GetMapping("/movimientos/{numero}")
    public ResponseEntity<List<Movimiento>> obtenerMovimientos(@PathVariable String numero) {
        List<Movimiento> movimientos = bancoService.obtenerMovimientos(numero);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * GET /api/movimientos/{numero}/ultimos
     * Obtiene los últimos N movimientos de una cuenta
     */
    @GetMapping("/movimientos/{numero}/ultimos")
    public ResponseEntity<List<Movimiento>> obtenerUltimosMovimientos(
            @PathVariable String numero,
            @RequestParam(defaultValue = "10") int cantidad) {
        List<Movimiento> movimientos = bancoService.obtenerUltimosMovimientos(numero, cantidad);
        return ResponseEntity.ok(movimientos);
    }
}
