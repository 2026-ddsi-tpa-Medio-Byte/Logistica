package ar.edu.utn.dds.k3003.zAlumno.entidades;

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

    public record DepositoDTO(
            String nombre,
            String depositoid,
            String direccion,
            Integer capacidadMaxima,
            Integer stockActual,
            TipoAlgoritmoEnum algoritmo
    ) { }

    public static class Deposito{
        String depositoid;
        String nombre;
        String direccion;
        Integer capacidadMaxima;
        Integer stockActual;
        TipoAlgoritmoEnum algoritmo;

        public Deposito(DepositoDTO dto) {
            this.depositoid = dto.depositoid();
            this.nombre = dto.nombre();
            this.direccion = dto.direccion();
            this.capacidadMaxima = dto.capacidadMaxima();
            this.stockActual = dto.stockActual();
            this.algoritmo = dto.algoritmo();
        }
        public void setAlgoritmo(TipoAlgoritmoEnum algoritmo) {this.algoritmo = algoritmo; }
        public TipoAlgoritmoEnum getAlgoritmo() {return algoritmo;};
        public String getId() {return depositoid; }
        //public String getproductoSolicitadoid() { return productoSolicitadoid; }
        //public Integer getcantidadObjetivo() { return cantidadObjetivo; }
        //public Integer getcantidadActual() { return cantidadActual; }
    }

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
            EstadoAsginacionEnum estado
    ){}

    public static class Asignacion{
        String asignacionid;
        String paqueteid;
        String necesidadid;
        LocalDateTime fecha;
        EstadoAsginacionEnum estado;

        public Asignacion(AsignacionDTO dto) {
            this.asignacionid = dto.asignacionid();
            this.paqueteid = dto.paqueteid();
            this.necesidadid = dto.necesidadid();
            this.fecha = dto.fecha();
            this.estado = dto.estado();
        }

        public void setEstado(EstadoAsginacionEnum nuevoestado){
            this.estado = nuevoestado;
        }
        public String getId() {return asignacionid; }
        public String getpaqueteId() {return paqueteid; }
        public String getNecesidadId() {return necesidadid; }
        public LocalDateTime getfecha() {return fecha; }
        public EstadoAsginacionEnum getEstado() {return estado; }

    }

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
