package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.TipoNecesidadMaterialEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.*;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;

import java.time.LocalDateTime;
import java.util.*;

public class Fachada implements FachadaLogistica {

  private final Map<String, DepositoDTO>    depositos   = new LinkedHashMap<>();
  private final Map<String, AsignacionDTO>  asignaciones = new LinkedHashMap<>();

  private final Map<String, TipoAlgoritmoEnum> algoritmos = new HashMap<>();

  private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;
  private FachadaDonaciones          fachadaDonaciones;

  @Override
  public DepositoDTO agregarDeposito(DepositoDTO deposito) {
    if (deposito == null) {
      throw new RuntimeException("El depósito no puede ser nulo");
    }
    if (deposito.id() != null && depositos.containsKey(deposito.id())) {
      throw new RuntimeException("Ya existe un depósito con ese ID");
    }
    String id = deposito.id() != null ? deposito.id() : UUID.randomUUID().toString();
    DepositoDTO nuevo = new DepositoDTO(id, deposito.algoritmo(), deposito.nombre(),
        deposito.direccion(), deposito.capacidadMaxima(), new ArrayList<>());
    depositos.put(id, nuevo);
    return nuevo;
  }

  @Override
  public DepositoDTO buscarDepositoPorID(String depositoID) throws NoSuchElementException {
    DepositoDTO dep = depositos.get(depositoID);
    if (dep == null) throw new NoSuchElementException("Depósito no encontrado: " + depositoID);
    return dep;
  }

  @Override
  public AsignacionDTO buscarAsignacionPorPaqueteID(String paqueteID) throws NoSuchElementException {
    AsignacionDTO asig = asignaciones.get(paqueteID);
    if (asig == null) throw new NoSuchElementException("Asignación no encontrada para paquete: " + paqueteID);
    return asig;
  }

  @Override
  public DepositoDTO gestionarDonacion(String depositoID, String donacionID,
      String productoID, Integer cantidad) throws NoSuchElementException {

    if (!depositos.containsKey(depositoID)) {
      throw new NoSuchElementException("Depósito no encontrado: " + depositoID);
    }
    if (cantidad == null || cantidad <= 0) {
      throw new RuntimeException("La cantidad debe ser mayor a 0");
    }

    List<NecesidadMaterialDTO> necesidades =
        fachadaDonadoresYEntidades.obtenerNecesidadesInsatisfechasDe(productoID);

    PaqueteDTO paquete = new PaqueteDTO("paq-" + donacionID, donacionID, productoID, cantidad);

    if (necesidades != null && !necesidades.isEmpty()) {
      AsignacionDTO asig = ejecutarMatchmaking(depositoID, paquete, necesidades);
      if (asig != null) {
        asignaciones.put(paquete.id(), asig);
      }
    }
    return buscarDepositoPorID(depositoID);
  }

  @Override
  public void setAlgoritmoMM(String depositoID, TipoAlgoritmoEnum tipoAlgoritmo) {
    algoritmos.put(depositoID, tipoAlgoritmo);
    DepositoDTO dep = depositos.get(depositoID);
    if (dep != null) {
      DepositoDTO actualizado = new DepositoDTO(dep.id(), tipoAlgoritmo, dep.nombre(),
          dep.direccion(), dep.capacidadMaxima(), dep.stockActual());
      depositos.put(depositoID, actualizado);
    }
  }

  @Override
  public AsignacionDTO ejecutarMatchmaking(String depositoID, PaqueteDTO paqueteDTO,
      List<NecesidadMaterialDTO> necesidades) {

    if (depositoID == null || paqueteDTO == null) {
      throw new RuntimeException("depositoID y paqueteDTO no pueden ser nulos");
    }
    if (necesidades == null || necesidades.isEmpty()) return null;

    NecesidadMaterialDTO elegida = necesidades.stream()
        .max(Comparator.comparingInt(NecesidadMaterialDTO::nivelDeUrgencia))
        .orElse(necesidades.get(0));

    String asigId = UUID.randomUUID().toString();
    return new AsignacionDTO(asigId, paqueteDTO.id(), elegida.id(),
        LocalDateTime.now(), EstadoAsginacionEnum.ASIGNADA);
  }

  @Override
  public void reportarEntrega(PaqueteDTO paqueteDTO) {
    if (paqueteDTO == null) throw new RuntimeException("El paquete no puede ser nulo");

    AsignacionDTO asig = asignaciones.get(paqueteDTO.id());
    if (asig == null) throw new NoSuchElementException("No existe asignación para ese paquete");

    AsignacionDTO completada = new AsignacionDTO(asig.id(), asig.paqueteID(),
        asig.necesidadID(), asig.fecha(), EstadoAsginacionEnum.COMPLETADA);
    asignaciones.put(paqueteDTO.id(), completada);

    fachadaDonadoresYEntidades.satisfacerNecesidad(asig.necesidadID(), paqueteDTO.cantidad());
    fachadaDonaciones.cambiarEstadoDeDonacion(paqueteDTO.donacionID(),
        ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum.ACEPTADA);
  }

  @Override
  public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades f) {
    this.fachadaDonadoresYEntidades = f;
  }

  @Override
  public void setFachadaDonaciones(FachadaDonaciones f) {
    this.fachadaDonaciones = f;
  }
}
