package ar.edu.utn.dds.k3003.zAlumno.services;


import ar.edu.utn.dds.k3003.zAlumno.Interface.Algoritmos_Interface;
import ar.edu.utn.dds.k3003.zAlumno.Interface.Donaciones_Interface;
import ar.edu.utn.dds.k3003.zAlumno.Interface.Logistica_Interface;
import ar.edu.utn.dds.k3003.zAlumno.MatcheoAlgoritmos;
import ar.edu.utn.dds.k3003.zAlumno.clients.DonacionesClient;
import ar.edu.utn.dds.k3003.zAlumno.clients.DonadoresYEntidadesClient;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Donaciones.Donacion;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Donaciones.DonacionesDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.NecesidadDeMaterial;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.Asignacion;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.Deposito;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.Donaciones.DonacionRepository;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.DonacionesYEntidades.NecesidadDeMaterialRepository;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.Logistica.AsignacionRepository;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.Logistica.DepositoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogisticaService implements Logistica_Interface, Donaciones_Interface {

    private List<LogisticaDTOs.DepositoDTO> listaDepositosDTO = new ArrayList<>();
    private List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO = new ArrayList<>();
    private List<LogisticaDTOs.AsignacionDTO> listaAsignacionDTO = new ArrayList<>();
    private List<DonacionesDTOs.DonacionDTO> listaDoancionesDTO = new ArrayList<>();

    @Autowired
    private DepositoRepository depositoRepository;

    @Autowired
    private AsignacionRepository asignacionRepository;

    @Autowired
    private DonacionRepository donacionRepository;

    @Autowired
    private NecesidadDeMaterialRepository necesidaddematerialRepository;

    @Autowired
    private DonacionesClient donacionesClient;

    @Autowired
    private DonadoresYEntidadesClient donadoresYEntidadesClient;

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

    @Override
    public LogisticaDTOs.DepositoDTO buscarDepositoIDDTO(String depositoid) {
        Deposito deposito = depositoRepository.findById(depositoid).orElse(null);

        if (deposito == null) {
            return null;
        }

        return new LogisticaDTOs.DepositoDTO(
                deposito.getNombre(),
                deposito.getId(),
                deposito.getDireccion(),
                deposito.getCapacidadMaxima(),
                deposito.getStockActual(),
                deposito.getAlgoritmo()
        );
    }

    @Override
    public Deposito buscarDepositoID(String depositoid) {
        return depositoRepository.findById(depositoid).orElse(null);
    }

    @Override
    public LogisticaDTOs.DepositoDTO agregarDeposito(LogisticaDTOs.DepositoDTO depositoDTO) {
        String id;
        if (depositoDTO.depositoid() != null && !depositoDTO.depositoid().isBlank()) {
            id = depositoDTO.depositoid();
        } else {
            long cantidad = depositoRepository.countByDepositoidStartingWith("DEP-UTN-");
            id = String.format("DEP-UTN-%02d", cantidad + 1);
        }

        LogisticaDTOs.DepositoDTO dtoConId = new LogisticaDTOs.DepositoDTO(
                depositoDTO.nombre(),
                id,
                depositoDTO.direccion(),
                depositoDTO.capacidadMaxima(),
                depositoDTO.stockActual() != null ? depositoDTO.stockActual() : 0,
                depositoDTO.algoritmo()
        );

        Deposito deposito = new Deposito(dtoConId);
        depositoRepository.save(deposito);
        return buscarDepositoIDDTO(id);
    }

    /*public LogisticaDTOs.DepositoDTO agregarDeposito(LogisticaDTOs.DepositoDTO depositoDTO) {
        Deposito deposito = new Deposito(depositoDTO);
        Deposito guardado = depositoRepository.save(deposito);
        return buscarDepositoIDDTO(guardado.getId());
    }*/

    @Override
    public void eliminarDeposito(String depositoid) {
        if (depositoRepository.existsById(depositoid)) {
            depositoRepository.deleteById(depositoid);
            System.out.println("Deposito eliminado con exito");
        } else {
            System.out.println("No se encontro el deposito a eliminar");
        }
    }

    @Override
    public LogisticaDTOs.DepositoDTO modificarDeposito(String depositoid, LogisticaDTOs.DepositoDTO nuevosDatos) {
        if (depositoRepository.existsById(depositoid)) {
            Deposito depModificado = new Deposito(nuevosDatos);
            depositoRepository.save(depModificado);
            return nuevosDatos;
        }
        return null;
    }

    @Override
    public DonacionYEntiDTOs.NecesidadMaterialDTO buscarNecesidadPorIDDTO(String necesidadId) {
        NecesidadDeMaterial necesidad = necesidaddematerialRepository.findById(necesidadId).orElse(null);

        if (necesidad == null) {
            return null;
        }

        return new DonacionYEntiDTOs.NecesidadMaterialDTO(
                necesidad.getId(),
                necesidad.getEntidadid(),
                necesidad.getNivelDeUrgencia(),
                necesidad.getDescripcion(),
                necesidad.getcantidadObjetivo(),
                necesidad.getcantidadActual(),
                necesidad.getproductoSolicitadoid(),
                necesidad.getTipo()

        );
    }

    @Override
    public NecesidadDeMaterial buscarNecesidadPorID(String necesidadId){
        return necesidaddematerialRepository.findById(necesidadId).orElse(null);
    }

    @Override
    public LogisticaDTOs.AsignacionDTO buscarAsignacionPorPaqueteIDDTO(String paqueteId) {
        Asignacion asig = asignacionRepository.findByPaqueteid(paqueteId).orElse(null);

        if (asig == null) {
            return null;
        }

        return new LogisticaDTOs.AsignacionDTO(
                asig.getId(),
                asig.getpaqueteId(),
                asig.getNecesidadId(),
                asig.getfecha(),
                asig.getEstado()
        );
    }

    @Override
    public Asignacion buscarAsignacionPorPaqueteID(String paqueteId) {
        return asignacionRepository.findByPaqueteid(paqueteId).orElse(null);
    }

    @Override
    public Donacion buscarDonacionPorID(String donacionid){
        return donacionRepository.findById(donacionid).orElse(null);
    }

    @Override
    public DonacionesDTOs.DonacionDTO buscarDonacionPorIDDTO(String donacionid) {
        Donacion donacion = donacionRepository.findById(donacionid).orElse(null);

        if (donacion == null) {
            return null;
        }

        return new DonacionesDTOs.DonacionDTO(
                donacion.getId(),
                donacion.getDonadorId(),
                donacion.getdepositoId(),
                donacion.getDescripcion(),
                donacion.getproductoId(),
                donacion.getCantidad(),
                donacion.getEstado()
        );
    }

    @Override
    public List<LogisticaDTOs.DepositoDTO> obtenerTodosDepositosDTO() {
        List<Deposito> depositos = depositoRepository.findAll();

        return depositos.stream()
                .map(d -> new LogisticaDTOs.DepositoDTO(
                        d.getNombre(),
                        d.getId(),
                        d.getDireccion(),
                        d.getCapacidadMaxima(),
                        d.getStockActual(),
                        d.getAlgoritmo()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void agregarAlStock(String depositoId, Integer cantidad){

        Deposito deposito = buscarDepositoID(depositoId);

        if(deposito.estaLleno()){
            System.out.println("Deposito lleno, se descarto el sobrante");
        }

        if(!deposito.estaLleno()){
            int espacio = deposito.espacioDisponible();

            if(cantidad <= espacio){
                deposito.agregarAlStock(cantidad);
                depositoRepository.save(deposito);
                System.out.println("Producto guardado en deposito sin sobrante");
            }
            else{
                deposito.agregarAlStock(espacio);
                depositoRepository.save(deposito);
                System.out.println("Producto guardado en deposito sobrante descartado");
            }
        }
    }

    public void setAlgoritmoMM(String depositoid, LogisticaDTOs.TipoAlgoritmoEnum algoritmo){
        Deposito deposito = buscarDepositoID(depositoid);
        deposito.setAlgoritmo(algoritmo);
        depositoRepository.save(deposito);
    }

    @Transactional
    @Override
    public LogisticaDTOs.GestionDonacionResponseDTO gestionarDonacion(String depositoid, String donacionid, String productoid, Integer cantidad) {


        //crea paquete
        LogisticaDTOs.PaqueteDTO paqueteMatch = new LogisticaDTOs.PaqueteDTO(
                "paq-" + donacionid,
                donacionid,
                productoid,
                cantidad
        );

        //verifica que la cantidad de la donacion NO sea 0
        if (cantidad <= 0) {
            System.out.println("Cantidad insuficiente");
            return new LogisticaDTOs.GestionDonacionResponseDTO(
                    "Cantidad insuficiente, no se creó asignación",
                    buscarDepositoIDDTO(depositoid),
                    null
            );
        }

        //verifica que el deposito exista
        Deposito deposito = buscarDepositoID(depositoid);
        if(deposito == null){
            System.out.println("Deposito id: " + depositoid + " no encontrado");
            return new LogisticaDTOs.GestionDonacionResponseDTO(
                    "Deposito id: " + depositoid + " no encontrado",
                    null,
                    null);
        }

        List<DonacionYEntiDTOs.NecesidadMaterialDTO> necesidadesDelProducto =
                donadoresYEntidadesClient.obtenerNecesidadesConCantidad(productoid);

        //verifica existencia de necesidades para producto especifico
        if (necesidadesDelProducto == null || necesidadesDelProducto.isEmpty()) {

            //NO NECESIDADES Y DEPOSITO LLENO
            if(deposito.getStockActual() >= deposito.getCapacidadMaxima()){
                System.out.println("No existe necesidad para el producto: " + productoid + "y el deposito " +  depositoid + "esta lleno");
                return new LogisticaDTOs.GestionDonacionResponseDTO(
                        "No existe necesidad para el producto: " + productoid + "y el deposito " +  depositoid + "esta lleno",
                        buscarDepositoIDDTO(depositoid),
                        null);
            }
            //NO NECESIDADES Y DEPOSITO DISPONIBLE, SE GUARDA EN DEPOSITO
            agregarAlStock(depositoid, cantidad);
            System.out.println("No existe necesidad para el producto: " + productoid + ", se guarda en deposito: " + depositoid);
            return new LogisticaDTOs.GestionDonacionResponseDTO(
                    "No existe necesidad para el producto: " + productoid + ", se guarda en deposito: " + depositoid,
                    buscarDepositoIDDTO(depositoid),
                    null
            );
        }

        //verifica existencia de necesidades, filtra las insatisfechas y filtra las recurrentes insuficientes
        List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadesFiltradaDTO =
                necesidadesDelProducto.stream()
                        .filter(n -> {
                            boolean noAlcanza = cantidad < (n.cantidadObjetivo() - n.cantidadActual());
                            boolean esRecurrente = n.tipo() == DonacionYEntiDTOs.TipoNecesidadMaterialEnum.RECURRENTE;
                            return !(noAlcanza && esRecurrente);
                        })
                        .collect(Collectors.toList());

        LogisticaDTOs.AsignacionDTO asignacion = null;

        if (!listaNecesidadesFiltradaDTO.isEmpty()) {
            asignacion = ejecutarMatchmaking(depositoid, paqueteMatch, listaNecesidadesFiltradaDTO);
            if (asignacion != null) {
                Asignacion nuevaAsignacion = new Asignacion(asignacion);
                asignacionRepository.save(nuevaAsignacion);

                final String necesidadIdBuscada = asignacion.necesidadid();

                DonacionYEntiDTOs.NecesidadMaterialDTO necesidadElegida =
                        necesidadesDelProducto.stream()
                                .filter(n -> n.necesidadid().equals(necesidadIdBuscada))
                                .findFirst()
                                .orElse(null);

                if (necesidadElegida != null) {

                    int cantidadNecesaria = necesidadElegida.cantidadObjetivo() - necesidadElegida.cantidadActual();

                    // caso: donación exacta o suficiente
                    if (cantidad >= cantidadNecesaria) {
                        int sobrante = cantidad - cantidadNecesaria;

                        if (sobrante > 0) {
                            if (!deposito.estaLleno()) {
                                agregarAlStock(depositoid, sobrante);
                                System.out.println("Asignación creada con éxito. Sobrante de " + sobrante + " guardado en stock");
                                return new LogisticaDTOs.GestionDonacionResponseDTO(
                                        "Asignación creada con éxito. Sobrante de " + sobrante + " unidades guardado en stock",
                                        buscarDepositoIDDTO(depositoid),
                                        asignacion
                                );
                            } else {
                                System.out.println("Asignación creada con éxito. Sobrante descartado: depósito lleno");
                                return new LogisticaDTOs.GestionDonacionResponseDTO(
                                        "Asignación creada con éxito. Sobrante de " + sobrante + " unidades descartado: depósito lleno",
                                        buscarDepositoIDDTO(depositoid),
                                        asignacion
                                );
                            }
                        }

                        System.out.println("Asignación creada con éxito. Sin sobrante");
                        return new LogisticaDTOs.GestionDonacionResponseDTO(
                                "Asignación creada con éxito. Sin sobrante",
                                buscarDepositoIDDTO(depositoid),
                                asignacion
                        );
                    }

                    // caso: donación insuficiente
                    if (cantidad < cantidadNecesaria) {
                        if (necesidadElegida.tipo() == DonacionYEntiDTOs.TipoNecesidadMaterialEnum.EXTRAORDINARIA) {
                            System.out.println("Asignación creada con éxito. Necesidad EXTRAORDINARIA, cantidad insuficiente pero se asigna igual");
                            return new LogisticaDTOs.GestionDonacionResponseDTO(
                                    "Asignación creada con éxito. Necesidad extraordinaria cubierta parcialmente",
                                    buscarDepositoIDDTO(depositoid),
                                    asignacion
                            );
                        }
                    }
                }

                /*if (necesidadElegida != null) {

                    //caso: cant. actual + donacion = cant. objetivo
                    if(necesidadElegida.cantidadActual() + cantidad == necesidadElegida.cantidadObjetivo()) {
                        int nuevoProgreso = necesidadElegida.cantidadActual() + cantidad;
                        necesidadElegida.setcantidadActual(nuevoProgreso);
                        //necesidaddematerialRepository.save(necesidadElegida);
                        System.out.println("Asignación guardada y necesidad actualizada con éxito. No hubo sobrante");
                        return new LogisticaDTOs.GestionDonacionResponseDTO(
                                "Asignación guardada y necesidad actualizada con éxito. No hubo sobrante",
                                buscarDepositoIDDTO(depositoid),
                                asignacion
                        );
                    }

                    //caso: cantidad prodcuto suficiente
                    if(necesidadElegida.getcantidadActual() + cantidad > necesidadElegida.getcantidadObjetivo()){

                        if(!deposito.estaLleno()){
                            int sobrante = necesidadElegida.getcantidadActual() + cantidad - necesidadElegida.getcantidadObjetivo();
                            int nuevoProgreso = necesidadElegida.getcantidadActual() + cantidad;
                            necesidadElegida.setcantidadActual(nuevoProgreso - sobrante);
                            necesidaddematerialRepository.save(necesidadElegida);
                            System.out.println("Asignación guardada y necesidad actualizada con éxito. Hubo sobrante");
                            agregarAlStock(depositoid, sobrante);
                            return new LogisticaDTOs.GestionDonacionResponseDTO(
                                    "Asignación guardada y necesidad actualizada con éxito. Hubo sobrante",
                                    buscarDepositoIDDTO(depositoid),
                                    asignacion
                            );
                        }
                    }

                    //caso: cantidad prodcuto insuficiente
                    if(necesidadElegida.getcantidadActual() + cantidad < necesidadElegida.getcantidadObjetivo()){

                        if(necesidadElegida.getTipo() == DonacionYEntiDTOs.TipoNecesidadMaterialEnum.EXTRAORDINARIA){

                            int nuevoProgreso = necesidadElegida.getcantidadActual() + cantidad;
                            necesidadElegida.setcantidadActual(nuevoProgreso);
                            necesidaddematerialRepository.save(necesidadElegida);
                            System.out.println("Asignación guardada y necesidad actualizada con éxito.");
                            return new LogisticaDTOs.GestionDonacionResponseDTO(
                                    "Asignación guardada y necesidad actualizada con éxito.",
                                    buscarDepositoIDDTO(depositoid),
                                    asignacion
                            );
                        }
                    }
                }*/
            }
        } else {
            System.out.println("Existen necesidades para el producto, pero todas están cubiertas (cantidad actual >= objetivo)");
            return new LogisticaDTOs.GestionDonacionResponseDTO(
                    "No existen necesidades insatisfechas para el producto: " + productoid,
                    buscarDepositoIDDTO(depositoid),
                    null
            );
        }

        return new LogisticaDTOs.GestionDonacionResponseDTO(
                asignacion != null ? "Asignación creada con éxito" : "Todas las necesidades del producto ya están cubiertas",
                buscarDepositoIDDTO(depositoid),
                asignacion
        );
    }

    public LogisticaDTOs.AsignacionDTO ejecutarMatchmaking(String depositoid, LogisticaDTOs.PaqueteDTO paquete, List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaNecesidadMaterialDTO){

        Deposito deposito = buscarDepositoID(depositoid);
        if (deposito == null) {
            throw new RuntimeException("No se pudo ejecutar el matchmaking: El depósito no existe.");
        }
        LogisticaDTOs.TipoAlgoritmoEnum algoritmoConfigurado = deposito.getAlgoritmo();
        Algoritmos_Interface algoritomo = MatcheoAlgoritmos.seleccionAlgoritmo(algoritmoConfigurado);

        return algoritomo.ejecutarAlgoritmo(depositoid, paquete, listaNecesidadMaterialDTO);
    }

    public LogisticaDTOs.ReporteEntregaResponseDTO reportarEntrega(LogisticaDTOs.PaqueteDTO paquete) {

        Asignacion asignacion = buscarAsignacionPorPaqueteID(paquete.paqueteid());
        if (asignacion == null) {
            throw new RuntimeException("No existe asignación para el paquete: " + paquete.paqueteid());
        }

        Donacion donacion = buscarDonacionPorID(paquete.donacionID());
        if (donacion == null) {
            throw new RuntimeException("No existe donación con id: " + paquete.donacionID());
        }

        asignacion.setEstado(LogisticaDTOs.EstadoAsginacionEnum.COMPLETADA);
        asignacionRepository.save(asignacion);

        try {
            donadoresYEntidadesClient.satisfacerNecesidad(asignacion.getNecesidadId(), paquete.cantidad());
        } catch (Exception e) {
            System.out.println("No se pudo satisfacer la necesidad " + asignacion.getNecesidadId() + ": " + e.getMessage());
        }

        donacion.setEstado(DonacionesDTOs.EstadoDonacionEnum.ACEPTADA);
        donacionRepository.save(donacion);

        try {
            donacionesClient.cambiarEstadoDeDonacion(
                    donacion.getId(),
                    ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum.ACEPTADA);
        } catch (Exception e) {
            System.out.println("No se pudo cambiar el estado de la donación " + donacion.getId() + ": " + e.getMessage());
        }

        return new LogisticaDTOs.ReporteEntregaResponseDTO(
                "Donación aceptada",
                donacion.getId(),
                "Asignación completada",
                asignacion.getId()
        );
    }

    public void limpiarTodaLaBase() {
        asignacionRepository.deleteAll();
        necesidaddematerialRepository.deleteAll();
        depositoRepository.deleteAll();
        System.out.println("Base de datos de Logística reseteada por completo.");
    }


}


