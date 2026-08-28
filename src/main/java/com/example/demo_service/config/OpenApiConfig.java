package com.example.demo_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API de Supermercado")
                .version("1.0.0")
                .description("API REST por capas para administrar categor�as, proveedores, productos, clientes y ventas. Al crear una venta se validan precios, productos activos y stock; el total se calcula y el stock se descuenta de forma transaccional.")
                .contact(new Contact().name("Equipo de Supermercado").email("soporte@supermercado.example")));
    }
}