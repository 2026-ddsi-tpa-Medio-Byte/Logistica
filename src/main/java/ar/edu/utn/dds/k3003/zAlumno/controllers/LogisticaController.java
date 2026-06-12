package ar.edu.utn.dds.k3003.zAlumno.controllers;

import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;
import ar.edu.utn.dds.k3003.zAlumno.services.LogisticaService;
import ar.edu.utn.dds.k3003.zAlumno.services.MetricasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/depositos")
    public ResponseEntity<List<LogisticaDTOs.DepositoDTO>> obtenerTodosDepositos() {
        List<LogisticaDTOs.DepositoDTO> lista = logisticaService.obtenerTodosDepositosDTO();
        metricasService.incrementarDepositosConsultados();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/depositos/{id}")
    public ResponseEntity<LogisticaDTOs.DepositoDTO> buscarDepositoPorID(@PathVariable String id) {
        LogisticaDTOs.DepositoDTO deposito = logisticaService.buscarDepositoIDDTO(id);
        if (deposito == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deposito);
    }

    @PostMapping("/depositos")
    public ResponseEntity<LogisticaDTOs.DepositoDTO> crearDeposito(@RequestBody LogisticaDTOs.DepositoDTO nuevo) {
        LogisticaDTOs.DepositoDTO guardado = logisticaService.agregarDeposito(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PostMapping("/depositos/gestionar-donacion")
    public ResponseEntity<LogisticaDTOs.DepositoDTO> gestionarDonacion(
            @RequestParam String depositoid,
            @RequestParam String donacionid,
            @RequestParam String productoid,
            @RequestParam Integer cantidad) {

        try {
            LogisticaDTOs.DepositoDTO resultado = logisticaService.gestionarDonacion(depositoid, donacionid, productoid, cantidad);
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

    @GetMapping("/asignaciones/paquetes/{paqueteId}")
    public ResponseEntity<LogisticaDTOs.AsignacionDTO> buscarAsignacionPorPaqueteID(@PathVariable String paqueteId) {
        LogisticaDTOs.AsignacionDTO asignacion = logisticaService.buscarAsignacionPorPaqueteIDDTO(paqueteId);
        if (asignacion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(asignacion);
    }

    @DeleteMapping("/limpiar-base")
    public ResponseEntity<String> limpiarBaseDeDatos() {
        logisticaService.limpiarTodaLaBase();
        return ResponseEntity.ok("Base de datos de Logística limpiada con éxito para la evaluación.");
    }

    @PutMapping("/depositos/{id}/algoritmo")
    public ResponseEntity<String> configurarAlgoritmo(
            @PathVariable String id,
            @RequestParam LogisticaDTOs.TipoAlgoritmoEnum algoritmo) {

        logisticaService.setAlgoritmoMM(id, algoritmo);
        return ResponseEntity.ok("Algoritmo configurado correctamente.");
    }

    @PostMapping("/asignaciones/reportar-entrega")
    public ResponseEntity<String> reportarEntregaEfectiva(@RequestBody LogisticaDTOs.PaqueteDTO paquete) {
        try {
            logisticaService.reportarEntrega(paquete);
            metricasService.incrementarEntregasReportadas();
            return ResponseEntity.ok("Entrega registrada con éxito.");
        } catch (Exception e) {
            metricasService.incrementarAsignacionesErrores();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la entrega: " + e.getMessage());
        }
    }
}
