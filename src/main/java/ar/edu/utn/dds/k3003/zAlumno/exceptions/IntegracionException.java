package ar.edu.utn.dds.k3003.zAlumno.exceptions;

/** Fallo al comunicarse con otro modulo de DonaTrack (Donaciones o DonadoresYEntidades). */
public class IntegracionException extends RuntimeException {
    public IntegracionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}