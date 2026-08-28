package com.example.demo_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración OpenAPI 3 / Swagger UI para el ejemplo simplificado de 2 entidades.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Bancaria - Demostración de Lógica de Negocio (2 Entidades)")
                        .version("1.0.0")
                        .description("""
                                ### Arquitectura Limpia en Capas
                                Demostración con 2 entidades relacionadas (**Cuenta** y **Movimiento**) 
                                enfocada principalmente en la **Lógica de Negocio en la Capa de Servicio**.

                                ---
                                ### Reglas de Negocio en `MovimientoService`:
                                1. **Monto Válido**: El importe de la transacción debe ser un número positivo (> 0).
                                2. **Cuenta Activa**: Solo cuentas con `activa = true` pueden recibir depósitos o emitir retiros.
                                3. **Límite Máximo por Retiro**: El tope máximo permitido por retiro individual es de `$1,000.00`.
                                4. **Saldo Suficiente**: Para retiros, el saldo disponible debe ser mayor o igual al monto solicitado.
                                5. **Cálculo Automático**: El servicio recalcula y actualiza el saldo de la cuenta y guarda el movimiento con el `saldoResultante`.

                                ---
                                ### Cuentas Pre-cargadas para Pruebas Inmediatas:
                                - **Cuenta 1 (ID: 1)**: `CTA-1001` - Titular: `Carlos Gómez` - Saldo: `$500.0` - Estado: `Activa (true)`
                                - **Cuenta 2 (ID: 2)**: `CTA-1002` - Titular: `Ana Martínez` - Saldo: `$300.0` - Estado: `Inactiva (false)`
                                """)
                        .contact(new Contact()
                                .name("Equipo de Arquitectura")
                                .email("soporte@banco.com")));
    }
}
