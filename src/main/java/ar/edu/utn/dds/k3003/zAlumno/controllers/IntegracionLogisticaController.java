package ar.edu.utn.dds.k3003.zAlumno.controllers;

import ar.edu.utn.dds.k3003.catedra.dtos.logistica.AsignacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.DepositoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.TipoAlgoritmoEnum;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;
import ar.edu.utn.dds.k3003.zAlumno.services.LogisticaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints "de cátedra" (sin prefijo /api) que permiten la integración HTTP con los
 * demás módulos. Adaptan los DTOs internos de Logística (zAlumno) a los DTOs
 * compartidos definidos por la cátedra.
 *
 * Implementa el flujo obligatorio "Donaciones -> Logística: gestionarDonacion"
 * mediante POST /asignaciones.
 */
@RestController
public class IntegracionLogisticaController {

  private final LogisticaService logisticaService;

  @Autowired
  public IntegracionLogisticaController(LogisticaService logisticaService) {
    this.logisticaService = logisticaService;
  }

  @PostMapping("/depositos")
  public ResponseEntity<DepositoDTO> crearDeposito(@RequestBody DepositoDTO depositoDTO) {
    LogisticaDTOs.DepositoDTO nuevo = new LogisticaDTOs.DepositoDTO(
        depositoDTO.nombre(),
        depositoDTO.id(),
        depositoDTO.direccion(),
        depositoDTO.capacidadMaxima(),
        0,
        aTipoAlgoritmoLocal(depositoDTO.algoritmo()));
    LogisticaDTOs.DepositoDTO guardado = logisticaService.agregarDeposito(nuevo);
    return ResponseEntity.status(HttpStatus.CREATED).body(aDepositoDTOCatedra(guardado));
  }

  @GetMapping("/depositos")
  public ResponseEntity<List<DepositoDTO>> obtenerDepositos() {
    List<DepositoDTO> depositos = logisticaService.obtenerTodosDepositosDTO().stream()
        .map(IntegracionLogisticaController::aDepositoDTOCatedra)
        .toList();
    return ResponseEntity.ok(depositos);
  }

  @GetMapping("/depositos/{id}")
  public ResponseEntity<DepositoDTO> buscarDepositoPorID(@PathVariable String id) {
    LogisticaDTOs.DepositoDTO deposito = logisticaService.buscarDepositoIDDTO(id);
    if (deposito == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(aDepositoDTOCatedra(deposito));
  }

  @PostMapping("/asignaciones")
  public ResponseEntity<DepositoDTO> gestionarDonacion(@RequestBody LogisticaDTOs.GestionDonacionDTO body) {
    LogisticaDTOs.DepositoDTO resultado = logisticaService.gestionarDonacion(
        body.depositoID(), body.donacionID(), body.productoID(), body.cantidad());
    if (resultado == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(aDepositoDTOCatedra(resultado));
  }

  @GetMapping("/asignaciones/{paqueteID}")
  public ResponseEntity<AsignacionDTO> buscarAsignacionPorPaqueteID(@PathVariable String paqueteID) {
    LogisticaDTOs.AsignacionDTO asignacion = logisticaService.buscarAsignacionPorPaqueteIDDTO(paqueteID);
    if (asignacion == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(new AsignacionDTO(
        asignacion.asignacionid(),
        asignacion.paqueteid(),
        asignacion.necesidadid(),
        asignacion.fecha(),
        ar.edu.utn.dds.k3003.catedra.dtos.logistica.EstadoAsginacionEnum.valueOf(asignacion.estado().name())));
  }

  private static DepositoDTO aDepositoDTOCatedra(LogisticaDTOs.DepositoDTO deposito) {
    return new DepositoDTO(
        deposito.depositoid(),
        aTipoAlgoritmoCatedra(deposito.algoritmo()),
        deposito.nombre(),
        deposito.direccion(),
        deposito.capacidadMaxima(),
        List.of());
  }

  private static TipoAlgoritmoEnum aTipoAlgoritmoCatedra(LogisticaDTOs.TipoAlgoritmoEnum algoritmo) {
    if (algoritmo == null) {
      return null;
    }
    return switch (algoritmo) {
      case SUBATENDIDOS -> TipoAlgoritmoEnum.SUB_ATENDIDOS;
      case PRIOSCORE -> TipoAlgoritmoEnum.PRIORIDAD_POR_SCORE;
      case NULL -> null;
    };
  }

  private static LogisticaDTOs.TipoAlgoritmoEnum aTipoAlgoritmoLocal(TipoAlgoritmoEnum algoritmo) {
    if (algoritmo == null) {
      return LogisticaDTOs.TipoAlgoritmoEnum.NULL;
    }
    return switch (algoritmo) {
      case SUB_ATENDIDOS -> LogisticaDTOs.TipoAlgoritmoEnum.SUBATENDIDOS;
      case PRIORIDAD_POR_SCORE -> LogisticaDTOs.TipoAlgoritmoEnum.PRIOSCORE;
    };
  }
}
