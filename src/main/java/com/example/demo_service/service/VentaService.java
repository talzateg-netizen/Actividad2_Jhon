package com.example.demo_service.service;

import com.example.demo_service.exception.ReglaNegocioException;
import com.example.demo_service.model.Cliente;
import com.example.demo_service.model.DetalleVenta;
import com.example.demo_service.model.Producto;
import com.example.demo_service.model.Venta;
import com.example.demo_service.repository.ClienteRepository;
import com.example.demo_service.repository.ProductoRepository;
import com.example.demo_service.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VentaService {
    private final VentaRepository repository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    public VentaService(VentaRepository repository, ClienteRepository clienteRepository, ProductoRepository productoRepository) { this.repository=repository; this.clienteRepository=clienteRepository; this.productoRepository=productoRepository; }
    public List<Venta> obtenerTodas() { return repository.findAll(); }
    public Venta obtenerPorId(Long id) { return repository.findById(id).orElseThrow(() -> new ReglaNegocioException("Venta no encontrada con ID: " + id)); }
    public List<Venta> porCliente(Long clienteId) { return repository.findByClienteId(clienteId); }

    @Transactional
    public Venta registrar(Venta venta) {
        if (venta == null || venta.getCliente() == null || venta.getCliente().getId() == null) throw new ReglaNegocioException("Debe especificar el ID de un cliente existente.");
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) throw new ReglaNegocioException("La venta debe incluir al menos un detalle.");
        Cliente cliente = clienteRepository.findById(venta.getCliente().getId()).orElseThrow(() -> new ReglaNegocioException("Cliente no encontrado con ID: " + venta.getCliente().getId()));
        Set<Long> productosIncluidos = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVenta detalle : venta.getDetalles()) {
            if (detalle == null || detalle.getProducto() == null || detalle.getProducto().getId() == null) throw new ReglaNegocioException("Cada detalle debe especificar un producto existente.");
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) throw new ReglaNegocioException("La cantidad de cada detalle debe ser mayor a cero.");
            Long productoId = detalle.getProducto().getId();
            if (!productosIncluidos.add(productoId)) throw new ReglaNegocioException("No se puede repetir un producto dentro de la misma venta: " + productoId);
            Producto producto = productoRepository.findById(productoId).orElseThrow(() -> new ReglaNegocioException("Producto no encontrado con ID: " + productoId));
            if (!Boolean.TRUE.equals(producto.getActivo())) throw new ReglaNegocioException("El producto '" + producto.getNombre() + "' est� inactivo.");
            if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) throw new ReglaNegocioException("El precio del producto '" + producto.getNombre() + "' no es v�lido.");
            if (producto.getStock() == null || producto.getStock() < detalle.getCantidad()) throw new ReglaNegocioException("Stock insuficiente para el producto '" + producto.getNombre() + "'. Disponible: " + producto.getStock() + ".");
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad())).setScale(2, RoundingMode.HALF_UP);
            detalle.setProducto(producto); detalle.setPrecioUnitario(producto.getPrecio()); detalle.setSubtotal(subtotal); detalle.setVenta(venta);
            producto.setStock(producto.getStock() - detalle.getCantidad()); productoReposito