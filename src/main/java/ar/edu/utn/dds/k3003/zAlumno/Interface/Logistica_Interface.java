package ar.edu.utn.dds.k3003.zAlumno.Interface;

//creo que estos imports no se pueden usar
import ar.edu.utn.dds.k3003.zAlumno.entidades.Donaciones.Donacion;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.NecesidadDeMaterial;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.Asignacion;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.Deposito;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;

import java.util.List;

public interface Logistica_Interface {

     LogisticaDTOs.DepositoDTO buscarDepositoIDDTO(String depositoid);
     Deposito buscarDepositoID(String depositoid);
     LogisticaDTOs.DepositoDTO agregarDeposito(LogisticaDTOs.DepositoDTO deposito);
     LogisticaDTOs.AsignacionDTO buscarAsignacionPorPaqueteIDDTO(String paqueteid);
     Asignacion buscarAsignacionPorPaqueteID(String paqueteid);
     void eliminarDeposito(String depositoid);
     LogisticaDTOs.DepositoDTO modificarDeposito(String id, LogisticaDTOs.DepositoDTO nuevosDatos);
     DonacionYEntiDTOs.NecesidadMaterialDTO buscarNecesidadPorIDDTO(String necesidadid);
     NecesidadDeMaterial buscarNecesidadPorID(String necesidadid);
     void agregarAlStock(String depositoId, Integer cantidad);

     LogisticaDTOs.GestionDonacionResponseDTO gestionarDonacion(String depositoid,String donacionid, String productoid, Integer cantidad);
     void setAlgoritmoMM(String depositoid, LogisticaDTOs.TipoAlgoritmoEnum algoritmo);
     LogisticaDTOs.AsignacionDTO ejecutarMatchmaking(String depositoid, LogisticaDTOs.PaqueteDTO paquete, List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO);
     //LogisticaDTOs.AsignacionDTO ejecutarMatchmaking(String depositoid, LogisticaDTOs.PaqueteDTO paquete, List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO);
     LogisticaDTOs.ReporteEntregaResponseDTO reportarEntrega(LogisticaDTOs.PaqueteDTO paquete);
     List<LogisticaDTOs.DepositoDTO> obtenerTodosDepositosDTO();

     //LogisticaDTOs.AsignacionDTO buscarAsignacionPorPaqueteID(String paqueteid);
     //void reportarEntrega(LogisticaDTOs.PaqueteDTO paquete);
     //void setAlgoritmoMM(String depositoid, LogisticaDTOs.TipoAlgoritmoEnum algoritmo);


}


