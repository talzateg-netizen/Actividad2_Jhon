package com.example.demo_service.controller;

import com.example.demo_service.model.Cliente;
import com.example.demo_service.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@Tag(name = "Movimientos (Lógica de Negocio)", description = "Endpoints para ejecutar transacciones bancarias aplicando las reglas del servicio")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los movimientos", description = "Retorna el historial completo de movimientos bancarios.")
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(movimientoService.obtenerTodos());
    }

    @GetMapping("/cuenta/{cuentaId}")
    @Operation(summary = "Listar movimientos por cuenta", description = "Retorna los movimientos realizados sobre una cuenta específica.")
    public ResponseEntity<List<Cliente>> listarPorCuenta(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(movimientoService.obtenerPorCuenta(cuentaId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener movimiento por ID", description = "Consulta el detalle y saldo resultante de una transacción específica.")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Registrar movimiento bancario (Aplica Reglas de Negocio en Servicio)",
            description = """
                    ### Pruebas de Reglas de Negocio en este Endpoint:
                    Este endpoint ejecuta las validaciones y lógica financiera en `MovimientoService`:

                    #### CASO 1: REGLA CUMPLIDA - DEPÓSITO EXITOSO (HTTP 201)
                    - **Entrada**: Selecciona el ejemplo **'1. Éxito: Depósito Válido'**.
                      - Cuenta ID 1 (Activa, saldo inicial $500.0)
                      - Tipo: `DEPOSITO`, Monto: `200.0`
                    - **Resultado**:
                      - HTTP 201 Created.
                      - El saldo de la cuenta aumenta a `$700.0`.
                      - `saldoResultante` guardado en el movimiento: `700.0`.

                    ---
                    #### CASO 2: REGLA CUMPLIDA - RETIRO EXITOSO (HTTP 201)
                    - **Entrada**: Selecciona el ejemplo **'2. Éxito: Retiro Válido'**.
                      - Cuenta ID 1 (Activa, saldo disponible suficiente)
                      - Tipo: `RETIRO`, Monto: `150.0` (<= $1000.0)
                    - **Resultado**:
                      - HTTP 201 Created.
                      - El saldo se descuenta adecuadamente.
                      - `saldoResultante` refleja el saldo posterior.

                    ---
                    #### CASO 3: REGLA INCUMPLIDA - SALDO INSUFICIENTE (HTTP 400)
                    - **Entrada**: Selecciona el ejemplo **'3. Falla: Saldo Insuficiente'**.
                      - Cuenta ID 1 (Saldo actual menor al solicitado)
                      - Tipo: `RETIRO`, Monto: `950.0`
                    - **Resultado**:
                      - HTTP 400 Bad Request.
                      - Mensaje: `"Regla de negocio INCUMPLIDA [Saldo Insuficiente]: La cuenta CTA-1001 cuenta con un saldo de $X, insuficiente para retirar $950.0."`

                    ---
                    #### CASO 4: REGLA INCUMPLIDA - LÍMITE DE RETIRO EXCEDIDO (HTTP 400)
                    - **Entrada**: Selecciona el ejemplo **'4. Falla: Límite Excedido (> $1000)'**.
                      - Tipo: `RETIRO`, Monto: `2500.0`
                    - **Resultado**:
                      - HTTP 400 Bad Request.
                      - Mensaje: `"Regla de negocio INCUMPLIDA [Límite Excedido]: El retiro solicitado de $2500.0 supera el límite máximo permitido de $1000.0 por operación."`

                    ---
                    #### CASO 5: REGLA INCUMPLIDA - CUENTA INACTIVA O BLOQUEADA (HTTP 400)
                    - **Entrada**: Selecciona el ejemplo **'5. Falla: Cuenta Inactiva'**.
                      - Cuenta ID 2 (Inactiva: activa = false)
                      - Tipo: `DEPOSITO` o `RETIRO`, Monto: `100.0`
                    - **Resultado**:
                      - HTTP 400 Bad Request.
                      - Mensaje: `"Regla de negocio INCUMPLIDA [Cuenta Inactiva]: La cuenta CTA-1002 se encuentra bloqueada o inactiva y no permite transacciones."`
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Movimiento financiero procesado y saldo actualizado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Cliente.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Regla de negocio incumplida (Saldo insuficiente, límite excedido, cuenta inactiva o datos inválidos)."
            )
    })
    public ResponseEntity<Cliente> registrarMovimiento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del movimiento usando directamente la entidad Movimiento",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Cliente.class),
                            examples = {
                                    @ExampleObject(
                                            name = "1. Éxito: Depósito Válido",
                                            summary = "Caso Exitoso: Depositar $200.0 en cuenta activa",
                                            value = """
                                                    {
                                                      "cuenta": { "id": 1 },
                                                      "tipo": "DEPOSITO",
                                                      "monto": 200.0
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "2. Éxito: Retiro Válido",
                                            summary = "Caso Exitoso: Retirar $150.0 con saldo suficiente",
                                            value = """
                                                    {
                                                      "cuenta": { "id": 1 },
                                                      "tipo": "RETIRO",
                                                      "monto": 150.0
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "3. Falla: Saldo Insuficiente",
                                            summary = "Caso Fallo: Intentar retirar más dinero del saldo disponible",
                                            value = """
                                                    {
                                                      "cuenta": { "id": 1 },
                                                      "tipo": "RETIRO",
                                                      "monto": 950.0
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "4. Falla: Límite Excedido (> $1000)",
                                            summary = "Caso Fallo: Intentar retirar más del tope máximo por operación",
                                            value = """
                                                    {
                                                      "cuenta": { "id": 1 },
                                                      "tipo": "RETIRO",
                                                      "monto": 2500.0
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "5. Falla: Cuenta Inactiva",
                                            summary = "Caso Fallo: Operación sobre cuenta bloqueada/inactiva (ID 2)",
                                            value = """
                                                    {
                                                      "cuenta": { "id": 2 },
                                                      "tipo": "DEPOSITO",
                                                      "monto": 100.0
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @RequestBody Cliente movimiento) {
        Cliente guardado = movimientoService.registrarMovimiento(movimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(com.example.demo_service.exception.ReglaNegocioException.class)
    public ResponseEntity<java.util.Map<String, Object>> manejarReglaNegocio(com.example.demo_service.exception.ReglaNegocioException ex) {
        java.util.Map<String, Object> error = new java.util.LinkedHashMap<>();
        error.put("timestamp", java.time.LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        error.put("path", "/api/movimientos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
