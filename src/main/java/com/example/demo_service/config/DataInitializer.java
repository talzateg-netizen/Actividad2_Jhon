package com.example.demo_service.config;

import com.example.demo_service.model.Cuenta;
import com.example.demo_service.repository.CuentaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Inicializador de datos de prueba en la base de datos H2.
 * Pre-carga cuentas para probar de inmediato las reglas de negocio en los endpoints.
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner inicializarDatos(CuentaRepository cuentaRepository) {
        return args -> {
            if (cuentaRepository.count() == 0) {
                // Cuenta 1: ACTIVA con saldo de $500.0
                // Ideal para probar depósitos y retiros exitosos, o saldo insuficiente (> $500.0)
                cuentaRepository.save(new Cuenta(null, "CTA-1001", "Carlos Gómez", 500.0, true));

                // Cuenta 2: INACTIVA / BLOQUEADA con saldo de $300.0
                // Ideal para probar la regla de negocio: rechazo por cuenta inactiva
                cuentaRepository.save(new Cuenta(null, "CTA-1002", "Ana Martínez", 300.0, false));
            }
        };
    }
}
