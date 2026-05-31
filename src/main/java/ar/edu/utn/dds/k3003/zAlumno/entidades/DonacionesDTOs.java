package ar.edu.utn.dds.k3003.zAlumno.entidades;

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

    public static class Donacion{
        String donacionid;
        String donadorid;
        String depositoid;
        String descripcion;
        String productoid;
        Integer cantidad;
        EstadoDonacionEnum estado;

        public Donacion(DonacionDTO dto) {
            this.donacionid = dto.donacionid();
            this.donadorid = dto.donadorid();
            this.depositoid = dto.depositoid();
            this.descripcion = dto.descripcion();
            this.productoid = dto.productoid();
            this.cantidad = dto.cantidad();
            this.estado = dto.estado();

        }
        public String getId() {return donacionid; }
        public String getDonadorId() {return donadorid; }
        public String getdepositoId() {return depositoid; }
        public String getDescripcion() {return descripcion; }
        public String getproductoId() {return productoid; }
        public Integer getCantidad() {return cantidad; }
        public EstadoDonacionEnum getEstado() {return estado; }

        public void setEstado(EstadoDonacionEnum nuevoestado){
            this.estado = nuevoestado;
        }
    }

    public record ProductoDTO (
        String productoid,
        String nombre,
        String descripcion,
        String categoriaid,
        String identificadorid
    ){}
}
