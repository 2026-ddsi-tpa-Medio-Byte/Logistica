package ar.edu.utn.dds.k3003.zAlumno.controllers;

import ar.edu.utn.dds.k3003.zAlumno.services.LogisticaService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// Endpoints de operacion del modulo (no de negocio). Ruta acordada con el grupo:
// por el gateway queda como /logistica/admin/republicar-eventos.
@RestController
public class AdminController {

  private final LogisticaService logisticaService;

  @Autowired
  public AdminController(LogisticaService logisticaService) {
    this.logisticaService = logisticaService;
  }

  @Operation(summary = "Vuelve a publicar todo el estado actual de Logística (depósitos, stock y asignaciones) "
          + "para reconstruir el modelo de lectura. No modifica datos.")
  @PostMapping("/admin/republicar-eventos")
  public ResponseEntity<Map<String, Integer>> republicarEventos() {
    return ResponseEntity.ok(logisticaService.republicarEventos());
  }
}
