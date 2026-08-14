package ar.edu.utn.dds.k3003.zAlumno.controllers;

import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;
import ar.edu.utn.dds.k3003.zAlumno.services.LogisticaService;
import ar.edu.utn.dds.k3003.zAlumno.services.MetricasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LogisticaController {

    private final LogisticaService logisticaService;
    private final MetricasService metricasService;

    @Autowired
    public LogisticaController(LogisticaService logisticaService, MetricasService metricasService) {
        this.logisticaService = logisticaService;
        this.metricasService = metricasService;
    }

    @Operation(summary = "Muestra todos los depósitos")
    @GetMapping("/depositos")
    public ResponseEntity<List<LogisticaDTOs.DepositoDTO>> obtenerTodosDepositos() {
        List<LogisticaDTOs.DepositoDTO> lista = logisticaService.obtenerTodosDepositosDTO();
        metricasService.incrementarDepositosConsultados();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Muestra depósito por ID")
    @GetMapping("/depositos/{id}")
    public ResponseEntity<LogisticaDTOs.DepositoDTO> buscarDepositoPorID(@PathVariable String id) {
        LogisticaDTOs.DepositoDTO deposito = logisticaService.buscarDepositoIDDTO(id);
        if (deposito == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deposito);
    }

    @Operation(summary = "Crea un nuevo depósito")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(
                    name = "Depósito nuevo",
                    description = "El depositoid se autogenera, no hace falta enviarlo.",
                    value = """
                            {
                              "nombre": "stirng",
                              "depositoid": null,
                              "direccion": "string",
                              "capacidadMaxima": 1200,
                              "stockActual": 0,
                              "algoritmo": "SUBATENDIDOS"
                            }
                            """
            ))
    )
    @PostMapping("/depositos")
    public ResponseEntity<LogisticaDTOs.DepositoDTO> crearDeposito(
            @org.springframework.web.bind.annotation.RequestBody LogisticaDTOs.DepositoDTO nuevo) {
        LogisticaDTOs.DepositoDTO guardado = logisticaService.agregarDeposito(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @Operation(summary = "Gestiona una donación y la asigna a una necesidad")
    @PostMapping("/depositos/gestionar-donacion")
    public ResponseEntity<LogisticaDTOs.GestionDonacionResponseDTO> gestionarDonacion(
            @RequestParam String depositoid,
            @RequestParam String donacionid,
            @RequestParam String productoid,
            @RequestParam Integer cantidad) {

        try {
            LogisticaDTOs.GestionDonacionResponseDTO resultado = logisticaService.gestionarDonacion(depositoid, donacionid, productoid, cantidad);
            metricasService.incrementarAsignacionesCreadas();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            metricasService.incrementarAsignacionesErrores();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/necesidades/{id}")
    public ResponseEntity<DonacionYEntiDTOs.NecesidadMaterialDTO> buscarNecesidadPorID(@PathVariable String id) {
        DonacionYEntiDTOs.NecesidadMaterialDTO necesidad = logisticaService.buscarNecesidadPorIDDTO(id);
        if (necesidad == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(necesidad);
    }

    @Operation(summary = "Busca una asignación por ID de paquete")
    @GetMapping("/asignaciones/paquetes/{paqueteId}")
    public ResponseEntity<LogisticaDTOs.AsignacionDTO> buscarAsignacionPorPaqueteID(@PathVariable String paqueteId) {
        LogisticaDTOs.AsignacionDTO asignacion = logisticaService.buscarAsignacionPorPaqueteIDDTO(paqueteId);
        if (asignacion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(asignacion);
    }

    @Operation(summary = "Limpia toda la base de datos")
    @DeleteMapping("/limpiar-base")
    public ResponseEntity<String> limpiarBaseDeDatos() {
        logisticaService.limpiarTodaLaBase();
        return ResponseEntity.ok("Base de datos de Logística limpiada con éxito para la evaluación.");
    }

    @Operation(summary = "Configura el algoritmo de matchmaking de un depósito")
    @PutMapping("/depositos/{id}/algoritmo")
    public ResponseEntity<String> configurarAlgoritmo(
            @PathVariable String id,
            @RequestParam LogisticaDTOs.TipoAlgoritmoEnum algoritmo) {

        logisticaService.setAlgoritmoMM(id, algoritmo);
        return ResponseEntity.ok("Algoritmo configurado correctamente.");
    }

    @Operation(summary = "Da de alta una asignación calculada por un worker")
    @PostMapping("/asignaciones/alta")
    public ResponseEntity<LogisticaDTOs.AsignacionDTO> altaAsignacion(@RequestBody LogisticaDTOs.AsignacionDTO asignacionDTO) {
        LogisticaDTOs.AsignacionDTO guardada = logisticaService.altaAsignacion(asignacionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @Operation(summary = "Reporta la entrega efectiva de un paquete. Solo requiere el paqueteid; " +
            "la cantidad, el producto y la donación se toman de la asignación guardada.")
    @PostMapping("/asignaciones/reportar-entrega")
    public ResponseEntity<LogisticaDTOs.ReporteEntregaResponseDTO> reportarEntregaEfectiva(
            @org.springframework.web.bind.annotation.RequestBody LogisticaDTOs.ReportarEntregaRequestDTO request) {

        // validación de entrada: el paqueteid es obligatorio
        if (request == null || request.paqueteid() == null || request.paqueteid().isBlank()) {
            return ResponseEntity.badRequest().body(new LogisticaDTOs.ReporteEntregaResponseDTO(
                    "Debe indicar el paqueteid", null, null, null));
        }

        // la asignación tiene que existir
        LogisticaDTOs.AsignacionDTO asignacion =
                logisticaService.buscarAsignacionPorPaqueteIDDTO(request.paqueteid());
        if (asignacion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LogisticaDTOs.ReporteEntregaResponseDTO(
                    "No existe asignación para el paquete: " + request.paqueteid(), null, null, null));
        }

        // no se puede volver a entregar algo ya entregado (idempotencia)
        if (asignacion.estado() == LogisticaDTOs.EstadoAsginacionEnum.COMPLETADA) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new LogisticaDTOs.ReporteEntregaResponseDTO(
                    "La asignación ya fue entregada", asignacion.donacionid(),
                    "Asignación ya completada", asignacion.asignacionid()));
        }

        try {
            LogisticaDTOs.ReporteEntregaResponseDTO resultado =
                    logisticaService.reportarEntrega(request.paqueteid());
            metricasService.incrementarEntregasReportadas();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            metricasService.incrementarAsignacionesErrores();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LogisticaDTOs.ReporteEntregaResponseDTO(
                            "Error al procesar la entrega: " + e.getMessage(),
                            null,
                            null,
                            null));
        }
    }
}
