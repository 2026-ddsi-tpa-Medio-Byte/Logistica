package ar.edu.utn.dds.k3003.zAlumno.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EstadoDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class DonadoresYEntidadesClient implements FachadaDonadoresYEntidades {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public DonadoresYEntidadesClient(
      RestTemplate restTemplate,
      @Value("${donadoresyentidades.url:http://localhost:8083}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  @Override
  public List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(String productoSolicitadoID) {
    String url =
        UriComponentsBuilder.fromUriString(baseUrl + "/necesidades")
            .queryParam("productoID", productoSolicitadoID)
            .toUriString();
    NecesidadMaterialDTO[] necesidades = restTemplate.getForObject(url, NecesidadMaterialDTO[].class);
    return necesidades != null ? Arrays.asList(necesidades) : List.of();
  }

  @Override
  public NecesidadMaterialDTO satisfacerNecesidad(String necesidadID, Integer cantidad)
      throws NoSuchElementException {
    String url = baseUrl + "/necesidades/" + necesidadID + "/satisfaccion";
    Map<String, Object> body = Map.of("cantidad", cantidad);
    try {
      return restTemplate.postForObject(url, body, NecesidadMaterialDTO.class);
    } catch (Exception e) {
      throw new NoSuchElementException("Necesidad no encontrada con ID: " + necesidadID);
    }
  }

  @Override
  public DonadorDTO agregarDonador(DonadorDTO donadorDTO) {
    throw new UnsupportedOperationException("Operación no implementada en DonadoresYEntidadesClient");
  }

  @Override
  public DonadorDTO buscarDonadorPorID(String donadorID) throws NoSuchElementException {
    throw new UnsupportedOperationException("Operación no implementada en DonadoresYEntidadesClient");
  }

  @Override
  public EntidadBeneficaDTO agregarEntidad(EntidadBeneficaDTO entidadBeneficaDTO) {
    throw new UnsupportedOperationException("Operación no implementada en DonadoresYEntidadesClient");
  }

  @Override
  public EntidadBeneficaDTO buscarEntidadPorID(String entidadID) throws NoSuchElementException {
    throw new UnsupportedOperationException("Operación no implementada en DonadoresYEntidadesClient");
  }

  @Override
  public NecesidadMaterialDTO registrarNecesidad(NecesidadMaterialDTO necesidadMaterialDTO) {
    throw new UnsupportedOperationException("Operación no implementada en DonadoresYEntidadesClient");
  }

  @Override
  public QuejaDTO agregarQueja(QuejaDTO quejaDTO) throws NoSuchElementException {
    throw new UnsupportedOperationException("Operación no implementada en DonadoresYEntidadesClient");
  }

  @Override
  public Boolean puedeDonar(String donadorID) throws NoSuchElementException {
    throw new UnsupportedOperationException("Operación no implementada en DonadoresYEntidadesClient");
  }

  @Override
  public List<QuejaDTO> obtenerQuejasDe(String donadorID) throws NoSuchElementException {
    throw new UnsupportedOperationException("Operación no implementada en DonadoresYEntidadesClient");
  }

  @Override
  public DonadorDTO modificarEstado(String donadorID, EstadoDonadorEnum estado)
      throws NoSuchElementException {
    throw new UnsupportedOperationException("Operación no implementada en DonadoresYEntidadesClient");
  }

  @Override
  public DonadorDTO modifcarCategoria(String donadorID, String categoria)
      throws NoSuchElementException {
    throw new UnsupportedOperationException("Operación no implementada en DonadoresYEntidadesClient");
  }

  @Override
  public DonadorStatsDTO estadisticasDonador(String donadorID) {
    throw new UnsupportedOperationException("Operación no implementada en DonadoresYEntidadesClient");
  }

  @Override
  public void setFachadaIncentivos(FachadaIncentivos fachadaIncentivos) {
  }
}
