package ar.edu.utn.dds.k3003.zAlumno.repositorires.Logistica;

import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.Asignacion;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, String> {

    Optional<Asignacion> findByPaqueteid(String paqueteid);

    boolean existsByPaqueteid(String paqueteid);

    List<Asignacion> findByEstado(LogisticaDTOs.EstadoAsginacionEnum estado);

    List<Asignacion> findByNecesidadid(String necesidadid);

    List<Asignacion> findByDonacionid(String donacionid);

    // Cuanto ya hay asignado (en un estado dado) a una necesidad. Se usa para no sobre-asignar.
    @Query("select coalesce(sum(a.cantidad), 0) from Asignacion a "
            + "where a.necesidadid = :necesidadid and a.estado = :estado")
    Long sumarCantidadPorNecesidadYEstado(@Param("necesidadid") String necesidadid,
                                          @Param("estado") LogisticaDTOs.EstadoAsginacionEnum estado);

}