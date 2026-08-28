package com.example.demo_service.controller;
import com.example.demo_service.model.DetalleVenta;
import com.example.demo_service.service.DetalleVentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/detalles-venta")
public class DetalleVentaController {
    private final DetalleVentaService service;
    public DetalleVentaController(DetalleVentaService service) { this.service=service; }
    @GetMapping public List<DetalleVenta> listar() { return service.obtenerTodos(); }
    @GetMapping("/venta/{ventaId}") public List<DetalleVenta> porVenta(@PathVariable Long ventaId) { return service.porVenta(ventaId); }
    @GetMapping("/{id}") public DetalleVenta obtener(@PathVariable Long id) { return service.obtenerPorId(id); }
    @PostMapping public ResponseEntity<DetalleVenta> crear(@RequestBody DetalleVenta dato) { return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dato)); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}