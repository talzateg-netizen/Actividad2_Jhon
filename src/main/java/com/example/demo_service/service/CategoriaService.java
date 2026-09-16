package com.example.demo_service.service;

import com.example.demo_service.exception.ReglaNegocioException;
import com.example.demo_service.model.Categoria;
import com.example.demo_service.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository repository;
    public CategoriaService(CategoriaRepository repository) { this.repository = repository; }
    public List<Categoria> obtenerTodas() { return repository.findAll(); }
    public Categoria obtenerPorId(Long id) { return repository.findById(id).orElseThrow(() -> new ReglaNegocioException("Categor�a no encontrada con ID: " + id)); }
    @Transactional public Categoria guardar(Categoria categoria) {
        if (categoria == null || categoria.getNombre() == null || categoria.getNombre().isBlank()) throw new ReglaNegocioException("El nombre de la categor�a es obligatorio.");
        categoria.setNombre(categoria.getNombre().trim());
        return repository.save(categoria);
    }
    @Transactional public Categoria actualizar(Long id, Categoria datos) {
        Categoria actual = obtenerPorId(id); actual.setNombre(datos.getNombre()); actual.setDescripcion(datos.getDescripcion()); return guardar(actual);
    }
    @Transactional public void eliminar(Long id) { repository.delete(obtenerPorId(id)); }
}