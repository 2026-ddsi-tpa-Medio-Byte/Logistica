package ar.edu.utn.dds.k3003.zAlumno.Interface;

import ar.edu.utn.dds.k3003.zAlumno.entidades.Donaciones.Donacion;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Donaciones.DonacionesDTOs;

public interface Donaciones_Interface {

    DonacionesDTOs.DonacionDTO buscarDonacionPorIDDTO(String donacionid);
    Donacion buscarDonacionPorID(String donacionid);

}
