package ar.edu.utn.dds.k3003.zAlumno.entidades;

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

    public static class NecesidadDeMaterial{
        String necesidadid;
        String entidadid;
        Integer nivelDeUrgencia;
        String descripcion;
        Integer cantidadObjetivo;
        String productoSolicitadoid;
        Integer cantidadActual;
        TipoNecesidadMaterialEnum tipo;

        public NecesidadDeMaterial(NecesidadMaterialDTO dto) {
            this.necesidadid = dto.necesidadid();
            this.entidadid = dto.entidadid();
            this.nivelDeUrgencia = dto.nivelDeUrgencia();
            this.descripcion = dto.descripcion();
            this.cantidadObjetivo = dto.cantidadObjetivo();
            this.productoSolicitadoid = dto.productoSolicitadoid();
            this.cantidadActual = 0;
            TipoNecesidadMaterialEnum tipo;
        }

        public String getId() {return necesidadid; }
        public String getproductoSolicitadoid() { return productoSolicitadoid; }
        public Integer getcantidadObjetivo() { return cantidadObjetivo; }
        public Integer getcantidadActual() { return cantidadActual; }
        public Integer getNivelDeUrgencia() { return nivelDeUrgencia; }
    }

    public record EntidadBeneficaDTO(
            String id,
            String razonSocial,
            String domicilio,
            String telefono,
            String correo
    ) {}
}
