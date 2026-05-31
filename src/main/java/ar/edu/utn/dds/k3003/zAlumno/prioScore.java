package ar.edu.utn.dds.k3003.zAlumno;

import ar.edu.utn.dds.k3003.zAlumno.Interface.Algoritmos_Interface;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.LogisticaDTOs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class prioScore implements Algoritmos_Interface {
    @Override
    public LogisticaDTOs.AsignacionDTO ejecutarAlgoritmo(String depositoid, LogisticaDTOs.PaqueteDTO paquete, List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO){
        List<DonacionYEntiDTOs.NecesidadDeMaterial> listaNecesidadMaterial = new ArrayList<>(); //a revisar
        DonacionYEntiDTOs.NecesidadDeMaterial entidadElegida = null;
        double mayorScore = -1;

        for(DonacionYEntiDTOs.NecesidadDeMaterial entidad : listaNecesidadMaterial){
            if(entidad.getproductoSolicitadoid().equals(paquete.productoid())){
                double scoreActual = entidad.getNivelDeUrgencia() / (entidad.getcantidadActual()/entidad.getcantidadObjetivo() );
                if(scoreActual > mayorScore){
                    mayorScore = scoreActual;
                    entidadElegida = entidad;//problema
                }
            }
        }

        return new LogisticaDTOs.AsignacionDTO(
                java.util.UUID.randomUUID().toString(),
                paquete.paqueteid(),
                entidadElegida.getId(),
                LocalDateTime.now(),
                LogisticaDTOs.EstadoAsginacionEnum.ASIGNADA
        );
    }
}
