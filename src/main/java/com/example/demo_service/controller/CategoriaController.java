package com.example.demo_service.controller;
import com.example.demo_service.model.Categoria;
import com.example.demo_service.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaService service;
    public CategoriaController(CategoriaService service) { this.service=service; }
    @GetMapping public List<Categoria> listar() { return service.obtenerTodas(); }
    @GetMapping("/{id}") public Categoria obtener(@PathVariable Long id) { return service.obtenerPorId(id); }
    @PostMapping public ResponseEntity<Categoria> crear(@RequestBody Categoria dato) { return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dato)); }
    @PutMapping("/{id}") public Categoria actualizar(@PathVariable Long id, @RequestBody Categoria dato) { return service.actualizar(id, dato); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}