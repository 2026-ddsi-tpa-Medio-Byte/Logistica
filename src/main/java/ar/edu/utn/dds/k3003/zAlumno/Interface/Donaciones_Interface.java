package ar.edu.utn.dds.k3003.zAlumno.Interface;

import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesDTOs;

public interface Donaciones_Interface {

    DonacionesDTOs.DonacionDTO buscarDonacionPorIDDTO(String donacionid);
    DonacionesDTOs.Donacion buscarDonacionPorID(String donacionid);

}
