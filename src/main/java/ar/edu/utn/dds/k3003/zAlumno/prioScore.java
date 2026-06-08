package ar.edu.utn.dds.k3003.zAlumno;

import ar.edu.utn.dds.k3003.zAlumno.Interface.Algoritmos_Interface;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.NecesidadDeMaterial;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.DonacionesYEntidades.NecesidadDeMaterialRepository;

import java.time.LocalDateTime;
import java.util.List;

public class prioScore implements Algoritmos_Interface {

    private final NecesidadDeMaterialRepository necesidadRepository;

    public prioScore(NecesidadDeMaterialRepository necesidadRepository) {
        this.necesidadRepository = necesidadRepository;
    }

    @Override
    public LogisticaDTOs.AsignacionDTO ejecutarAlgoritmo(String depositoid, LogisticaDTOs.PaqueteDTO paquete, List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO) {

        DonacionYEntiDTOs.NecesidadMaterialDTO dtoElegido = null;
        double mayorScore = -1;

        for (DonacionYEntiDTOs.NecesidadMaterialDTO dto : listaNecesidadMaterialDTO) {

            NecesidadDeMaterial entidad = necesidadRepository.findById(dto.necesidadid()).orElse(null);

            if (entidad != null) {
                double cantidadActual = entidad.getcantidadActual();
                double cantidadObjetivo = entidad.getcantidadObjetivo();

                double progreso = (cantidadActual == 0) ? 0.0001 : (cantidadActual / cantidadObjetivo);
                double scoreActual = entidad.getNivelDeUrgencia() / progreso;

                if (scoreActual > mayorScore) {
                    mayorScore = scoreActual;
                    dtoElegido = dto;
                }
            }
        }

        if (dtoElegido == null) {
            return null;
        }

        // 3. Retornamos la asignación mapeando el ID de la necesidad ganadora
        return new LogisticaDTOs.AsignacionDTO(
                java.util.UUID.randomUUID().toString(),
                paquete.paqueteid(),
                dtoElegido.necesidadid(),
                LocalDateTime.now(),
                LogisticaDTOs.EstadoAsginacionEnum.ASIGNADA
        );
    }
}
