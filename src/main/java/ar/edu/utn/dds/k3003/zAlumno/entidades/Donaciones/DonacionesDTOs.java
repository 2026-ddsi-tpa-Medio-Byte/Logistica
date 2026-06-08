package ar.edu.utn.dds.k3003.zAlumno.entidades.Donaciones;

public class DonacionesDTOs {

    public enum EstadoDonacionEnum {
        INGRESADA,
        ACEPTADA,
        CONQUEJA
    }

    public record DonacionDTO(
            String donacionid,
            String donadorid,
            String depositoid,
            String descripcion,
            String productoid,
            Integer cantidad,
            EstadoDonacionEnum estado
    ){}

    public record ProductoDTO (
        String productoid,
        String nombre,
        String descripcion,
        String categoriaid,
        String identificadorid
    ){}
}
