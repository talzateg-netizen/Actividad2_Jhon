package com.example.demo_service.service;

import com.example.demo_service.exception.ReglaNegocioException;
import com.example.demo_service.model.Categoria;
import com.example.demo_service.model.Producto;
import com.example.demo_service.model.Proveedor;
import com.example.demo_service.repository.CategoriaRepository;
import com.example.demo_service.repository.ProductoRepository;
import com.example.demo_service.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;
    public ProductoService(ProductoRepository repository, CategoriaRepository categoriaRepository, ProveedorRepository proveedorRepository) { this.repository=repository; this.categoriaRepository=categoriaRepository; this.proveedorRepository=proveedorRepository; }
    public List<Producto> obtenerTodos() { return repository.findAll(); }
    public List<Producto> obtenerActivos() { return repository.findByActivoTrue(); }
    public Producto obtenerPorId(Long id) { return repository.findById(id).orElseThrow(() -> new ReglaNegocioException("Producto no encontrado con ID: " + id)); }
    @Transactional public Producto guardar(Producto producto) {
        validar(producto);
        Categoria categoria = cargarCategoria(producto.getCategoria());
        Proveedor proveedor = cargarProveedor(producto.getProveedor());
        producto.setCategoria(categoria); producto.setProveedor(proveedor);
        if (producto.getActivo() == null) producto.setActivo(true);
        return repository.save(producto);
    }
    @Transactional public Producto actualizar(Long id, Producto datos) {
        Producto actual = obtenerPorId(id); actual.setNombre(datos.getNombre()); actual.setDescripcion(datos.getDescripcion()); actual.setPrecio(datos.getPrecio()); actual.setStock(datos.getStock()); actual.setActivo(datos.getActivo()); actual.setCategoria(datos.getCategoria()); actual.setProveedor(datos.getProveedor()); return guardar(actual);
    }
    @Transactional public void eliminar(Long id) { repository.delete(obtenerPorId(id)); }
    public List<Producto> porCategoria(Long categoriaId) { return repository.findByCategoriaId(categoriaId); }
    public List<Producto> porProveedor(Long proveedorId) { return repository.findByProveedorId(proveedorId); }
    private void validar(Producto p) {
        if (p == null || p.getNombre() == null || p.getNombre().isBlank()) throw new ReglaNegocioException("El nombre del producto es obligatorio.");
        if (p.getPrecio() == null || p.getPrecio().compareTo(BigDecimal.ZERO) <= 0) throw new ReglaNegocioException("El precio del producto debe ser mayor a cero.");
        if (p.getStock() == null || p.getStock() < 0) throw new ReglaNegocioException("El stock no puede ser negativo.");
        if (p.getCategoria() == null || p.getCategoria().getId() == null) throw new ReglaNegocioException("Debe especificar una categor�a existente.");
        if (p.getProveedor() == null || p.getProveedor().getId() == null) throw new ReglaNegocioException("Debe especificar un proveedor existente.");
    }
    private Categoria cargarCategoria(Categoria ref) { return categoriaRepository.findById(ref.getId()).orElseThrow(() -> new ReglaNegocioException("Categor�a no encontrada con ID: " + ref.getId())); }
    private Proveedor cargarProveedor(Proveedor ref) { return proveedorRepository.findById(ref.getId()).orElseThrow(() -> new ReglaNegocioException("Proveedor no encontrado con ID: " + ref.getId())); }
}