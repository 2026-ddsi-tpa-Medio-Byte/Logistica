package ar.edu.utn.dds.k3003.zAlumno.eventos;

import ar.edu.utn.dds.k3003.zAlumno.config.RabbitMQConfig;
import ar.edu.utn.dds.k3003.zAlumno.logging.TraceIdFilter;
import ar.edu.utn.dds.k3003.zAlumno.services.MetricasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// Manda a RabbitMQ los eventos que armo PublicadorEventos.
// AFTER_COMMIT: solo se publica si la transaccion del comando termino bien.
// fallbackExecution: si el metodo que lo genero no era transaccional, se publica igual (en el momento).
@Component
public class EnvioEventosListener {

    private static final Logger log = LoggerFactory.getLogger(EnvioEventosListener.class);

    private final RabbitTemplate rabbitTemplate;
    private final MetricasService metricasService;

    public EnvioEventosListener(RabbitTemplate rabbitTemplate, MetricasService metricasService) {
        this.rabbitTemplate = rabbitTemplate;
        this.metricasService = metricasService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void enviar(EventosLogistica.EventoDTO evento) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_EVENTOS,
                    evento.tipo().getRoutingKey(),
                    evento,
                    m -> {
                        String traceId = MDC.get(TraceIdFilter.MDC_TRACE_ID);
                        if (traceId != null) {
                            m.getMessageProperties().setHeader(TraceIdFilter.TRACE_ID_HEADER, traceId);
                        }
                        return m;
                    });
            metricasService.incrementarEventoPublicado(evento.tipo().name(), "ok");
            log.info("[EVENTOS] {} publicado (entidad={}, eventoId={})",
                    evento.tipo(), evento.entidadId(), evento.eventoId());
        } catch (Exception e) {
            // El comando ya se commiteo: no se puede deshacer. Se registra para que se vea
            // en Datadog (y pueda disparar una alarma) en vez de romper la respuesta al cliente.
            metricasService.incrementarEventoPublicado(evento.tipo().name(), "error");
            log.error("[EVENTOS] No se pudo publicar {} (entidad={}, eventoId={})",
                    evento.tipo(), evento.entidadId(), evento.eventoId(), e);
        }
    }
}
