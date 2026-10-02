package ar.edu.utn.dds.k3003.zAlumno.controllers;

import ar.edu.utn.dds.k3003.zAlumno.exceptions.IntegracionException;
import ar.edu.utn.dds.k3003.zAlumno.services.MetricasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MetricasService metricasService;

    public GlobalExceptionHandler(MetricasService metricasService) {
        this.metricasService = metricasService;
    }

    // 404: no existe lo que se busca
    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(java.util.NoSuchElementException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // 400: datos invalidos (nombre vacio, capacidad <= 0, cantidad <= 0...)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        log.warn("Request invalido: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // 409: el estado actual no permite la operacion (id repetido, deposito con stock, sin stock...)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleConflict(IllegalStateException ex) {
        log.warn("Conflicto: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // 409: dos operaciones modificaron lo mismo a la vez (@Version) o se violo el unique de paqueteid
    @ExceptionHandler({OptimisticLockingFailureException.class, DataIntegrityViolationException.class})
    public ResponseEntity<String> handleConcurrencia(RuntimeException ex) {
        log.warn("Conflicto de concurrencia: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("El recurso fue modificado por otra operacion al mismo tiempo, reintentar.");
    }

    // 502: fallo otro modulo; la operacion no se completo y se puede reintentar
    // El body va como JSON {"error": "..."}: es el formato con el que los modulos de DonaTrack
    // avisan "yo conteste, el que fallo es otro modulo". Con texto plano, el MCP confunde este
    // 502 con el 502 que da Render cuando el servicio esta dormido.
    @ExceptionHandler(IntegracionException.class)
    public ResponseEntity<java.util.Map<String, String>> handleIntegracion(IntegracionException ex) {
        log.error("Fallo de integracion: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(java.util.Map.of("error", ex.getMessage()));
    }

    // Request a una ruta que no existe (health check o UptimeRobot pegandole a "/",
    // favicon, etc). No es un error de negocio: se responde 404 sin loguear ERROR,
    // para no ensuciar el log central ni disparar alarmas de errores por los pings.
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResource(NoResourceFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Error no controlado: {}", ex.getMessage(), ex);
        metricasService.incrementarAsignacionesErrores();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + ex.getMessage());
    }
}