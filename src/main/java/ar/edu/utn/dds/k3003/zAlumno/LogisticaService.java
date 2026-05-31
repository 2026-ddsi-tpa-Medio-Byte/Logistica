package ar.edu.utn.dds.k3003.zAlumno;


import ar.edu.utn.dds.k3003.zAlumno.Interface.Algoritmos_Interface;
import ar.edu.utn.dds.k3003.zAlumno.Interface.Donaciones_Interface;
import ar.edu.utn.dds.k3003.zAlumno.Interface.Logistica_Interface;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.LogisticaDTOs;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogisticaService implements Logistica_Interface, Donaciones_Interface {

    private List<LogisticaDTOs.DepositoDTO> listaDepositosDTO = new ArrayList<>();
    private List<LogisticaDTOs.Deposito> listaDepositos = new ArrayList<>();
    private List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO = new ArrayList<>();
    private List<DonacionYEntiDTOs.NecesidadDeMaterial> listaNecesidadMaterial = new ArrayList<>();
    private List<LogisticaDTOs.AsignacionDTO> listaAsignacionDTO = new ArrayList<>();
    private List<LogisticaDTOs.Asignacion> listaAsignacion = new ArrayList<>();
    private List<DonacionesDTOs.DonacionDTO> listaDoancionesDTO = new ArrayList<>();
    private List<DonacionesDTOs.Donacion> listaDoanciones = new ArrayList<>();


    public LogisticaService(){

        LogisticaDTOs.DepositoDTO deposito1 = new LogisticaDTOs.DepositoDTO(
                "Depósito Central",
                "DEP-001",
                "Av. Corrientes 1234, CABA",
                5000,
                1200,
                LogisticaDTOs.TipoAlgoritmoEnum.SUBATENDIDOS
        );

        LogisticaDTOs.DepositoDTO deposito2 = new LogisticaDTOs.DepositoDTO(
                "Nodo Logístico Norte",
                "DEP-002",
                "Ruta 9 Km 50, Escobar",
                3000,
                2800,
                LogisticaDTOs.TipoAlgoritmoEnum.PRIOSCORE
        );

        LogisticaDTOs.DepositoDTO deposito3 = new LogisticaDTOs.DepositoDTO(
                "Depósito Donaciones Sur",
                "DEP-003",
                "Calle 45 nro 890, La Plata",
                1500,
                450,
                LogisticaDTOs.TipoAlgoritmoEnum.NULL // Este inicia sin algoritmo configurado
        );

        LogisticaDTOs.DepositoDTO deposito4 = new LogisticaDTOs.DepositoDTO(
                "Punto de Acopio Este",
                "DEP-004",
                "Av. Rivadavia 15000, Haedo",
                2000,
                1900,
                LogisticaDTOs.TipoAlgoritmoEnum.SUBATENDIDOS
        );

        LogisticaDTOs.DepositoDTO deposito5 = new LogisticaDTOs.DepositoDTO(
                "Centro de Emergencias",
                "DEP-005",
                "Gral. Paz y Beiró, CABA",
                1000,
                100,
                LogisticaDTOs.TipoAlgoritmoEnum.PRIOSCORE
        );

        listaDepositosDTO.addAll(Arrays.asList(deposito1,deposito2,deposito3,deposito4,deposito5));
    }


    //BUSQUEDA
    @Override
    public LogisticaDTOs.DepositoDTO buscarDepositoIDDTO(String depositoid){
        for(LogisticaDTOs.DepositoDTO d : listaDepositosDTO){
            if (d.depositoid().equals(depositoid)){
                return d;
            }
        }
        return null;
    }

    @Override
    public LogisticaDTOs.Deposito buscarDepositoID(String depositoid){
        for(LogisticaDTOs.Deposito d : listaDepositos){
            if (d.getId().equals(depositoid)){
                return d;
            }
        }
        return null;
    }

    @Override
    public LogisticaDTOs.DepositoDTO agregarDeposito(LogisticaDTOs.DepositoDTO deposito) {
        this.listaDepositosDTO.add(deposito);
        return deposito;
    }

    @Override
    public void eliminarDeposito(String depositoid) {
        LogisticaDTOs.DepositoDTO deposito = buscarDepositoIDDTO(depositoid);
        if (deposito != null){
            listaDepositosDTO.remove(deposito);
            System.out.println("Deposito eliminado con exito");
        }
    }

    @Override
    public LogisticaDTOs.DepositoDTO modificarDeposito(String depositoid, LogisticaDTOs.DepositoDTO nuevosDatos) {
        LogisticaDTOs.DepositoDTO deposito = buscarDepositoIDDTO(depositoid);
        if (deposito != null){
            int indice = listaDepositosDTO.indexOf(deposito);
            listaDepositosDTO.set(indice, nuevosDatos);
            return nuevosDatos;
        }
        return null;
    }//REVISAR

    @Override
    public DonacionYEntiDTOs.NecesidadMaterialDTO buscarNecesidadPorIDDTO(String necesidadId){
        for(DonacionYEntiDTOs.NecesidadMaterialDTO necesidad : listaNecesidadMaterialDTO){
            if(necesidad.necesidadid().equals(necesidadId)){
                return necesidad;
            }
        }
        return null;
    }

    @Override
    public DonacionYEntiDTOs.NecesidadDeMaterial buscarNecesidadPorID(String necesidadId){
        for(DonacionYEntiDTOs.NecesidadDeMaterial necesidad : listaNecesidadMaterial){
            if(necesidad.getId().equals(necesidadId)){
                return necesidad;
            }
        }
        return null;
    }

    @Override
    public LogisticaDTOs.AsignacionDTO buscarAsignacionPorPaqueteIDDTO(String paqueteId){
        return listaAsignacionDTO.stream().filter(asig -> asig.paqueteid().equals(paqueteId)).findFirst().orElse(null);
    }

    @Override
    public LogisticaDTOs.Asignacion buscarAsignacionPorPaqueteID(String paqueteId){
        return listaAsignacion.stream().filter(asig -> asig.getId().equals(paqueteId)).findFirst().orElse(null);
    }

    @Override
    public DonacionesDTOs.Donacion buscarDonacionPorID(String donacionid){
        return listaDoanciones.stream().filter(n -> n.getId().equals(donacionid)).findFirst().orElse(null);
    }

    @Override
    public DonacionesDTOs.DonacionDTO buscarDonacionPorIDDTO(String donacionid){
        return listaDoancionesDTO.stream().filter(n -> n.donacionid().equals(donacionid)).findFirst().orElse(null);
    }

    @Override
    public List<LogisticaDTOs.DepositoDTO> obtenerTodosDepositosDTO(){
        return this.listaDepositosDTO;
    }


    //LOGICAS
    public void setAlgoritmoMM(String depositoid, LogisticaDTOs.TipoAlgoritmoEnum algoritmo){
        LogisticaDTOs.Deposito deposito = buscarDepositoID(depositoid);
        deposito.setAlgoritmo(algoritmo);
    }

    @Override
    public LogisticaDTOs.DepositoDTO gestionarDonacion(String depositoid,String donacionid, String productoid, Integer cantidad) {

        LogisticaDTOs.PaqueteDTO paqueteMatch = new LogisticaDTOs.PaqueteDTO(
                "paq-" + donacionid,
                donacionid,
                productoid,
                cantidad
        );

        if(cantidad > 0){
            if(listaNecesidadMaterialDTO.stream().anyMatch(n -> n.productoSolicitadoid().equals(productoid))){
                List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNececidadesFiltradaDTO = listaNecesidadMaterialDTO.stream()
                        .filter(n -> {
                            DonacionYEntiDTOs.NecesidadDeMaterial necesidad = buscarNecesidadPorID(n.necesidadid());
                            return necesidad.getcantidadActual() < necesidad.getcantidadObjetivo();})
                        .filter(n -> n.productoSolicitadoid().equals(productoid))
                        .collect(Collectors.toList());

                if(!listaNececidadesFiltradaDTO.isEmpty()){
                LogisticaDTOs.AsignacionDTO asignacion = ejecutarMatchmaking(depositoid, paqueteMatch, listaNececidadesFiltradaDTO);
            }
            }
            else{
                System.out.println("No existe necesidad");
            }
        }
        else{
            System.out.println("Cantidad insuficiente");
        }
        return buscarDepositoIDDTO(depositoid);
    }

    public LogisticaDTOs.AsignacionDTO ejecutarMatchmaking(String depositoid, LogisticaDTOs.PaqueteDTO paquete, List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO){

        LogisticaDTOs.Deposito deposito = buscarDepositoID(depositoid);
        LogisticaDTOs.TipoAlgoritmoEnum algoritmoConfigurado = deposito.getAlgoritmo();
        Algoritmos_Interface algoritomo = MatcheoAlgoritmos.seleccionAlgoritmo(algoritmoConfigurado);

        return algoritomo.ejecutarAlgoritmo(depositoid, paquete, listaNecesidadMaterialDTO);
    }

    public void reportarEntrega(LogisticaDTOs.PaqueteDTO paquete){
        LogisticaDTOs.Asignacion asigancion = buscarAsignacionPorPaqueteID(paquete.paqueteid());

        if(asigancion != null){
            asigancion.setEstado(LogisticaDTOs.EstadoAsginacionEnum.COMPLETADA);
            LogisticaDTOs.AsignacionDTO asignacionVieja = buscarAsignacionPorPaqueteIDDTO(paquete.paqueteid());
            int indice = listaAsignacionDTO.indexOf(asignacionVieja);
            if(indice != -1){
                listaAsignacionDTO.set(indice, new LogisticaDTOs.AsignacionDTO(
                        asigancion.getId(),
                        asigancion.getpaqueteId(),
                        asigancion.getNecesidadId(),
                        asigancion.getfecha(),
                        asigancion.getEstado()
                ));
            }
        }

        DonacionesDTOs.Donacion donacion = buscarDonacionPorID(paquete.donacionID());

        if(donacion != null){
            donacion.setEstado(DonacionesDTOs.EstadoDonacionEnum.ACEPTADA);
            DonacionesDTOs.DonacionDTO donacionVieja = buscarDonacionPorIDDTO(paquete.donacionID());
            int indice = listaDoancionesDTO.indexOf(donacionVieja);
            if(indice != -1){
                listaDoancionesDTO.set(indice, new DonacionesDTOs.DonacionDTO(
                        donacion.getId(),
                        donacion.getDonadorId(),
                        donacion.getdepositoId(),
                        donacion.getDescripcion(),
                        donacion.getproductoId(),
                        donacion.getCantidad(),
                        donacion.getEstado()
                ));
            }
        }
    }


}
    /*viejo LogisticaDTOs.AsignacionDTO ejecutarMatchmaking(String depositoid, LogisticaDTOs.PaqueteDTO paquete, List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO){
        DonacionYEntiDTOs.NecesidadDeMaterial entidadElegida = null;
        double menorPorcentaje = 101.0;

        for(DonacionYEntiDTOs.NecesidadDeMaterial necesidad : listaNecesidadMaterial){
            if(necesidad.getproductoSolicitadoid().equals(paquete.productoid())){
                double porcentajeActual = (necesidad.getcantidadActual() * 100.0) / necesidad.getcantidadObjetivo();
                if(porcentajeActual < menorPorcentaje){
                    menorPorcentaje = porcentajeActual;
                    entidadElegida = necesidad;//problema
                }
            }
        }

        return new LogisticaDTOs.AsignacionDTO(
                java.util.UUID.randomUUID().toString(),
                paquete.paqueteid(),
                entidadElegida.necesidadid(),
                LocalDate.now(),
                "ASIGNADA"
        );
    }*/

    /*LogisticaDTOs.AsignacionDTO ejecutarMatchmaking(LogisticaDTOs.PaqueteDTO paquete, List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO, LogisticaDTOs.DepositoDTO deposito){

        void setAlgoritmoMM();

        DonacionYEntiDTOs.NecesidadDeMaterial entidadElegida = null;
        double menorPorcentaje = 101.0;

        for(DonacionYEntiDTOs.NecesidadDeMaterial necesidad : listaNecesidadMaterial){
            if(necesidad.getproductoSolicitadoid().equals(paquete.productoid())){
                double porcentajeActual = (necesidad.getcantidadActual() * 100.0) / necesidad.getcantidadObjetivo();
                if(porcentajeActual < menorPorcentaje){
                    menorPorcentaje = porcentajeActual;
                    entidadElegida = necesidad;//problema
                }
            }
        }

        return new LogisticaDTOs.AsignacionDTO(
                java.util.UUID.randomUUID().toString(),
                paquete.paqueteid(),
                entidadElegida.necesidadid(),
                LocalDate.now(),
                "ASIGNADA"
        );
    }*/

    /*public void reportarEntrega(LogisticaDTOs.PaqueteDTO paquete) {

        int indice = -1;
        for (int i = 0; i < listaAsignacion.size(); i++) {
            if (listaAsignacion.get(i).paqueteid().equals(paquete.id())) {
                indice = i;
                break;
            }
        }

        if (indice != -1) {
            LogisticaDTOs.AsignacionDTO vieja = listaAsignacion.get(indice);

            LogisticaDTOs.AsignacionDTO nueva = new LogisticaDTOs.AsignacionDTO(
                    vieja.id(),
                    vieja.paqueteid(),
                    vieja.necesidadid(),
                    vieja.fecha(),
                    "COMPLETADA"
            );

            listaAsignacion.set(indice, nueva);

            int indice2 = -1;
            for (int i = 0; i < listaDoanciones.size(); i++) {
                if (listaDoanciones.get(i).id().equals(paquete.donacionID())) {
                    indice = i;
                    break;
                }
            }

            if (indice2 != -1) {
                DonacionesDTOs.DonacionDTO vieja2 = listaDoanciones.get(indice2);

                DonacionesDTOs.DonacionDTO nueva2 = new DonacionesDTOs.DonacionDTO(
                        vieja2.id(),
                        vieja2.donadorid(),
                        vieja2.depositoID(),
                        vieja2.descripcion(),
                        "ACEPTADA",
                        vieja2.ProductoID(),
                        vieja2.cantidad()
                );

                listaDoanciones.set(indice2, nueva2);

                registrarAuditoria(nueva, "ENTREGA_REPORTADA"); //??
            }
        }
    /*La plataforma deberá permitir a los transportistas, una vez completada la entrega, registrarla en el sistema para cambiar el estado de la asignación y donación.
Al confirmarse la entrega:
El estado de la asignación pasará a “Completada” (ver sección Estados).
El estado de la donación deberá ser actualizado a “Aceptada” (ver sección Estados).
El sistema deberá garantizar la trazabilidad y auditoría de los estados de las asignaciones

        void setAlgoritmoMM();



    }no terminada*/


