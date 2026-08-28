package com.example.demo_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad 1: Cuenta Bancaria
 * Representa la cuenta sobre la cual se realizan movimientos de dinero.
 */
@Entity
@Table(name = "cuentas")
@Schema(description = "Entidad que representa una cuenta bancaria")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la cuenta", example = "1")
    private Long id;

    @Schema(description = "Número o código de cuenta", example = "CTA-1001")
    private String numeroCuenta;

    @Schema(description = "Nombre del titular de la cuenta", example = "Carlos Gómez")
    private String titular;

    @Schema(description = "Saldo disponible actual", example = "500.0")
    private Double saldo;

    @Schema(description = "Indica si la cuenta está habilitada para operar (Regla de negocio)", example = "true")
    private Boolean activa;

    public Cuenta() {
    }

    public Cuenta(Long id, String numeroCuenta, String titular, Double saldo, Boolean activa) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.saldo = saldo;
        this.activa = activa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
}
