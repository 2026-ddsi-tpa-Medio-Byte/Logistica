package ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica;

import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs.AsignacionDTO;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs.EstadoAsginacionEnum;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    private String asignacionid;
    private String paqueteid;
    private String necesidadid;
    private LocalDateTime fecha;
    @Enumerated(EnumType.STRING)
    private EstadoAsginacionEnum estado;
    private LogisticaDTOs.OrigenAsignacionEnum origen;

    public Asignacion() {
    }

    public Asignacion(AsignacionDTO dto) {
        this.asignacionid = dto.asignacionid();
        this.paqueteid = dto.paqueteid();
        this.necesidadid = dto.necesidadid();
        this.fecha = dto.fecha();
        this.estado = dto.estado();
        this.origen = dto.origen();
    }

    public void setEstado(EstadoAsginacionEnum nuevoestado) { this.estado = nuevoestado; }
    public String getId() { return asignacionid; }
    public String getpaqueteId() { return paqueteid; }
    public String getNecesidadId() { return necesidadid; }
    public LocalDateTime getfecha() { return fecha; }
    public EstadoAsginacionEnum getEstado() { return estado; }
    public LogisticaDTOs.OrigenAsignacionEnum getOrigen() { return origen; }
}