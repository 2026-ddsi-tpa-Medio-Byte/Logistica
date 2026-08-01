package ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NecesidadResponseDTO(
        String id,
        String entidadID,
        Integer nivelDeUrgencia,
        String descripcion,
        Integer cantidadObjetivo,
        Integer cantidadActual,
        String productoSolicitadoID,
        String tipo
) {}