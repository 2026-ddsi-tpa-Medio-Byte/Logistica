package ar.edu.utn.dds.k3003.zAlumno.repositorires.Logistica;

import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.Deposito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositoRepository extends JpaRepository<Deposito, String> {
    long countByDepositoidStartingWith(String prefix);
}