package ar.edu.utn.dds.k3003.zAlumno.controllers;

import ar.edu.utn.dds.k3003.catedra.dtos.logistica.AsignacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.DepositoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.TipoAlgoritmoEnum;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;
import ar.edu.utn.dds.k3003.zAlumno.services.LogisticaService;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class IntegracionLogisticaController {

  private final LogisticaService logisticaService;

  @Autowired
  public IntegracionLogisticaController(LogisticaService logisticaService) {
    this.logisticaService = logisticaService;
  }

  @Operation(summary = "Crea un depósito desde otro módulo")
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

  @Operation(summary = "Obtiene todos los depósitos")
  @GetMapping("/depositos")
  public ResponseEntity<List<DepositoDTO>> obtenerDepositos() {
    List<DepositoDTO> depositos = logisticaService.obtenerTodosDepositosDTO().stream()
        .map(IntegracionLogisticaController::aDepositoDTOCatedra)
        .toList();
    return ResponseEntity.ok(depositos);
  }

  @Operation(summary = "Obtiene un depósito por ID")
  @GetMapping("/depositos/{id}")
  public ResponseEntity<DepositoDTO> buscarDepositoPorID(@PathVariable String id) {
    LogisticaDTOs.DepositoDTO deposito = logisticaService.buscarDepositoIDDTO(id);
    if (deposito == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(aDepositoDTOCatedra(deposito));
  }

  @Operation(summary = "Recibe una donación desde el módulo Donaciones")
  @PostMapping("/asignaciones")
  public ResponseEntity<DepositoDTO> gestionarDonacion(@RequestBody LogisticaDTOs.GestionDonacionDTO body) {
    LogisticaDTOs.GestionDonacionResponseDTO resultado = logisticaService.gestionarDonacion(
        body.depositoID(), body.donacionID(), body.productoID(), body.cantidad());
    if (resultado == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(aDepositoDTOCatedra(resultado.deposito()));
  }

  @Operation(summary = "Obtiene una asignación por ID de paquete")
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

  @Operation(summary = "Consulta el stock disponible de un producto")
  @GetMapping("/stock/{productoID}")
  public ResponseEntity<Map<String, Integer>> stockDisponible(@PathVariable String productoID) {
    Integer disponible = logisticaService.stockDisponibleDeProducto(productoID);
    return ResponseEntity.ok(Map.of("disponible", disponible));
  }

  @Operation(summary = "Stock de un producto desglosado por deposito, con el total (vista detallada)")
  @GetMapping("/stock/{productoID}/detalle")
  public ResponseEntity<LogisticaDTOs.StockDetalladoDTO> stockDetallado(@PathVariable String productoID) {
    return ResponseEntity.ok(logisticaService.stockDetalladoDeProducto(productoID));
  }

  @Operation(summary = "Stock de todos los productos, desglosado por deposito")
  @GetMapping("/stock")
  public ResponseEntity<java.util.List<LogisticaDTOs.StockDetalladoDTO>> stockTotal() {
    return ResponseEntity.ok(logisticaService.stockDeTodosLosProductos());
  }

  @Operation(summary = "Asigna stock directamente a una necesidad por solicitud de Donadores(sin donacion, con lo que hay en stock)")
  @PostMapping("/asignaciones/solicitud")
  public ResponseEntity<AsignacionDTO> asignarPorSolicitud(@RequestBody LogisticaDTOs.SolicitudAsignacionDTO solicitud) {
    try {
      LogisticaDTOs.AsignacionDTO asignacion = logisticaService.asignarPorSolicitud(
              solicitud.necesidadID(),
              solicitud.productoID(),
              solicitud.cantidad()
      );
      return ResponseEntity.status(HttpStatus.CREATED).body(new AsignacionDTO(
              asignacion.asignacionid(),
              asignacion.paqueteid(),
              asignacion.necesidadid(),
              asignacion.fecha(),
              ar.edu.utn.dds.k3003.catedra.dtos.logistica.EstadoAsginacionEnum.valueOf(asignacion.estado().name())
      ));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
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
