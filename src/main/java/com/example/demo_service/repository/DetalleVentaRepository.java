package com.example.demo_service.repository;
import com.example.demo_service.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVentaId(Long ventaId);
    List<DetalleVenta> findByProductoId(Long productoId);
}