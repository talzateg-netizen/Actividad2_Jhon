package com.example.demo_service.repository;
import com.example.demo_service.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> { }