package ar.edu.utn.dds.k3003.zAlumno.controllers;

import ar.edu.utn.dds.k3003.zAlumno.config.RabbitMQConfig;
import ar.edu.utn.dds.k3003.zAlumno.services.LogisticaService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// Endpoints de operacion del modulo (no de negocio). Ruta acordada con el grupo:
// por el gateway queda como /logistica/admin/republicar-eventos.
@RestController
public class AdminController {

  private static final Logger log = LoggerFactory.getLogger(AdminController.class);

  private final LogisticaService logisticaService;
  private final AmqpAdmin amqpAdmin;

  @Autowired
  public AdminController(LogisticaService logisticaService, AmqpAdmin amqpAdmin) {
    this.logisticaService = logisticaService;
    this.amqpAdmin = amqpAdmin;
  }

  @Operation(summary = "Vuelve a publicar todo el estado actual de Logística (depósitos, stock y asignaciones) "
          + "para reconstruir el modelo de lectura. No modifica datos.")
  @PostMapping("/admin/republicar-eventos")
  public ResponseEntity<Map<String, Integer>> republicarEventos() {
    return ResponseEntity.ok(logisticaService.republicarEventos());
  }

  // Despues de revisar una donacion que fallo todos los reintentos (y de mostrar la alarma "DLQ"),
  // se vacia la DLQ para que el monitor vuelva a OK. Los mensajes descartados no se reprocesan.
  @Operation(summary = "Vacía la cola de donaciones fallidas (DLQ). Los mensajes se descartan.")
  @DeleteMapping("/admin/dlq")
  public ResponseEntity<Map<String, Integer>> vaciarDlq() {
    // purgeQueue(nombre) espera la respuesta del broker y devuelve cuantos mensajes borro
    // (la version con noWait devuelve void)
    int descartados = amqpAdmin.purgeQueue(RabbitMQConfig.COLA_DONACIONES_DLQ);
    log.warn("DLQ vaciada: {} mensajes descartados", descartados);
    return ResponseEntity.ok(Map.of("descartados", descartados));
  }
}
