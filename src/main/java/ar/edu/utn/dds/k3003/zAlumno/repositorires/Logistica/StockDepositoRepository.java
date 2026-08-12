package ar.edu.utn.dds.k3003.zAlumno.repositorires.Logistica;

import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.StockDeposito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockDepositoRepository extends JpaRepository<StockDeposito, Long> {

    // stock de un producto específico en un depósito específico
    Optional<StockDeposito> findByDepositoidAndProductoid(String depositoid, String productoid);

    // el stock de un producto en todos los depósitos
    List<StockDeposito> findByProductoid(String productoid);

    // stock de un depósito
    List<StockDeposito> findByDepositoid(String depositoid);
}