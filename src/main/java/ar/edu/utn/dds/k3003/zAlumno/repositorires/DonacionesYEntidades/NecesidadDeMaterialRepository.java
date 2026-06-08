package ar.edu.utn.dds.k3003.zAlumno.repositorires.DonacionesYEntidades;

import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.NecesidadDeMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NecesidadDeMaterialRepository extends JpaRepository<NecesidadDeMaterial, String>{
    List<NecesidadDeMaterial> findByProductoSolicitadoid(String productoSolicitadoid);
}
