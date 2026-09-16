package com.example.demo_service.service;

import com.example.demo_service.exception.ReglaNegocioException;
import com.example.demo_service.model.Cuenta;
import com.example.demo_service.model.Cliente;
import com.example.demo_service.repository.CuentaRepository;
import com.example.demo_service.repository.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================================
 * CAPA DE SERVICIO: MovimientoService
 * ============================================================================
 * Esta clase concentra la LÓGICA DE NEGOCIO principal de la aplicación bancaria.
 * Reglas de negocio evaluadas antes de autorizar cualquier movimiento:
 *
 * 1. VALIDACIÓN DE ENTRADA:
 *    - El monto debe ser estrictamente mayor a 0.
 *    - El tipo de operación debe ser "DEPOSITO" o "RETIRO".
 *    - La cuenta debe especificarse con su ID.
 *
 * 2. REGLA DE NEGOCIO 1 (Cuenta Activa):
 *    - Solo se procesan transacciones en cuentas con estado activo = true.
 *    - Si la cuenta está inactiva/bloqueada, se rechaza la transacción.
 *
 * 3. REGLA DE NEGOCIO 2 (Límite Máximo por Retiro):
 *    - Por política de seguridad, ningún retiro individual puede superar $1,000.00.
 *
 * 4. REGLA DE NEGOCIO 3 (Fondos Suficientes para Retiros):
 *    - Para retiros, el saldo disponible de la cuenta debe ser mayor o igual al monto solicitado.
 *    - Si saldo < monto, la operación se rechaza por fondos insuficientes.
 *
 * 5. REGLA DE NEGOCIO 4 (Actualización de Saldo y Auditoría):
 *    - DEPOSITO: nuevoSaldo = saldoActual + monto.
 *    - RETIRO:   nuevoSaldo = saldoActual - monto.
 *    - Se actualiza el saldo de la cuenta y se guarda el movimiento con su saldo resultante.
 */
@Service
public class MovimientoService {

    public static final double LIMITE_MAXIMO_RETIRO = 1000.0;

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    public MovimientoService(MovimientoRepository movimientoRepository, CuentaRepository cuentaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
    }

    public List<Cliente> obtenerTodos() {
        return movimientoRepository.findAll();
    }

    public List<Cliente> obtenerPorCuenta(Long cuentaId) {
        return movimientoRepository.findByCuentaId(cuentaId);
    }

    public Cliente obtenerPorId(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el movimiento con ID: " + id));
    }

    /**
     * Procesa un nuevo movimiento bancario aplicando todas las reglas de negocio.
     *
     * @param movimiento Entidad movimiento que contiene cuenta.id, tipo y monto.
     * @return Movimiento registrado con saldo resultante y fecha asignada.
     */
    @Transactional
    public Cliente registrarMovimiento(Cliente movimiento) {
        // --- 1. Validaciones estructurales y de monto ---
        if (movimiento.getMonto() == null || movimiento.getMonto() <= 0) {
            throw new ReglaNegocioException("El monto de la transacción debe ser mayor a 0.");
        }

        if (movimiento.getTipo() == null || 
            (!movimiento.getTipo().equalsIgnoreCase("DEPOSITO") && !movimiento.getTipo().equalsIgnoreCase("RETIRO"))) {
            throw new ReglaNegocioException("El tipo de movimiento debe ser 'DEPOSITO' o 'RETIRO'.");
        }

        if (movimiento.getCuenta() == null || movimiento.getCuenta().getId() == null) {
            throw new ReglaNegocioException("Debe especificar el ID de la cuenta bancaria.");
        }

        // --- 2. Cargar cuenta desde la base de datos ---
        Long cuentaId = movimiento.getCuenta().getId();
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new ReglaNegocioException("Cuenta no encontrada con ID: " + cuentaId));

        // --- 3. REGLA DE NEGOCIO 1: La cuenta debe estar ACTIVA ---
        if (Boolean.FALSE.equals(cuenta.getActiva())) {
            throw new ReglaNegocioException(
                    "Regla de negocio INCUMPLIDA [Cuenta Inactiva]: La cuenta " + cuenta.getNumeroCuenta() 
                    + " (" + cuenta.getTitular() + ") se encuentra bloqueada o inactiva y no permite transacciones."
            );
        }

        String tipoNormalizado = movimiento.getTipo().toUpperCase();
        double monto = movimiento.getMonto();
        double saldoActual = cuenta.getSaldo();
        double nuevoSaldo;

        if ("RETIRO".equals(tipoNormalizado)) {
            // --- 4. REGLA DE NEGOCIO 2: Límite máximo por retiro ---
            if (monto > LIMITE_MAXIMO_RETIRO) {
                throw new ReglaNegocioException(
                        "Regla de negocio INCUMPLIDA [Límite Excedido]: El retiro solicitado de $" + monto 
                        + " supera el límite máximo permitido de $" + LIMITE_MAXIMO_RETIRO + " por operación."
                );
            }

            // --- 5. REGLA DE NEGOCIO 3: Fondos suficientes ---
            if (saldoActual < monto) {
                throw new ReglaNegocioException(
                        "Regla de negocio INCUMPLIDA [Saldo Insuficiente]: La cuenta " + cuenta.getNumeroCuenta() 
                        + " cuenta con un saldo de $" + saldoActual + ", insuficiente para retirar $" + monto + "."
                );
            }

            nuevoSaldo = saldoActual - monto;
        } else {
            // DEPOSITO
            nuevoSaldo = saldoActual + monto;
        }

        // --- 6. REGLA DE NEGOCIO 4: Aplicar cambio de saldo y persistir ---
        cuenta.setSaldo(nuevoSaldo);
        cuentaRepository.save(cuenta);

        movimiento.setCuenta(cuenta);
        movimiento.setTipo(tipoNormalizado);
        movimiento.setMonto(monto);
        movimiento.setSaldoResultante(nuevoSaldo);
        movimiento.setFecha(LocalDateTime.now());

        return movimientoRepository.save(movimiento);
    }
}
