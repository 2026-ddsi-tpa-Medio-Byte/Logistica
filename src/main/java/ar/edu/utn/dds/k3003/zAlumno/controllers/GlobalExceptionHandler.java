package ar.edu.utn.dds.k3003.zAlumno.controllers;

import ar.edu.utn.dds.k3003.zAlumno.exceptions.IntegracionException;
import ar.edu.utn.dds.k3003.zAlumno.services.MetricasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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

    // 400: el request esta mal armado y Spring no lo pudo leer. Sin esto caian en el handler
    // generico y respondian 500, como si fuera un error del servidor.
    //  - falta un parametro obligatorio (?depositoid=... en gestionar-donacion)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleParametroFaltante(MissingServletRequestParameterException ex) {
        log.warn("Request invalido: falta el parametro {}", ex.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Falta el parametro obligatorio '" + ex.getParameterName() + "'");
    }

    //  - un parametro de la URL no tiene el tipo esperado (?algoritmo=XYZ, ?estado=OTRO)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        Class<?> tipo = ex.getRequiredType();
        String mensaje = "Valor invalido para '" + ex.getName() + "': " + ex.getValue();
        if (tipo != null && tipo.isEnum()) {
            mensaje += ". Valores posibles: " + java.util.Arrays.toString(tipo.getEnumConstants());
        }
        log.warn("Request invalido: {}", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensaje);
    }

    //  - el body no es un JSON valido o trae un valor que no corresponde (por ejemplo un enum inexistente)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleBodyInvalido(HttpMessageNotReadableException ex) {
        log.warn("Request invalido: body ilegible: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("El body del request no es valido: " + ex.getMostSpecificCause().getMessage());
    }

    // 405 / 415: metodo HTTP no permitido en esa ruta (PATCH /api/depositos) o Content-Type que el
    // endpoint no acepta (text/plain en vez de JSON). Spring ya trae el codigo correcto en la excepcion
    // (ErrorResponse); sin este handler caian en el generico y respondian 500.
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<String> handleMetodoOFormatoNoSoportado(Exception ex) {
        HttpStatusCode status = ((ErrorResponse) ex).getStatusCode();
        log.warn("Request no soportado ({}): {}", status.value(), ex.getMessage());
        return ResponseEntity.status(status).body(ex.getMessage());
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