package ar.edu.utn.dds.k3003.zAlumno.workers;

import ar.edu.utn.dds.k3003.zAlumno.config.RabbitMQConfig;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.DonacionMensaje;
import ar.edu.utn.dds.k3003.zAlumno.logging.InstanceInfo;
import ar.edu.utn.dds.k3003.zAlumno.logging.TraceIdFilter;
import ar.edu.utn.dds.k3003.zAlumno.services.LogisticaService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class DonacionWorker {

    private static final Logger log = LoggerFactory.getLogger(DonacionWorker.class);

    private final LogisticaService logisticaService;
    private final InstanceInfo instanceInfo;

    public DonacionWorker(LogisticaService logisticaService, InstanceInfo instanceInfo) {
        this.logisticaService = logisticaService;
        this.instanceInfo = instanceInfo;
    }

    @RabbitListener(queues = RabbitMQConfig.COLA_DONACIONES)
    public void procesarDonacion(
            DonacionMensaje mensaje,
            @Header(name = TraceIdFilter.TRACE_ID_HEADER, required = false) String traceId) {

        // Se recupera el traceId que venia en el request original. Si no vino
        // (mensaje viejo o publicado por otro lado), se genera uno nuevo.
        MDC.put(TraceIdFilter.MDC_TRACE_ID,
                traceId != null && !traceId.isBlank() ? traceId : UUID.randomUUID().toString().substring(0, 8));
        MDC.put("instanceId", instanceInfo.getInstanceId());

        try {
            log.info("[WORKER] Recibida donacion {} (deposito={}, producto={}, cantidad={})",
                    mensaje.donacionid(), mensaje.depositoid(), mensaje.productoid(), mensaje.cantidad());

            logisticaService.procesarDonacionDesdeCola(
                    mensaje.depositoid(),
                    mensaje.donacionid(),
                    mensaje.productoid(),
                    mensaje.cantidad());

            log.info("[WORKER] Donacion {} procesada", mensaje.donacionid());
        } catch (Exception e) {
            log.error("[WORKER] Error procesando donacion {}", mensaje.donacionid(), e);
            // Se relanza: Spring reintenta segun spring.rabbitmq.listener.simple.retry.*
            // y, agotados los intentos, el mensaje va a la DLQ en vez de perderse.
            throw (e instanceof RuntimeException re) ? re : new RuntimeException(e);
        } finally {
            MDC.clear();
        }
    }
}