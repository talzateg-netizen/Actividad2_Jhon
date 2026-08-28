package com.example.demo_service.service;

import com.example.demo_service.exception.ReglaNegocioException;
import com.example.demo_service.model.Cuenta;
import com.example.demo_service.repository.CuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Capa de servicio para la gestión de Cuentas.
 */
@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public CuentaService(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    public List<Cuenta> obtenerTodas() {
        return cuentaRepository.findAll();
    }

    public Cuenta obtenerPorId(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró la cuenta con ID: " + id));
    }

    @Transactional
    public Cuenta guardar(Cuenta cuenta) {
        if (cuenta.getSaldo() == null) {
            cuenta.setSaldo(0.0);
        }
        if (cuenta.getActiva() == null) {
            cuenta.setActiva(true);
        }
        return cuentaRepository.save(cuenta);
    }
}
