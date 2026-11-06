package ar.edu.utn.dds.k3003.zAlumno.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// Serializa, entre todas las instancias (workers), las operaciones que asignan un mismo producto.
//
// Problema que resuelve: con 2 workers, dos donaciones del mismo producto se procesaban en
// paralelo; los dos leian la misma necesidad con el mismo faltante y los dos le asignaban,
// sobreasignandola. Lo mismo entre un worker y una solicitud de Donadores.
//
// Usa un advisory lock de Postgres a nivel transaccion: lo toma la primera transaccion que llega;
// la otra espera en la base hasta que la primera hace commit o rollback, y recien ahi sigue
// (y ya ve las asignaciones que la primera guardo). Productos distintos no se bloquean entre si.
// Como cada transaccion toma un solo lock, no puede haber deadlock entre ellas.
@Component
public class LockPorProducto {

    private final JdbcTemplate jdbcTemplate;

    public LockPorProducto(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // MANDATORY: tiene que llamarse dentro de una transaccion (si no, el lock se liberaria al instante).
    @Transactional(propagation = Propagation.MANDATORY)
    public void bloquear(String productoid) {
        // hashtext convierte la clave a un entero, que es lo que recibe pg_advisory_xact_lock.
        // Se envuelve en un SELECT 1 porque la funcion devuelve void.
        jdbcTemplate.queryForObject(
                "SELECT 1 FROM (SELECT pg_advisory_xact_lock(hashtext(?))) AS l",
                Integer.class,
                "logistica:producto:" + productoid);
    }
}
