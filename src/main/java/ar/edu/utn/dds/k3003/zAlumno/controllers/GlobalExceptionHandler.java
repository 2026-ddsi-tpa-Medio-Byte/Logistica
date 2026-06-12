package ar.edu.utn.dds.k3003.zAlumno.controllers;

import ar.edu.utn.dds.k3003.zAlumno.services.MetricasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MetricasService metricasService;

    public GlobalExceptionHandler(MetricasService metricasService) {
        this.metricasService = metricasService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        metricasService.incrementarAsignacionesErrores();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno: " + ex.getMessage());
    }
}
