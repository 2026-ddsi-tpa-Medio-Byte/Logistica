package ar.edu.utn.dds.k3003.zAlumno.entidades.Donaciones;

import ar.edu.utn.dds.k3003.zAlumno.entidades.Donaciones.DonacionesDTOs.DonacionDTO;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Donaciones.DonacionesDTOs.EstadoDonacionEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "donaciones")
public class Donacion{

    @Id
    private String donacionid;
    private String donadorid;
    private String depositoid;
    private String descripcion;
    private String productoid;
    private Integer cantidad;
    @Enumerated(EnumType.STRING)
    private EstadoDonacionEnum estado;

    public Donacion() {
    }

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
