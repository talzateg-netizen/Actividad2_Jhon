package com.example.demo_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo_service.exception.ReglaNegocioException;
import com.example.demo_service.model.Proveedor;
import com.example.demo_service.repository.ProveedorRepository;

@Service
public class ProveedorService {
    private final ProveedorRepository repository;
    public ProveedorService(ProveedorRepository repository) { this.repository = repository; }
    public List<Proveedor> obtenerTodos() { return repository.findAll(); }
    public Proveedor obtenerPorId(Long id) { return repository.findById(id).orElseThrow(() -> new ReglaNegocioException("Proveedor no encontrado con ID: " + id)); }
    @Transactional public Proveedor guardar(Proveedor proveedor) {
        if (proveedor == null || proveedor.getNombre() == null || proveedor.getNombre().isBlank()) throw new ReglaNegocioException("El nombre del proveedor es obligatorio.");
        proveedor.setNombre(proveedor.getNombre().trim()); return repository.save(proveedor);
    }
    @Transactional public Proveedor actualizar(Long id, Proveedor datos) {
        Proveedor actual = obtenerPorId(id); actual.setNombre(datos.getNombre()); actual.setContacto(datos.getContacto()); actual.setTelefono(datos.getTelefono()); actual.setEmail(datos.getEmail()); return guardar(actual);
    }
    @Transactional public void eliminar(Long id) { repository.delete(obtenerPorId(id)); }
}