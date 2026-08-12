package ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica;

import java.time.LocalDateTime;

public class LogisticaDTOs {

    public enum EstadoAsginacionEnum {
        ASIGNADA,
        COMPLETADA
    }

    public enum TipoAlgoritmoEnum{
        NULL,
        SUBATENDIDOS,
        PRIOSCORE
    }

    public enum OrigenAsignacionEnum {
        MATCHMAKING,
        SOLICITUD_DONADORES
    }

    public record DepositoDTO(
            String nombre,
            String depositoid,
            String direccion,
            Integer capacidadMaxima,
            Integer stockActual,
            TipoAlgoritmoEnum algoritmo
    ) { }

    public record PaqueteDTO(
            String paqueteid,
            String donacionID,
            String productoid,
            Integer cantidad
    ){}

    public record AsignacionDTO(
            String asignacionid,
            String paqueteid,
            String necesidadid,
            LocalDateTime fecha,
            EstadoAsginacionEnum estado,
            OrigenAsignacionEnum origen
    ){}

    public record GestionDonacionResponseDTO(
            String mensaje,
            DepositoDTO deposito,
            AsignacionDTO asignacion
    ) {}

    public record ReporteEntregaResponseDTO(
            String mensajeDonacion,
            String donacionId,
            String mensajeAsignacion,
            String asignacionId
    ) {}

    public record SolicitudAsignacionDTO(
            String necesidadID,
            String productoID,
            Integer cantidad,
            String origen
    ) {}

    //SWAGGER

    public record DepositoBase(
            String nombre,
            String direccion,
            Integer capacidadMaxima
    ){}

    public record DepositoRequest(
            String nombre,
            String direccion,
            Integer capacidadMaxima
    ){}

    public record DepositoResponse(
            String nombre,
            String direccion,
            Integer capacidadMaxima,
            String id,
            PaqueteDTO stockActual
    ){}

    public record GestionDonacionDTO(
         String depositoID,
         String donacionID,
         String productoID,
         Integer cantidad
    ){}
}
