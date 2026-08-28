package com.example.demo_service.controller;

import com.example.demo_service.model.Cuenta;
import com.example.demo_service.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/cuentas")
@Tag(name = "Cuentas", description = "Endpoints para la consulta y apertura de cuentas bancarias")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    @Operation(summary = "Listar cuentas", description = "Obtiene la lista completa de cuentas registradas con su saldo y estado.")
    public ResponseEntity<List<Cuenta>> listarTodas() {
        return ResponseEntity.ok(cuentaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID", description = "Consulta una cuenta bancaria específica.")
    public ResponseEntity<Cuenta> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva cuenta", description = "Abre una nueva cuenta bancaria directamente usando la entidad.")
    public ResponseEntity<Cuenta> crear(@RequestBody Cuenta cuenta) {
        Cuenta guardada = cuentaService.guardar(cuenta);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(com.example.demo_service.exception.ReglaNegocioException.class)
    public ResponseEntity<java.util.Map<String, Object>> manejarReglaNegocio(com.example.demo_service.exception.ReglaNegocioException ex) {
        java.util.Map<String, Object> error = new java.util.LinkedHashMap<>();
        error.put("timestamp", java.time.LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        error.put("path", "/api/cuentas");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
