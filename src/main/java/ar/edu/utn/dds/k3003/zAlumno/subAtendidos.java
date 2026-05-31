package ar.edu.utn.dds.k3003.zAlumno;

import ar.edu.utn.dds.k3003.zAlumno.Interface.Algoritmos_Interface;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.LogisticaDTOs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class subAtendidos implements Algoritmos_Interface {

    @Override
    public LogisticaDTOs.AsignacionDTO ejecutarAlgoritmo(String depositoid, LogisticaDTOs.PaqueteDTO paquete, List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO){

    List<DonacionYEntiDTOs.NecesidadDeMaterial> listaNecesidadMaterial = new ArrayList<>(); //a revisar
    DonacionYEntiDTOs.NecesidadDeMaterial entidadElegida = null;
    double menorPorcentaje = 101.0;

        for(DonacionYEntiDTOs.NecesidadDeMaterial necesidad : listaNecesidadMaterial){
            if(necesidad.getproductoSolicitadoid().equals(paquete.productoid())){
                double porcentajeActual = (necesidad.getcantidadActual() * 100.0) / necesidad.getcantidadObjetivo();
                if(porcentajeActual < menorPorcentaje){
                    menorPorcentaje = porcentajeActual;
                    entidadElegida = necesidad;//problema
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
