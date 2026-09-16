package com.example.demo_service.service;

import com.example.demo_service.exception.ReglaNegocioException;
import com.example.demo_service.model.Cliente;
import com.example.demo_service.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository repository;
    public ClienteService(ClienteRepository repository) { this.repository=repository; }
    public List<Cliente> obtenerTodos() { return repository.findAll(); }
    public Cliente obtenerPorId(Long id) { return repository.findById(id).orElseThrow(() -> new ReglaNegocioException("Cliente no encontrado con ID: " + id)); }
    @Transactional public Cliente guardar(Cliente cliente) {
        if (cliente == null || cliente.getNombre() == null || cliente.getNombre().isBlank()) throw new ReglaNegocioException("El nombre del cliente es obligatorio.");
        if (cliente.getDocumento() == null || cliente.getDocumento().isBlank()) throw new ReglaNegocioException("El documento del cliente es obligatorio.");
        if (cliente.getActivo() == null) cliente.setActivo(true); return repository.save(cliente);
    }
    @Transactional public Cliente actualizar(Long id, Cliente datos) {
        Cliente actual=obtenerPorId(id); actual.setNombre(datos.getNombre()); actual.setDocumento(datos.getDocumento()); actual.setEmail(datos.getEmail()); actual.setTelefono(datos.getTelefono()); actual.setActivo(datos.getActivo()); return guardar(actual);
    }
    @Transactional public void eliminar(Long id) { repository.delete(obtenerPorId(id)); }
}