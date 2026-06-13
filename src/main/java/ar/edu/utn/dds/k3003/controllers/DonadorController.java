package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/donadores")
public class DonadorController {

  private FachadaDonadoresYEntidades fachada;
  public DonadorController(FachadaDonadoresYEntidades fachada) {
    this.fachada = fachada;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<DonadorDTO> postDonador(@RequestBody DonadorDTO donadorDTO) {
    DonadorDTO donadorAgregado = fachada.agregarDonador(donadorDTO);
    return ResponseEntity.ok(donadorAgregado);
  }

  @GetMapping
  public ResponseEntity<DonadorDTO> getDonadorByID(@RequestParam String donadorID) {
    return ResponseEntity.status(HttpStatus.OK).body(this.fachada.buscarDonadorPorID(donadorID));
  }
}
