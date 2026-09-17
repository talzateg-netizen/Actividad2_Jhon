package com.example.demo_service.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> manejarRegla(ReglaNegocioException ex) { return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage()); }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarGeneral(Exception ex) { return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor."); }
    private ResponseEntity<Map<String, Object>> respuesta(HttpStatus estado, String mensaje) {
        Map<String, Object> error = new LinkedHashMap<>(); error.put("timestamp", LocalDateTime.now()); error.put("status", estado.value()); error.put("error", estado.getReasonPhrase()); error.put("message", mensaje); return ResponseEntity.status(estado).body(error);
    }
}