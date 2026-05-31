package ar.edu.utn.dds.k3003.zAlumno.Interface;

import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.LogisticaDTOs;

import java.util.List;

public interface Algoritmos_Interface {

    LogisticaDTOs.AsignacionDTO ejecutarAlgoritmo(String depositoid, LogisticaDTOs.PaqueteDTO paquete, List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO);
}

