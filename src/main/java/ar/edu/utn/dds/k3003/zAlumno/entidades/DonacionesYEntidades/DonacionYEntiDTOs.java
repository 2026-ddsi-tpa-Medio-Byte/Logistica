package ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades;

public class DonacionYEntiDTOs {

    public enum EstadoDonadorEnum {
        VERIFICADO,
        SOSPECHOSO,
        BANEADO
    }

    public enum TipoNecesidadMaterialEnum {
        RECURRENTE,
        EXTRAORDINARIA
    }

    public record DonadorDTO(
            String id,
            String nombre,
            String apellido,
            Integer edad,
            String email,
            Integer nroDocumento,
            EstadoDonadorEnum estado,
            String domicilio
    ) {}

    public record NecesidadMaterialDTO(
            String necesidadid,
            String entidadid,
            Integer nivelDeUrgencia,
            String descripcion,
            Integer cantidadObjetivo,
            String productoSolicitadoid,
            TipoNecesidadMaterialEnum tipo
    ) {}

    public record EntidadBeneficaDTO(
            String id,
            String razonSocial,
            String domicilio,
            String telefono,
            String correo
    ) {}
}
