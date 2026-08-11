package ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica;

import java.io.Serializable;

public record DonacionMensaje(
        String depositoid,
        String donacionid,
        String productoid,
        Integer cantidad
) implements Serializable {}
