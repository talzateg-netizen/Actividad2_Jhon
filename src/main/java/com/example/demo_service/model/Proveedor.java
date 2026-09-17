package com.example.demo_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proveedores")
@Schema(description = "Proveedor de productos")
public class Proveedor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    private String contacto;
    private String telefono;
    private String email;
    @OneToMany(mappedBy = "proveedor")
    @JsonIgnore
    private List<Producto> productos = new ArrayList<>();

    public Proveedor() { }
    public Proveedor(Long id, String nombre, String contacto, String telefono, String email) { this.id = id; this.nombre = nombre; this.contacto = contacto; this.telefono = telefono; this.email = email; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
}