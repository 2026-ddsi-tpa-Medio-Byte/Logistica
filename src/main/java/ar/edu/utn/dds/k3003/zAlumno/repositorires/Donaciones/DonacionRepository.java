package ar.edu.utn.dds.k3003.zAlumno.repositorires.Donaciones;

import ar.edu.utn.dds.k3003.zAlumno.entidades.Donaciones.Donacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, String> {
}
