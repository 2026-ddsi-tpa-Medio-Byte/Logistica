package ar.edu.utn.dds.k3003.zAlumno.Interface;

//creo que estos imports no se pueden usar
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.LogisticaDTOs;

import java.util.List;

public interface Logistica_Interface {

     LogisticaDTOs.DepositoDTO buscarDepositoIDDTO(String depositoid);
     LogisticaDTOs.Deposito buscarDepositoID(String depositoid);
     LogisticaDTOs.DepositoDTO agregarDeposito(LogisticaDTOs.DepositoDTO deposito);
     LogisticaDTOs.AsignacionDTO buscarAsignacionPorPaqueteIDDTO(String paqueteid);
     LogisticaDTOs.Asignacion buscarAsignacionPorPaqueteID(String paqueteid);
     void eliminarDeposito(String depositoid);
     LogisticaDTOs.DepositoDTO modificarDeposito(String id, LogisticaDTOs.DepositoDTO nuevosDatos);
     DonacionYEntiDTOs.NecesidadMaterialDTO buscarNecesidadPorIDDTO(String necesidadid);
     DonacionYEntiDTOs.NecesidadDeMaterial buscarNecesidadPorID(String necesidadid);

     LogisticaDTOs.DepositoDTO gestionarDonacion(String depositoid,String donacionid, String productoid, Integer cantidad);
     void setAlgoritmoMM(String depositoid, LogisticaDTOs.TipoAlgoritmoEnum algoritmo);
     LogisticaDTOs.AsignacionDTO ejecutarMatchmaking(String depositoid, LogisticaDTOs.PaqueteDTO paquete, List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO);
     void reportarEntrega(LogisticaDTOs.PaqueteDTO paquete);
     List<LogisticaDTOs.DepositoDTO> obtenerTodosDepositosDTO();

     //LogisticaDTOs.AsignacionDTO buscarAsignacionPorPaqueteID(String paqueteid);
     //void reportarEntrega(LogisticaDTOs.PaqueteDTO paquete);
     //void setAlgoritmoMM(String depositoid, LogisticaDTOs.TipoAlgoritmoEnum algoritmo);


}


