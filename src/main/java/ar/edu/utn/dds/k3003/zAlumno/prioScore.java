package ar.edu.utn.dds.k3003.zAlumno;

import ar.edu.utn.dds.k3003.zAlumno.Interface.Algoritmos_Interface;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.NecesidadDeMaterial;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.DonacionesYEntidades.NecesidadDeMaterialRepository;

import java.time.LocalDateTime;
import java.util.List;

public class prioScore implements Algoritmos_Interface {

    @Override
    public LogisticaDTOs.AsignacionDTO ejecutarAlgoritmo(
            String depositoid,
            LogisticaDTOs.PaqueteDTO paquete,
            List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO) {

        DonacionYEntiDTOs.NecesidadMaterialDTO dtoElegido = null;
        double mayorScore = -1;

        for (DonacionYEntiDTOs.NecesidadMaterialDTO dto : listaNecesidadMaterialDTO) {
            double progreso = (dto.cantidadActual() == 0) ? 0.0001 : ((double) dto.cantidadActual() / dto.cantidadObjetivo());
            double scoreActual = dto.nivelDeUrgencia() / progreso;
            if (scoreActual > mayorScore) {
                mayorScore = scoreActual;
                dtoElegido = dto;
            }
        }

        if (dtoElegido == null) return null;

        return new LogisticaDTOs.AsignacionDTO(
                java.util.UUID.randomUUID().toString(),
                paquete.paqueteid(),
                dtoElegido.necesidadid(),
                LocalDateTime.now(),
                LogisticaDTOs.EstadoAsginacionEnum.ASIGNADA,
                LogisticaDTOs.OrigenAsignacionEnum.MATCHMAKING,
                paquete.donacionID(),
                paquete.productoid(),
                paquete.cantidad()
        );
    }
}
