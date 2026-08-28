package com.example.demo_service.service;

import com.example.demo_service.exception.ReglaNegocioException;
import com.example.demo_service.model.DetalleVenta;
import com.example.demo_service.model.Producto;
import com.example.demo_service.model.Venta;
import com.example.demo_service.repository.DetalleVentaRepository;
import com.example.demo_service.repository.ProductoRepository;
import com.example.demo_service.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class DetalleVentaService {
    private final DetalleVentaRepository repository;
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    public DetalleVentaService(DetalleVentaRepository repository, VentaRepository ventaRepository, ProductoRepository productoRepository) { this.repository=repository; this.ventaRepository=ventaRepository; this.productoRepository=productoRepository; }
    public List<DetalleVenta> obtenerTodos() { return repository.findAll(); }
    public DetalleVenta obtenerPorId(Long id) { return repository.findById(id).orElseThrow(() -> new ReglaNegocioException("Detalle de venta no encontrado con ID: " + id)); }
    public List<DetalleVenta> porVenta(Long ventaId) { return repository.findByVentaId(ventaId); }
    @Transactional public DetalleVenta guardar(DetalleVenta detalle) {
        if (detalle == null || detalle.getVenta() == null || detalle.getVenta().getId() == null) throw new ReglaNegocioException("Debe especificar una venta existente.");
        if (detalle.getProducto() == null || detalle.getProducto().getId() == null) throw new ReglaNegocioException("Debe especificar un producto existente.");
        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) throw new ReglaNegocioException("La cantidad debe ser un entero mayor a cero.");
        Venta venta = ventaRepository.findById(detalle.getVenta().getId()).orElseThrow(() -> new ReglaNegocioException("Venta no encontrada con ID: " + detalle.getVenta().getId()));
        Producto producto = productoRepository.findById(detalle.getProducto().getId()).orElseThrow(() -> new ReglaNegocioException("Producto no encontrado con ID: " + detalle.getProducto().getId()));
        if (!Boolean.TRUE.equals(producto.getActivo())) throw new ReglaNegocioException("No se pueden vender productos inactivos.");
        detalle.setVenta(venta); detalle.setProducto(producto); detalle.setPrecioUnitario(producto.getPrecio());
        detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad())).setScale(2, java.math.RoundingMode.HALF_UP));
        return repository.save(detalle);
    }
    @Transactional public void eliminar(Long id) { repository.delete(obtenerPorId(id)); }
}