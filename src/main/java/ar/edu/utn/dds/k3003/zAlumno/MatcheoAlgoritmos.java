package ar.edu.utn.dds.k3003.zAlumno;

import ar.edu.utn.dds.k3003.zAlumno.Interface.Algoritmos_Interface;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.DonacionesYEntidades.NecesidadDeMaterialRepository;

public class MatcheoAlgoritmos {
    public static Algoritmos_Interface seleccionAlgoritmo(LogisticaDTOs.TipoAlgoritmoEnum tipo) {
        if (tipo == null) {
            throw new IllegalStateException("El depósito no tiene un algoritmo configurado.");
        }
        switch(tipo) {
            case SUBATENDIDOS:
                return new subAtendidos();
            case PRIOSCORE:
                return new prioScore();
            default:
                throw new IllegalArgumentException("Algoritmo no soportado");
        }
    }
}
