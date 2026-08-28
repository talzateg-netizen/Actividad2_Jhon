package com.example.demo_service.controller;
import com.example.demo_service.model.Venta;
import com.example.demo_service.service.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    private final VentaService service;
    public VentaController(VentaService service) { this.service=service; }
    @GetMapping public List<Venta> listar() { return service.obtenerTodas(); }
    @GetMapping("/cliente/{clienteId}") public List<Venta> porCliente(@PathVariable Long clienteId) { return service.porCliente(clienteId); }
    @GetMapping("/{id}") public Venta obtener(@PathVariable Long id) { return service.obtenerPorId(id); }
    @PostMapping public ResponseEntity<Venta> crear(@RequestBody Venta dato) { return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dato)); }
}