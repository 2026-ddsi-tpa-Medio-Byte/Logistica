package ar.edu.utn.dds.k3003.zAlumno.services;


import ar.edu.utn.dds.k3003.zAlumno.Interface.Algoritmos_Interface;
import ar.edu.utn.dds.k3003.zAlumno.Interface.Donaciones_Interface;
import ar.edu.utn.dds.k3003.zAlumno.Interface.Logistica_Interface;
import ar.edu.utn.dds.k3003.zAlumno.MatcheoAlgoritmos;
import ar.edu.utn.dds.k3003.zAlumno.clients.DonacionesClient;
import ar.edu.utn.dds.k3003.zAlumno.clients.DonadoresYEntidadesClient;
import ar.edu.utn.dds.k3003.zAlumno.config.RabbitMQConfig;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Donaciones.Donacion;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.DonacionYEntiDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Donaciones.DonacionesDTOs;
import ar.edu.utn.dds.k3003.zAlumno.entidades.DonacionesYEntidades.NecesidadDeMaterial;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.Asignacion;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.Deposito;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.DonacionMensaje;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.Donaciones.DonacionRepository;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.DonacionesYEntidades.NecesidadDeMaterialRepository;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.Logistica.AsignacionRepository;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.Logistica.DepositoRepository;
import ar.edu.utn.dds.k3003.zAlumno.repositorires.Logistica.StockDepositoRepository;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.StockDeposito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;

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
    private StockDepositoRepository stockDepositoRepository;

    @Autowired
    private DonacionesClient donacionesClient;

    @Autowired
    private DonadoresYEntidadesClient donadoresYEntidadesClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MetricasService metricasService;

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
                asig.getEstado(),
                asig.getOrigen(),
                asig.getDonacionid(),
                asig.getProductoid(),
                asig.getCantidad()
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

    private void descontarStock(String productoID, Integer cantidad) {
        List<StockDeposito> stocks = stockDepositoRepository.findByProductoid(productoID);
        int restante = cantidad;

        for (StockDeposito stock : stocks) {
            if (restante <= 0) break;

            int aDescontar = Math.min(stock.getCantidad(), restante);
            stock.setCantidad(stock.getCantidad() - aDescontar);
            stockDepositoRepository.save(stock);

            // actualiza el depósito
            Deposito deposito = buscarDepositoID(stock.getDepositoid());
            deposito.setStockActual(deposito.getStockActual() - aDescontar);
            depositoRepository.save(deposito);
            metricasService.incrementarStockMovimiento("baja");

            restante -= aDescontar;
        }
    }

    @Override
    public void agregarAlStock(String depositoId, String productoId, Integer cantidad){

        Deposito deposito = buscarDepositoID(depositoId);

        if(deposito.estaLleno()){
            System.out.println("Deposito lleno, se descarto el sobrante");
            return;
        }

        int espacio = deposito.espacioDisponible();
        int cantidadAGuardar = Math.min(cantidad, espacio);

        // agrega el stock al deposito y lo guarda en el repositorio
        deposito.agregarAlStock(cantidadAGuardar);
        depositoRepository.save(deposito);

        StockDeposito stock = stockDepositoRepository
                .findByDepositoidAndProductoid(depositoId, productoId)
                .orElse(null);

        if (stock == null) {

            stock = new StockDeposito(depositoId, productoId, cantidadAGuardar);//no hay stock del producto en ese deposito

        } else {

            stock.setCantidad(stock.getCantidad() + cantidadAGuardar);//si hay stock del producto en ese deposito

        }
        stockDepositoRepository.save(stock);
        metricasService.incrementarStockMovimiento("alta");

        if (cantidadAGuardar < cantidad) {
            System.out.println("Producto guardado en deposito, sobrante descartado");
        } else {
            System.out.println("Producto guardado en deposito sin sobrante");
        }
    }

    public void setAlgoritmoMM(String depositoid, LogisticaDTOs.TipoAlgoritmoEnum algoritmo){
        Deposito deposito = buscarDepositoID(depositoid);
        deposito.setAlgoritmo(algoritmo);
        depositoRepository.save(deposito);
    }

    @Override
    public LogisticaDTOs.GestionDonacionResponseDTO gestionarDonacion(String depositoid, String donacionid, String productoid, Integer cantidad) {

        //  validad cantidad
        if (cantidad <= 0) {
            return new LogisticaDTOs.GestionDonacionResponseDTO(
                    "Cantidad insuficiente, no se encoló",
                    buscarDepositoIDDTO(depositoid),
                    null
            );
        }

        // existencia deposito
        Deposito deposito = buscarDepositoID(depositoid);
        if (deposito == null) {
            return new LogisticaDTOs.GestionDonacionResponseDTO(
                    "Deposito id: " + depositoid + " no encontrado",
                    null,
                    null
            );
        }

        // manda al worker
        DonacionMensaje mensaje = new DonacionMensaje(depositoid, donacionid, productoid, cantidad);
        rabbitTemplate.convertAndSend(RabbitMQConfig.COLA_DONACIONES, mensaje);
        metricasService.incrementarDonacionesEncoladas();

        // respuesta
        return new LogisticaDTOs.GestionDonacionResponseDTO(
                "Donación encolada, será procesada por un worker",
                buscarDepositoIDDTO(depositoid),
                null
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

    @Transactional
    public void procesarDonacionDesdeCola(String depositoid, String donacionid, String productoid, Integer cantidad) {

        // crea el paquete
        LogisticaDTOs.PaqueteDTO paqueteMatch = new LogisticaDTOs.PaqueteDTO(
                "paq-" + donacionid,
                donacionid,
                productoid,
                cantidad
        );

        // busca el depósito
        Deposito deposito = buscarDepositoID(depositoid);
        if (deposito == null) {
            System.out.println("[WORKER] Deposito no encontrado: " + depositoid);
            return;
        }

        //para que no sea cree mas de una asignacion con el mismo paquete y misma necesidad
        if (asignacionRepository.existsByPaqueteid(paqueteMatch.paqueteid())) {
            System.out.println("[WORKER] La donacion " + donacionid + " ya fue procesada, se ignora");
            metricasService.incrementarAsignacionesDuplicadas();
            return;
        }

        // consulta necesidades a DonadoresYEntidades
        List<DonacionYEntiDTOs.NecesidadMaterialDTO> necesidadesDelProducto =
                donadoresYEntidadesClient.obtenerNecesidadesConCantidad(productoid);

        // caso: sin necesidades guarda en stock
        if (necesidadesDelProducto == null || necesidadesDelProducto.isEmpty()) {
            agregarAlStock(depositoid, productoid, cantidad);
            System.out.println("[WORKER] Sin necesidades, guardado en stock: " + depositoid);
            return;
        }

        // filtra las recurrentes insuficientes
        List<DonacionYEntiDTOs.NecesidadMaterialDTO> listaFiltrada =
                necesidadesDelProducto.stream()
                        .filter(n -> {
                            boolean noAlcanza = cantidad < (n.cantidadObjetivo() - n.cantidadActual());
                            boolean esRecurrente = n.tipo() == DonacionYEntiDTOs.TipoNecesidadMaterialEnum.RECURRENTE;
                            return !(noAlcanza && esRecurrente);
                        })
                        .collect(Collectors.toList());

        // caso: todas eran recurrentes insuficientes van al stock
        if (listaFiltrada.isEmpty()) {
            agregarAlStock(depositoid, productoid, cantidad);
            System.out.println("[WORKER] Solo recurrentes insuficientes, guardado en stock");
            return;
        }

        // ejecuta el matchmaking
        LogisticaDTOs.AsignacionDTO asignacion = ejecutarMatchmaking(depositoid, paqueteMatch, listaFiltrada);
        if (asignacion == null) {
            System.out.println("[WORKER] Matchmaking no devolvió asignación");
            return;
        }

        // busca la necesidad elegida para calcular cuánto se asigna y cuánto sobra
        final String necesidadIdBuscada = asignacion.necesidadid();
        DonacionYEntiDTOs.NecesidadMaterialDTO necesidadElegida =
                necesidadesDelProducto.stream()
                        .filter(n -> n.necesidadid().equals(necesidadIdBuscada))
                        .findFirst()
                        .orElse(null);

        // cantidad que realmente se asigna a la necesidad y sobrante que va al stock
        int cantidadAsignada = cantidad;
        int sobrante = 0;
        if (necesidadElegida != null) {
            int cantidadNecesaria = Math.max(0, necesidadElegida.cantidadObjetivo() - necesidadElegida.cantidadActual());
            if (cantidad >= cantidadNecesaria) {
                // alcanza (o sobra): se asigna lo necesario y el resto va al stock
                cantidadAsignada = cantidadNecesaria;
                sobrante = cantidad - cantidadNecesaria;
            } else {
                // donación insuficiente pero EXTRAORDINARIA → se asigna lo donado
                cantidadAsignada = cantidad;
            }
        }

        // guarda la asignación con la cantidad efectivamente asignada (no la del paquete)
        LogisticaDTOs.AsignacionDTO asignacionFinal = new LogisticaDTOs.AsignacionDTO(
                asignacion.asignacionid(),
                asignacion.paqueteid(),
                asignacion.necesidadid(),
                asignacion.fecha(),
                asignacion.estado(),
                asignacion.origen(),
                donacionid,
                productoid,
                cantidadAsignada
        );
        Asignacion nuevaAsignacion = new Asignacion(asignacionFinal);
        asignacionRepository.save(nuevaAsignacion);
        metricasService.incrementarAsignacionesCreadas();

        if (sobrante > 0) {
            agregarAlStock(depositoid, productoid, sobrante);
            System.out.println("[WORKER] Asignación creada. Sobrante de " + sobrante + " al stock");
        } else {
            System.out.println("[WORKER] Asignación creada. Cantidad asignada: " + cantidadAsignada);
        }
    }

    public LogisticaDTOs.ReporteEntregaResponseDTO reportarEntrega(String paqueteid) {

        Asignacion asignacion = buscarAsignacionPorPaqueteID(paqueteid);
        if (asignacion == null) {
            throw new NoSuchElementException("No existe asignación para el paquete: " + paqueteid);
        }

        // lo que se entrega es de la asignación guardada, no del body.
        String donacionId = asignacion.getDonacionid();
        Integer cantidadAEntregar = asignacion.getCantidad();

        asignacion.setEstado(LogisticaDTOs.EstadoAsginacionEnum.COMPLETADA);
        asignacionRepository.save(asignacion);

        // satisface la necesidad con la cantidad que realmente se le asignó
        if (cantidadAEntregar != null && cantidadAEntregar > 0) {
            try {
                donadoresYEntidadesClient.satisfacerNecesidad(asignacion.getNecesidadId(), cantidadAEntregar);
                metricasService.incrementarNecesidadesSatisfechas();
            } catch (Exception e) {
                System.out.println("No se pudo satisfacer la necesidad " + asignacion.getNecesidadId() + ": " + e.getMessage());
            }
        }

        // cambia el estado de la donación (solo si la asignación vino de una donación real)
        if (donacionId != null && !donacionId.isBlank()) {
            try {
                donacionesClient.cambiarEstadoDeDonacion(
                        donacionId,
                        ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum.ACEPTADA);
            } catch (Exception e) {
                System.out.println("No se pudo cambiar el estado de la donación " + donacionId + ": " + e.getMessage());
            }
        }

        return new LogisticaDTOs.ReporteEntregaResponseDTO(
                donacionId != null ? "Donación aceptada" : "Asignación entregada (sin donación asociada)",
                donacionId,
                "Asignación completada",
                asignacion.getId()
        );
    }


    public LogisticaDTOs.AsignacionDTO altaAsignacion(LogisticaDTOs.AsignacionDTO asignacionDTO) {
        Asignacion nuevaAsignacion = new Asignacion(asignacionDTO);
        asignacionRepository.save(nuevaAsignacion);
        return asignacionDTO;
    }

    public Integer stockDisponibleDeProducto(String productoId) {
        List<StockDeposito> stocks = stockDepositoRepository.findByProductoid(productoId);
        return stocks.stream()
                .mapToInt(StockDeposito::getCantidad)
                .sum();
    }

    // crea asignaciones de necesidad con lo que hay en stock
    @Transactional
    public LogisticaDTOs.AsignacionDTO asignarPorSolicitud(String necesidadID, String productoID, Integer cantidad) {

        // verifica que haya stock suficiente del producto
        Integer disponible = stockDisponibleDeProducto(productoID);
        if (disponible < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + disponible + ", solicitado: " + cantidad);
        }

        // descuenta del stock (de los depósitos que tengan ese producto)
        descontarStock(productoID, cantidad);

        // crea la asignación con origen solicitud donadores
        LogisticaDTOs.AsignacionDTO asignacionDTO = new LogisticaDTOs.AsignacionDTO(
                java.util.UUID.randomUUID().toString(),
                "paq-solicitud-" + necesidadID,
                necesidadID,
                java.time.LocalDateTime.now(),
                LogisticaDTOs.EstadoAsginacionEnum.ASIGNADA,
                LogisticaDTOs.OrigenAsignacionEnum.SOLICITUD_DONADORES,
                null,
                productoID,
                cantidad
        );

        Asignacion nuevaAsignacion = new Asignacion(asignacionDTO);
        asignacionRepository.save(nuevaAsignacion);

        return asignacionDTO;
    }

    public LogisticaDTOs.StockDetalladoDTO stockDetalladoDeProducto(String productoID) {
        List<StockDeposito> stocks = stockDepositoRepository.findByProductoid(productoID);

        List<LogisticaDTOs.StockPorDepositoDTO> porDeposito = stocks.stream()
                .filter(s -> s.getCantidad() != null && s.getCantidad() > 0)
                .map(s -> new LogisticaDTOs.StockPorDepositoDTO(s.getDepositoid(), s.getCantidad()))
                .collect(Collectors.toList());

        int total = porDeposito.stream()
                .mapToInt(LogisticaDTOs.StockPorDepositoDTO::disponible)
                .sum();

        return new LogisticaDTOs.StockDetalladoDTO(productoID, porDeposito, total);
    }

    public java.util.List<LogisticaDTOs.StockDetalladoDTO> stockDeTodosLosProductos() {
        return stockDepositoRepository.findAll().stream()
                .filter(s -> s.getCantidad() != null && s.getCantidad() > 0)
                .collect(Collectors.groupingBy(StockDeposito::getProductoid))
                .entrySet().stream()
                .map(e -> {
                    List<LogisticaDTOs.StockPorDepositoDTO> porDep = e.getValue().stream()
                            .map(s -> new LogisticaDTOs.StockPorDepositoDTO(s.getDepositoid(), s.getCantidad()))
                            .collect(Collectors.toList());
                    int total = porDep.stream().mapToInt(LogisticaDTOs.StockPorDepositoDTO::disponible).sum();
                    return new LogisticaDTOs.StockDetalladoDTO(e.getKey(), porDep, total);
                })
                .collect(Collectors.toList());
    }

    public void limpiarTodaLaBase() {
        stockDepositoRepository.deleteAll();
        asignacionRepository.deleteAll();
        necesidaddematerialRepository.deleteAll();
        depositoRepository.deleteAll();
        System.out.println("Base de datos de Logística reseteada por completo.");
    }

}


