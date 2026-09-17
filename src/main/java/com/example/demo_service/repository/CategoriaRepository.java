package com.example.demo_service.repository;
import com.example.demo_service.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoriaRepository extends JpaRepository<Categoria, Long> { }