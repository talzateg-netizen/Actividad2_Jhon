package com.example.demo_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción básica para indicar cuándo y por qué una regla de negocio falla.
 * Retorna código HTTP 400 (Bad Request) con el mensaje exacto del motivo del fallo.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
