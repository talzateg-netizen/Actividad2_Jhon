package com.example.demo_service.controller;
import com.example.demo_service.model.Cliente;
import com.example.demo_service.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service=service; }
    @GetMapping public List<Cliente> listar() { return service.obtenerTodos(); }
    @GetMapping("/{id}") public Cliente obtener(@PathVariable Long id) { return service.obtenerPorId(id); }
    @PostMapping public ResponseEntity<Cliente> crear(@RequestBody Cliente dato) { return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dato)); }
    @PutMapping("/{id}") public Cliente actualizar(@PathVariable Long id, @RequestBody Cliente dato) { return service.actualizar(id, dato); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id) { service.eliminar(id); }
}