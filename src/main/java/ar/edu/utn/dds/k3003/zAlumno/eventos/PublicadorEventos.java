package ar.edu.utn.dds.k3003.zAlumno.eventos;

import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.Asignacion;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.Deposito;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.StockDeposito;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

// Arma los eventos de Logistica y los deja en el bus interno de Spring.
// NO los manda a RabbitMQ directamente: eso lo hace EnvioEventosListener recien
// cuando la transaccion commitea, para no avisar cambios que despues se deshicieron.
@Component
public class PublicadorEventos {

    private final ApplicationEventPublisher applicationEventPublisher;

    public PublicadorEventos(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void depositoCreado(Deposito deposito) {
        publicar(EventosLogistica.TipoEventoEnum.DEPOSITO_CREADO, deposito.getId(), aDepositoEvento(deposito));
    }

    public void depositoModificado(Deposito deposito) {
        publicar(EventosLogistica.TipoEventoEnum.DEPOSITO_MODIFICADO, deposito.getId(), aDepositoEvento(deposito));
    }

    public void depositoEliminado(Deposito deposito) {
        publicar(EventosLogistica.TipoEventoEnum.DEPOSITO_ELIMINADO, deposito.getId(), aDepositoEvento(deposito));
    }

    public void stockActualizado(StockDeposito stock, Deposito deposito) {
        EventosLogistica.StockEventoDTO datos = new EventosLogistica.StockEventoDTO(
                stock.getDepositoid(),
                stock.getProductoid(),
                stock.getCantidad(),
                deposito != null ? deposito.getStockActual() : null,
                deposito != null ? deposito.getCapacidadMaxima() : null);
        // un documento de stock por par deposito-producto, con id "DEP-003:P-3" (formato acordado con el grupo)
        publicar(EventosLogistica.TipoEventoEnum.STOCK_ACTUALIZADO,
                stock.getDepositoid() + ":" + stock.getProductoid(), datos);
    }

    // Se vacio la base de Logistica: el modelo de lectura borra todo lo que vino de este modulo.
    // Formato acordado con el grupo: entidadId "*" y datos vacios.
    public void moduloReseteado() {
        publicar(EventosLogistica.TipoEventoEnum.MODULO_RESETEADO, "*", Map.of());
    }

    public void asignacionCreada(Asignacion asignacion) {
        publicar(EventosLogistica.TipoEventoEnum.ASIGNACION_CREADA, asignacion.getId(), aAsignacionEvento(asignacion));
    }

    public void asignacionCompletada(Asignacion asignacion) {
        publicar(EventosLogistica.TipoEventoEnum.ASIGNACION_COMPLETADA, asignacion.getId(), aAsignacionEvento(asignacion));
    }

    private void publicar(EventosLogistica.TipoEventoEnum tipo, String entidadId, Object datos) {
        applicationEventPublisher.publishEvent(new EventosLogistica.EventoDTO(
                UUID.randomUUID().toString(),
                tipo,
                EventosLogistica.MODULO,
                entidadId,
                Instant.now().toString(),
                datos));
    }

    private static EventosLogistica.DepositoEventoDTO aDepositoEvento(Deposito d) {
        return new EventosLogistica.DepositoEventoDTO(
                d.getId(),
                d.getNombre(),
                d.getDireccion(),
                d.getCapacidadMaxima(),
                d.getStockActual(),
                d.getAlgoritmo() != null ? d.getAlgoritmo().name() : null);
    }

    private static EventosLogistica.AsignacionEventoDTO aAsignacionEvento(Asignacion a) {
        return new EventosLogistica.AsignacionEventoDTO(
                a.getId(),
                a.getpaqueteId(),
                a.getNecesidadId(),
                a.getDonacionid(),
                a.getProductoid(),
                a.getCantidad(),
                a.getDepositoid(),
                a.getEstado() != null ? a.getEstado().name() : null,
                a.getOrigen() != null ? a.getOrigen().name() : null,
                aTexto(a.getfecha()),
                aTexto(a.getFechaEntrega()));
    }

    // Las fechas de la asignacion se guardan como LocalDateTime (sin zona) con LocalDateTime.now(),
    // o sea en la zona de la JVM. Se pasan a UTC con "Z" para que el Servicio de Consultas pueda
    // compararlas con las de otros modulos (consulta 3: tiempo ingreso-entrega) sin importar
    // en que zona corre cada servicio.
    private static String aTexto(LocalDateTime fecha) {
        return fecha != null ? fecha.atZone(ZoneId.systemDefault()).toInstant().toString() : null;
    }
}
