package ar.edu.utn.dds.k3003.zAlumno.repositorires.Logistica;

import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, String> {

    Optional<Asignacion> findByPaqueteid(String paqueteid);

    boolean existsByPaqueteid(String paqueteid);

}
