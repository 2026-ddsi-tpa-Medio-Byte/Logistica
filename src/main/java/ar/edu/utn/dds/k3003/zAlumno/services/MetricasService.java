package ar.edu.utn.dds.k3003.zAlumno.services;

import ar.edu.utn.dds.k3003.zAlumno.config.RabbitMQConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class MetricasService {

  private static final Logger log = LoggerFactory.getLogger(MetricasService.class);

  private final Counter asignacionesCreadas;
  private final Counter asignacionesErrores;
  private final Counter depositosConsultados;
  private final Counter entregasReportadas;
  private final Counter donacionesEncoladas;
  private final Counter asignacionesDuplicadas;
  private final Counter necesidadesSatisfechas;
  private final MeterRegistry meterRegistry;
  private final AmqpAdmin amqpAdmin;

  public MetricasService(MeterRegistry meterRegistry,
                         @Lazy LogisticaService logisticaService,
                         AmqpAdmin amqpAdmin) {

    this.meterRegistry = meterRegistry;
    this.amqpAdmin = amqpAdmin;

    this.asignacionesCreadas =
            Counter.builder("logistica.asignaciones.creadas")
                    .description("Cantidad de asignaciones creadas exitosamente")
                    .tag("modulo", "logistica")
                    .register(meterRegistry);

    this.asignacionesErrores =
            Counter.builder("logistica.asignaciones.errores")
                    .description("Cantidad de errores al crear asignaciones")
                    .tag("modulo", "logistica")
                    .register(meterRegistry);

    this.depositosConsultados =
            Counter.builder("logistica.depositos.consultas")
                    .description("Cantidad de consultas a depósitos")
                    .tag("modulo", "logistica")
                    .register(meterRegistry);

    this.entregasReportadas =
            Counter.builder("logistica.entregas.reportadas")
                    .description("Cantidad de entregas reportadas como completadas")
                    .tag("modulo", "logistica")
                    .register(meterRegistry);

    this.donacionesEncoladas =
            Counter.builder("logistica.donaciones.encoladas")
                    .description("Donaciones que ingresaron a la cola de trabajo")
                    .tag("modulo", "logistica")
                    .register(meterRegistry);

    this.asignacionesDuplicadas =
            Counter.builder("logistica.asignaciones.duplicadas")
                    .description("Mensajes de donación ignorados por asignación ya existente")
                    .tag("modulo", "logistica")
                    .register(meterRegistry);

    this.necesidadesSatisfechas =
            Counter.builder("logistica.necesidades.satisfechas")
                    .description("Satisfacciones de necesidad disparadas al reportar entrega")
                    .tag("modulo", "logistica")
                    .register(meterRegistry);

    //GAUGES estado actual

    //Stock total disponible. Lee en vivo del LogisticaService.
    Gauge.builder("logistica.stock.actual", () -> logisticaService.stockTotalActual())
            .description("Unidades totales en stock en este momento (todos los depósitos)")
            .tag("modulo", "logistica")
            .register(meterRegistry);

    //Ocupación promedio de los depósitos AHORA (0 a 100)
    Gauge.builder("logistica.deposito.ocupacion", () -> logisticaService.ocupacionPromedio())
            .description("Porcentaje de ocupación promedio de los depósitos")
            .tag("modulo", "logistica")
            .register(meterRegistry);

    //Donaciones pendientes en la cola (leido del broker, igual para todas las instancias)
    Gauge.builder("logistica.cola.pendientes", () -> this.mensajesEnCola(RabbitMQConfig.COLA_DONACIONES))
            .description("Donaciones esperando ser procesadas por los workers")
            .tag("modulo", "logistica")
            .register(meterRegistry);

    //Donaciones que fallaron todos los reintentos y quedaron en la DLQ
    Gauge.builder("logistica.cola.dlq", () -> this.mensajesEnCola(RabbitMQConfig.COLA_DONACIONES_DLQ))
            .description("Donaciones que fallaron todos los reintentos")
            .tag("modulo", "logistica")
            .register(meterRegistry);
  }

  public void incrementarAsignacionesCreadas() {
    asignacionesCreadas.increment();
  }

  public void incrementarAsignacionesErrores() {
    asignacionesErrores.increment();
  }

  public void incrementarDepositosConsultados() {
    depositosConsultados.increment();
  }

  public void incrementarEntregasReportadas() {
    entregasReportadas.increment();
  }

  public void incrementarDonacionesEncoladas() {
    donacionesEncoladas.increment();
  }

  public void incrementarAsignacionesDuplicadas() {
    asignacionesDuplicadas.increment();
  }

  public void incrementarNecesidadesSatisfechas() {
    necesidadesSatisfechas.increment();
  }

  public void incrementarStockMovimiento(String tipo) {
    Counter.builder("logistica.stock.movimientos")
            .description("Altas y bajas de stock")
            .tag("modulo", "logistica")
            .tag("tipo", tipo)
            .register(meterRegistry)
            .increment();
  }

  public void incrementarEntregaRechazada(String motivo) {
    Counter.builder("logistica.entregas.rechazadas")
            .description("Reportes de entrega rechazados por validacion")
            .tag("modulo", "logistica")
            .tag("motivo", motivo)
            .register(meterRegistry)
            .increment();
  }

  /* Donación procesada por el worker. resultado = "asignada" | "stock" | "descartada". */
  public void incrementarDonacionGestionada(String resultado) {
    Counter.builder("logistica.donaciones.gestionadas")
            .description("Donaciones procesadas por el worker, por resultado")
            .tag("modulo", "logistica")
            .tag("resultado", resultado)
            .register(meterRegistry)
            .increment();
  }

  /* Solicitud directa de stock desde Donadores. resultado = "ok" | "sin_stock" | "error". */
  public void incrementarSolicitudDirecta(String resultado) {
    Counter.builder("logistica.solicitudes.directas")
            .description("Solicitudes de stock desde el módulo Donadores, por resultado")
            .tag("modulo", "logistica")
            .tag("resultado", resultado)
            .register(meterRegistry)
            .increment();
  }

  /* Unidades que no entraron al depósito (lleno o inexistente) y se descartaron. */
  public void incrementarUnidadesDescartadas(String depositoId, int cantidad) {
    if (cantidad <= 0) {
      return;
    }
    Counter.builder("logistica.stock.descartadas")
            .description("Unidades descartadas por falta de espacio en el depósito")
            .tag("modulo", "logistica")
            .tag("deposito", depositoId != null ? depositoId : "desconocido")
            .register(meterRegistry)
            .increment(cantidad);
  }

  /* Fallo al llamar a otro módulo. modulo = "donaciones" | "donadoresyentidades". */
  public void incrementarFalloIntegracion(String modulo, String operacion) {
    Counter.builder("logistica.integracion.fallos")
            .description("Llamadas fallidas a otros módulos")
            .tag("modulo", "logistica")
            .tag("destino", modulo)
            .tag("operacion", operacion)
            .register(meterRegistry)
            .increment();
  }

  // Fuente de los gauges de cola: se le pregunta al broker cuántos mensajes hay.
  // Devuelve NaN si no se puede consultar, para que Datadog no lo tome como un 0 real.
  public double mensajesEnCola(String cola) {
    try {
      QueueInformation info = amqpAdmin.getQueueInfo(cola);
      return info != null ? info.getMessageCount() : Double.NaN;
    } catch (Exception e) {
      log.debug("No se pudo leer el tamaño de la cola {}: {}", cola, e.getMessage());
      return Double.NaN;
    }
  }
}