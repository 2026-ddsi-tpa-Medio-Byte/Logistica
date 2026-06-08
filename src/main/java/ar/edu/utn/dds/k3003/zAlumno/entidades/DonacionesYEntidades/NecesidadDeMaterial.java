package ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades;

import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.DonacionYEntiDTOs.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.DonacionYEntiDTOs.TipoNecesidadMaterialEnum;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;
import jakarta.persistence.*;

@Entity
@Table(name = "necesidaddematerial")
public class NecesidadDeMaterial{

    @Id
    private String necesidadid;
    private String entidadid;
    private Integer nivelDeUrgencia;
    private String descripcion;
    private Integer cantidadObjetivo;
    private String productoSolicitadoid;
    private Integer cantidadActual;
    @Enumerated(EnumType.STRING)
    private TipoNecesidadMaterialEnum tipo;

    public NecesidadDeMaterial() {
    }

    public NecesidadDeMaterial(NecesidadMaterialDTO dto) {
        this.necesidadid = dto.necesidadid();
        this.entidadid = dto.entidadid();
        this.nivelDeUrgencia = dto.nivelDeUrgencia();
        this.descripcion = dto.descripcion();
        this.cantidadObjetivo = dto.cantidadObjetivo();
        this.productoSolicitadoid = dto.productoSolicitadoid();
        this.cantidadActual = 0;
        this.tipo = dto.tipo();
    }

    public void setcantidadActual(Integer nuevacantidad) { this.cantidadActual = nuevacantidad; }
    public String getId() { return necesidadid; }
    public String getEntidadid() { return entidadid; }
    public Integer getNivelDeUrgencia() { return nivelDeUrgencia; }
    public String getDescripcion() { return descripcion; }
    public Integer getcantidadObjetivo() { return cantidadObjetivo; }
    public Integer getcantidadActual() { return cantidadActual; }
    public String getproductoSolicitadoid() { return productoSolicitadoid; }
    public TipoNecesidadMaterialEnum getTipo() { return tipo; }
}