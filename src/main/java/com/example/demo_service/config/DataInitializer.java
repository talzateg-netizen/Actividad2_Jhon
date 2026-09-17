package com.example.demo_service.config;

import com.example.demo_service.model.Categoria;
import com.example.demo_service.model.Cliente;
import com.example.demo_service.model.Producto;
import com.example.demo_service.model.Proveedor;
import com.example.demo_service.repository.CategoriaRepository;
import com.example.demo_service.repository.ClienteRepository;
import com.example.demo_service.repository.ProductoRepository;
import com.example.demo_service.repository.ProveedorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner inicializarDatos(CategoriaRepository categorias, ProveedorRepository proveedores, ProductoRepository productos, ClienteRepository clientes) {
        return args -> {
            if (categorias.count() == 0 && proveedores.count() == 0 && productos.count() == 0 && clientes.count() == 0) {
                Categoria abarrotes = categorias.save(new Categoria(null, "Abarrotes", "Productos b�sicos de despensa"));
                Categoria frescos = categorias.save(new Categoria(null, "Frescos", "Frutas, verduras y alimentos refrigerados"));
                Proveedor alimentosAndinos = proveedores.save(new Proveedor(null, "Alimentos Andinos", "Laura P�rez", "3005550101", "ventas@andinos.example"));
                Proveedor distribucionesNorte = proveedores.save(new Proveedor(null, "Distribuciones del Norte", "Miguel Torres", "3005550102", "comercial@norte.example"));
                productos.save(new Producto(null, "Arroz premium 1 kg", "Arroz blanco de grano largo", new BigDecimal("5200.00"), 40, true, abarrotes, alimentosAndinos));
                productos.save(new Producto(null, "Aceite vegetal 1 L", "Aceite de cocina", new BigDecimal("9800.00"), 25, true, abarrotes, distribucionesNorte));
                productos.save(new Producto(null, "Manzana roja kg", "Manzana fresca nacional", new BigDecimal("7500.00"), 18, true, frescos, alimentosAndinos));
                clientes.save(new Cliente(null, "Mar�a G�mez", "CC-1001", "maria.gomez@example.com", "3105550101", true));
                clientes.save(new Cliente(null, "Juan Rodr�guez", "CC-1002", "juan.rodriguez@example.com", "3105550102", true));
            }
        };
    }
}