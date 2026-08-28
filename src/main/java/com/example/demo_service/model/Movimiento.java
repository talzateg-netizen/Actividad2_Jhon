package com.example.demo_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entidad 2: Movimiento (Transacción sobre una Cuenta)
 * Relacionada con Cuenta mediante @ManyToOne.
 * Es el punto central para validar y aplicar la lógica de negocio bancaria.
 */
@Entity
@Table(name = "movimientos")
@Schema(description = "Entidad que representa un movimiento o transacción financiera sobre una cuenta")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único autogenerado", example = "1")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false)
    @Schema(description = "Cuenta bancaria sobre la cual se realiza el movimiento")
    private Cuenta cuenta;

    @Schema(description = "Tipo de operación: DEPOSITO o RETIRO", example = "RETIRO")
    private String tipo;

    @Schema(description = "Monto de la transacción (debe ser mayor a cero)", example = "150.0")
    private Double monto;

    @Schema(description = "Saldo de la cuenta resultante tras aplicar la lógica de negocio", example = "350.0")
    private Double saldoResultante;

    @Schema(description = "Fecha y hora en que se procesó el movimiento en el servicio")
    private LocalDateTime fecha;

    public Movimiento() {
    }

    public Movimiento(Long id, Cuenta cuenta, String tipo, Double monto, Double saldoResultante, LocalDateTime fecha) {
        this.id = id;
        this.cuenta = cuenta;
        this.tipo = tipo;
        this.monto = monto;
        this.saldoResultante = saldoResultante;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Double getSaldoResultante() {
        return saldoResultante;
    }

    public void setSaldoResultante(Double saldoResultante) {
        this.saldoResultante = saldoResultante;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
