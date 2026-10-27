package ar.edu.utn.dds.k3003.zAlumno.eventos;

// Contrato de los eventos que Logistica publica para el modelo de lectura (CQRS).
// Las fechas viajan como String ISO-8601 para que cualquier consumidor las lea sin
// depender de como Jackson serializa los tipos de java.time.
public class EventosLogistica {

    public static final String MODULO = "logistica";

    public enum TipoEventoEnum {
        DEPOSITO_CREADO("logistica.deposito.creado"),
        DEPOSITO_MODIFICADO("logistica.deposito.modificado"),
        DEPOSITO_ELIMINADO("logistica.deposito.eliminado"),
        STOCK_ACTUALIZADO("logistica.stock.actualizado"),
        ASIGNACION_CREADA("logistica.asignacion.creada"),
        ASIGNACION_COMPLETADA("logistica.asignacion.completada"),
        MODULO_RESETEADO("logistica.modulo.reseteado");

        private final String routingKey;

        TipoEventoEnum(String routingKey) {
            this.routingKey = routingKey;
        }

        public String getRoutingKey() {
            return routingKey;
        }
    }

    // Sobre comun a todos los eventos.
    // - eventoId: unico por evento, sirve para descartar repetidos.
    // - entidadId: id del documento a actualizar (upsert por este id).
    // - ocurridoEn: momento del cambio; el consumidor ignora eventos mas viejos que lo guardado.
    public record EventoDTO(
            String eventoId,
            TipoEventoEnum tipo,
            String modulo,
            String entidadId,
            String ocurridoEn,
            Object datos
    ) {}

    // Foto completa del deposito despues del cambio (en DEPOSITO_ELIMINADO es la ultima foto).
    public record DepositoEventoDTO(
            String depositoid,
            String nombre,
            String direccion,
            Integer capacidadMaxima,
            Integer stockActual,
            String algoritmo
    ) {}

    // Cantidad ABSOLUTA de un producto en un deposito (no el delta), para que aplicar
    // el mismo evento dos veces de el mismo resultado.
    public record StockEventoDTO(
            String depositoid,
            String productoid,
            Integer cantidad,
            Integer stockActualDeposito,
            Integer capacidadMaximaDeposito
    ) {}

    // Foto completa de la asignacion despues del cambio.
    public record AsignacionEventoDTO(
            String asignacionid,
            String paqueteid,
            String necesidadid,
            String donacionid,
            String productoid,
            Integer cantidad,
            String depositoid,
            String estado,
            String origen,
            String fecha,
            String fechaEntrega
    ) {}
}
