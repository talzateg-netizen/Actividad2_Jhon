package com.example.demo_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
@Schema(description = "Categor�a de productos")
public class Categoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nombre;
    private String descripcion;
    @OneToMany(mappedBy = "categoria")
    @JsonIgnore
    private List<Producto> productos = new ArrayList<>();

    public Categoria() { }
    public Categoria(Long id, String nombre, String descripcion) { this.id = id; this.nombre = nombre; this.descripcion = descripcion; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
}