
package com.example.demo_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_venta")
@Schema(description = "L�nea de una venta")
public class DetalleVenta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    @JsonBackReference
    private Venta venta;
    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    @Column(nullable = false)
    private Integer cantidad;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public DetalleVenta() { }
    public DetalleVenta(Long id, Venta venta, Producto producto, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal) { this.id=id; this.venta=venta; this.producto=producto; this.cantidad=cantidad; this.precioUnitario=precioUnitario; this.subtotal=subtotal; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}