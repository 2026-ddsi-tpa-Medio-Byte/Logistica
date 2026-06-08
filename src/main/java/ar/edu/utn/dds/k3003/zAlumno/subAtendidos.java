package ar.edu.utn.dds.k3003.zAlumno;

import ar.edu.utn.dds.k3003.zAlumno.Interface.Algoritmos_Interface;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.NecesidadDeMaterial;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.DonacionesYEntidades.NecesidadDeMaterialRepository;

import java.time.LocalDateTime;
import java.util.List;

public class subAtendidos implements Algoritmos_Interface {

    private final NecesidadDeMaterialRepository necesidadRepository;

    public subAtendidos(NecesidadDeMaterialRepository necesidadRepository) {
        this.necesidadRepository = necesidadRepository;
    }

    @Override
    public LogisticaDTOs.AsignacionDTO ejecutarAlgoritmo(String depositoid, LogisticaDTOs.PaqueteDTO paquete, List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO) {

        DonacionYEntiDTOs.NecesidadMaterialDTO dtoElegido = null;
        double menorPorcentaje = 101.0;

        for (DonacionYEntiDTOs.NecesidadMaterialDTO dto : listaNecesidadMaterialDTO) {

            NecesidadDeMaterial necesidad = necesidadRepository.findById(dto.necesidadid()).orElse(null);

            if (necesidad != null) {
                double porcentajeActual = (necesidad.getcantidadActual() * 100.0) / necesidad.getcantidadObjetivo();

                if (porcentajeActual < menorPorcentaje) {
                    menorPorcentaje = porcentajeActual;
                    dtoElegido = dto;
                }
            }
        }

        if (dtoElegido == null) {
            return null;
        }

        return new LogisticaDTOs.AsignacionDTO(
                java.util.UUID.randomUUID().toString(),
                paquete.paqueteid(),
                dtoElegido.necesidadid(),
                LocalDateTime.now(),
                LogisticaDTOs.EstadoAsginacionEnum.ASIGNADA
        );
    }
}