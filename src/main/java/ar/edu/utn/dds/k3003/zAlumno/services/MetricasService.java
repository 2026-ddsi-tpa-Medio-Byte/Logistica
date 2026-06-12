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

  public MetricasService(MeterRegistry meterRegistry) {
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
}
