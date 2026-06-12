package ar.edu.utn.dds.k3003.zAlumno.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;

/**
 * Cliente HTTP hacia el módulo de Donaciones. Implementa el flujo obligatorio
 * "Logística -> Donaciones: cambiar el estado de una donación".
 */
@Component
public class DonacionesClient implements FachadaDonaciones {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public DonacionesClient(
      RestTemplate restTemplate,
      @Value("${donaciones.url:http://localhost:8081}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  @Override
  public DonacionDTO cambiarEstadoDeDonacion(String donacionID, EstadoDonacionEnum estado)
      throws NoSuchElementException {
    String url = baseUrl + "/donaciones/" + donacionID + "/estado";
    try {
      return restTemplate.patchForObject(url, estado, DonacionDTO.class);
    } catch (Exception e) {
      throw new NoSuchElementException("Donación no encontrada con ID: " + donacionID);
    }
  }

  @Override
  public DonacionDTO registrarDonacion(DonacionDTO donacionDTO) {
    throw new UnsupportedOperationException("Operación no implementada en DonacionesClient");
  }

  @Override
  public DonacionDTO buscarDonacionPorID(String donacionID) throws NoSuchElementException {
    throw new UnsupportedOperationException("Operación no implementada en DonacionesClient");
  }

  @Override
  public List<DonacionDTO> buscarPorDonadorYFechaInicio(String donadorID, LocalDate fecha)
      throws NoSuchElementException {
    throw new UnsupportedOperationException("Operación no implementada en DonacionesClient");
  }

  @Override
  public DonacionDTO registrarQuejaEnDonacion(String donacionID, String descripcion) {
    throw new UnsupportedOperationException("Operación no implementada en DonacionesClient");
  }

  @Override
  public ProductoDTO agregarProducto(ProductoDTO productoDTO) {
    throw new UnsupportedOperationException("Operación no implementada en DonacionesClient");
  }

  @Override
  public ProductoDTO buscarProductoPorID(String productoID) throws NoSuchElementException {
    throw new UnsupportedOperationException("Operación no implementada en DonacionesClient");
  }

  @Override
  public IdentificadorDTO agregarIdentificador(IdentificadorDTO identificadorDTO) {
    throw new UnsupportedOperationException("Operación no implementada en DonacionesClient");
  }

  @Override
  public IdentificadorDTO buscarIdentificadorPorID(String identificadorID)
      throws NoSuchElementException {
    throw new UnsupportedOperationException("Operación no implementada en DonacionesClient");
  }

  @Override
  public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {
    // No aplica para el cliente HTTP
  }

  @Override
  public void setFachadaLogistica(FachadaLogistica fachadaLogistica) {
    // No aplica para el cliente HTTP
  }
}
