package ar.edu.utn.dds.k3003.zAlumno.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MetricasService {

  private final Counter asignacionesCreadas;
  private final Counter asignacionesErrores;
  private final Counter depositosConsultados;
  private final Counter entregasReportadas;
  private final Counter donacionesEncoladas;
  private final Counter asignacionesDuplicadas;
  private final Counter necesidadesSatisfechas;
  private final MeterRegistry meterRegistry;

  public MetricasService(MeterRegistry meterRegistry) {

    this.meterRegistry = meterRegistry;

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

}
