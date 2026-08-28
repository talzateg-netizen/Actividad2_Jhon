package com.example.demo_service.controller;
import com.example.demo_service.model.Producto;
import com.example.demo_service.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService service;
    public ProductoController(ProductoService service) { this.service=service; }
    @GetMapping public List<Producto> listar() { return service.obtenerTodos(); }
    @GetMapping("/activos") public List<Producto> activos() { return service.obtenerActivos(); }
    @GetMapping("/categoria/{categoriaId}") public List<Producto> porCategoria(@PathVariable Long categoriaId) { return service.porCategoria(categoriaId); }
    @GetMapping("/proveedor/{proveedorId}") public List<Producto> porProveedor(@PathVariable Long proveedorId) { return service.porProveedor(proveedorId); }
    @GetMapping("/{id}") public Producto obtener(@PathVariable Long id) { return service.obtenerPorId(id); }
    @PostMapping public ResponseEntity<Producto> crear(@RequestBody Producto dato) { return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dato)); }
    @PutMapping("/{id}") public Producto actualizar(@PathVariable Long id, @RequestBody Producto dato) { return service.actualizar(id, dato); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}