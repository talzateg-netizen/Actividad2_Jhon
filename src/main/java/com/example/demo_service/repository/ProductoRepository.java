package com.example.demo_service.repository;
import com.example.demo_service.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();
    List<Producto> findByCategoriaId(Long categoriaId);
    List<Producto> findByProveedorId(Long proveedorId);
}