package com.example.demo_service.controller;
import com.example.demo_service.model.Proveedor;
import com.example.demo_service.service.ProveedorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {
    private final ProveedorService service;
    public ProveedorController(ProveedorService service) { this.service=service; }
    @GetMapping public List<Proveedor> listar() { return service.obtenerTodos(); }
    @GetMapping("/{id}") public Proveedor obtener(@PathVariable Long id) { return service.obtenerPorId(id); }
    @PostMapping public ResponseEntity<Proveedor> crear(@RequestBody Proveedor dato) { return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dato)); }
    @PutMapping("/{id}") public Proveedor actualizar(@PathVariable Long id, @RequestBody Proveedor dato) { return service.actualizar(id, dato); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}