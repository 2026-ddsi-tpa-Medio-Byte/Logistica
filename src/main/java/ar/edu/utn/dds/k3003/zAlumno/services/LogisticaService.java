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
import ar.edu.utn.dds.k3003.zAlumno.exceptions.IntegracionException;
import ar.edu.utn.dds.k3003.zAlumno.logging.TraceIdFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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

    private static final Logger log = LoggerFactory.getLogger(LogisticaService.class);

    // Algoritmo que se usa cuando un deposito no tiene uno configurado
    private static final LogisticaDTOs.TipoAlgoritmoEnum ALGORITMO_POR_DEFECTO =
            LogisticaDTOs.TipoAlgoritmoEnum.SUBATENDIDOS;

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
        validarDatosDeposito(depositoDTO.nombre(), depositoDTO.capacidadMaxima());

        int stockInicial = depositoDTO.stockActual() != null ? depositoDTO.stockActual() : 0;
        if (stockInicial < 0 || stockInicial > depositoDTO.capacidadMaxima()) {
            throw new IllegalArgumentException(
                    "El stock inicial debe estar entre 0 y la capacidad maxima (" + depositoDTO.capacidadMaxima() + ")");
        }

        String id;
        if (depositoDTO.depositoid() != null && !depositoDTO.depositoid().isBlank()) {
            id = depositoDTO.depositoid().trim();
            if (depositoRepository.existsById(id)) {
                throw new IllegalStateException("Ya existe un deposito con id " + id);
            }
        } else {
            id = siguienteIdDeposito();
        }

        LogisticaDTOs.DepositoDTO dtoConId = new LogisticaDTOs.DepositoDTO(
                depositoDTO.nombre().trim(),
                id,
                depositoDTO.direccion(),
                depositoDTO.capacidadMaxima(),
                stockInicial,
                depositoDTO.algoritmo() != null ? depositoDTO.algoritmo() : LogisticaDTOs.TipoAlgoritmoEnum.NULL
        );

        depositoRepository.save(new Deposito(dtoConId));
        log.info("Deposito {} creado (capacidad={}, algoritmo={})", id, dtoConId.capacidadMaxima(), dtoConId.algoritmo());
        return buscarDepositoIDDTO(id);
    }

    // Busca el primer DEP-UTN-XX libre. Con count+1 se podia repetir un id ya usado
    // (por ejemplo, despues de borrar un deposito) y el save lo pisaba.
    private String siguienteIdDeposito() {
        long numero = depositoRepository.countByDepositoidStartingWith("DEP-UTN-") + 1;
        String id = String.format("DEP-UTN-%02d", numero);
        while (depositoRepository.existsById(id)) {
            numero++;
            id = String.format("DEP-UTN-%02d", numero);
        }
        return id;
    }

    private void validarDatosDeposito(String nombre, Integer capacidadMaxima) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del deposito es obligatorio");
        }
        if (capacidadMaxima == null || capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad maxima debe ser mayor a 0");
        }
    }

    // ---------- 18: baja ----------
    @Override
    @Transactional
    public void eliminarDeposito(String depositoid) {
        Deposito deposito = buscarDepositoID(depositoid);
        if (deposito == null) {
            throw new NoSuchElementException("Deposito no encontrado: " + depositoid);
        }
        int stock = deposito.getStockActual() != null ? deposito.getStockActual() : 0;
        if (stock > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar el deposito " + depositoid + ": todavia tiene " + stock + " unidades en stock");
        }
        // se borran las filas de stock en 0 que quedaron de ese deposito, para no dejar huerfanos
        stockDepositoRepository.deleteAll(stockDepositoRepository.findByDepositoid(depositoid));
        depositoRepository.delete(deposito);
        log.info("Deposito {} eliminado", depositoid);
    }

    // ---------- 18: modificacion ----------
    // Se modifica la entidad existente (no se crea una nueva): el id sale de la URL, el stock
    // no se toca porque lo maneja el sistema, y se respeta el @Version del deposito.
    @Override
    @Transactional
    public LogisticaDTOs.DepositoDTO modificarDeposito(String depositoid, LogisticaDTOs.DepositoDTO nuevosDatos) {
        Deposito deposito = buscarDepositoID(depositoid);
        if (deposito == null) {
            throw new NoSuchElementException("Deposito no encontrado: " + depositoid);
        }
        validarDatosDeposito(nuevosDatos.nombre(), nuevosDatos.capacidadMaxima());

        int stock = deposito.getStockActual() != null ? deposito.getStockActual() : 0;
        if (nuevosDatos.capacidadMaxima() < stock) {
            throw new IllegalArgumentException(
                    "La capacidad maxima no puede ser menor al stock actual (" + stock + ")");
        }

        deposito.setNombre(nuevosDatos.nombre().trim());
        deposito.setDireccion(nuevosDatos.direccion());
        deposito.setCapacidadMaxima(nuevosDatos.capacidadMaxima());
        if (nuevosDatos.algoritmo() != null) {
            deposito.setAlgoritmo(nuevosDatos.algoritmo());
        }
        depositoRepository.save(deposito);
        log.info("Deposito {} modificado", depositoid);
        return buscarDepositoIDDTO(depositoid);
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

            int cantidadStock = stock.getCantidad() != null ? stock.getCantidad() : 0;
            int aDescontar = Math.min(cantidadStock, restante);
            stock.setCantidad(cantidadStock - aDescontar);
            stockDepositoRepository.save(stock);

            // actualiza el depósito
            Deposito deposito = buscarDepositoID(stock.getDepositoid());
            if (deposito != null) {
                int stockActual = deposito.getStockActual() != null ? deposito.getStockActual() : 0;
                deposito.setStockActual(stockActual - aDescontar);
                depositoRepository.save(deposito);
            }
            metricasService.incrementarStockMovimiento("baja");

            restante -= aDescontar;
        }
    }

    @Override
    public void agregarAlStock(String depositoId, String productoId, Integer cantidad){

        Deposito deposito = buscarDepositoID(depositoId);
        if (deposito == null) {
            log.warn("[STOCK] Deposito {} no encontrado, se descartan {} unidades de {}", depositoId, cantidad, productoId);
            metricasService.incrementarUnidadesDescartadas(depositoId, cantidad);
            return;
        }

        if(deposito.estaLleno()){
            log.warn("[STOCK] Deposito {} lleno, se descartan {} unidades de {}", depositoId, cantidad, productoId);
            metricasService.incrementarUnidadesDescartadas(depositoId, cantidad);
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
            int descartadas = cantidad - cantidadAGuardar;
            log.warn("[STOCK] Deposito {}: se guardaron {} unidades de {} y se descartaron {} por falta de espacio",
                    depositoId, cantidadAGuardar, productoId, descartadas);
            metricasService.incrementarUnidadesDescartadas(depositoId, descartadas);
        } else {
            log.info("[STOCK] Deposito {}: se guardaron {} unidades de {}", depositoId, cantidadAGuardar, productoId);
        }
    }

    public void setAlgoritmoMM(String depositoid, LogisticaDTOs.TipoAlgoritmoEnum algoritmo){
        Deposito deposito = buscarDepositoID(depositoid);
        if (deposito == null) {
            throw new NoSuchElementException("Depósito no encontrado: " + depositoid);
        }
        deposito.setAlgoritmo(algoritmo);
        depositoRepository.save(deposito);
        log.info("Deposito {} configurado con algoritmo {}", depositoid, algoritmo);
    }

    @Override
    public LogisticaDTOs.GestionDonacionResponseDTO gestionarDonacion(String depositoid, String donacionid, String productoid, Integer cantidad) {

        //  validad cantidad
        if (cantidad == null || cantidad <= 0) {
            log.warn("Donacion {} rechazada: cantidad invalida ({})", donacionid, cantidad);
            return new LogisticaDTOs.GestionDonacionResponseDTO(
                    "Cantidad insuficiente, no se encoló",
                    buscarDepositoIDDTO(depositoid),
                    null
            );
        }

        // existencia deposito
        Deposito deposito = buscarDepositoID(depositoid);
        if (deposito == null) {
            log.warn("Donacion {} rechazada: deposito {} no existe", donacionid, depositoid);
            return new LogisticaDTOs.GestionDonacionResponseDTO(
                    "Deposito id: " + depositoid + " no encontrado",
                    null,
                    null
            );
        }

        // manda al worker
        // el traceId del request viaja como header del mensaje para que el worker lo recupere
        DonacionMensaje mensaje = new DonacionMensaje(depositoid, donacionid, productoid, cantidad);
        rabbitTemplate.convertAndSend(RabbitMQConfig.COLA_DONACIONES, mensaje, m -> {
            String traceId = MDC.get(TraceIdFilter.MDC_TRACE_ID);
            if (traceId != null) {
                m.getMessageProperties().setHeader(TraceIdFilter.TRACE_ID_HEADER, traceId);
            }
            return m;
        });
        metricasService.incrementarDonacionesEncoladas();
        log.info("Donacion {} encolada (deposito={}, producto={}, cantidad={})", donacionid, depositoid, productoid, cantidad);

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
        if (algoritmoConfigurado == null || algoritmoConfigurado == LogisticaDTOs.TipoAlgoritmoEnum.NULL) {
            log.warn("Deposito {} sin algoritmo de matchmaking configurado, se usa {} por defecto",
                    depositoid, ALGORITMO_POR_DEFECTO);
            algoritmoConfigurado = ALGORITMO_POR_DEFECTO;
        }
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
            log.warn("[WORKER] Donacion {}: deposito {} no encontrado, no se procesa", donacionid, depositoid);
            return;
        }

        //para que no sea cree mas de una asignacion con el mismo paquete y misma necesidad
        if (asignacionRepository.existsByPaqueteid(paqueteMatch.paqueteid())) {
            log.warn("[WORKER] Donacion {} ya tenia asignacion (paquete {}), se ignora", donacionid, paqueteMatch.paqueteid());
            metricasService.incrementarAsignacionesDuplicadas();
            return;
        }

        // consulta necesidades a DonadoresYEntidades
        List<DonacionYEntiDTOs.NecesidadMaterialDTO> necesidadesDelProducto;
        try {
            necesidadesDelProducto = donadoresYEntidadesClient.obtenerNecesidadesConCantidad(productoid);
        } catch (Exception e) {
            metricasService.incrementarFalloIntegracion("donadoresyentidades", "obtener_necesidades");
            log.error("[WORKER] Donacion {}: no se pudieron obtener las necesidades del producto {}", donacionid, productoid, e);
            throw e; // se relanza para que RabbitMQ reintente
        }
        log.info("[WORKER] Donacion {}: {} necesidades encontradas para el producto {}",
                donacionid, necesidadesDelProducto == null ? 0 : necesidadesDelProducto.size(), productoid);

        // descuenta lo ya asignado y todavia no entregado, para no cubrir dos veces lo mismo
        necesidadesDelProducto = ajustarPorAsignacionesPendientes(necesidadesDelProducto);

        // caso: sin necesidades guarda en stock
        if (necesidadesDelProducto == null || necesidadesDelProducto.isEmpty()) {
            agregarAlStock(depositoid, productoid, cantidad);
            metricasService.incrementarDonacionGestionada("stock");
            log.info("[WORKER] Donacion {}: sin necesidades, se guarda en stock del deposito {}", donacionid, depositoid);
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
            metricasService.incrementarDonacionGestionada("stock");
            log.info("[WORKER] Donacion {}: solo hay necesidades recurrentes que no alcanza a cubrir, se guarda en stock", donacionid);
            return;
        }

        // ejecuta el matchmaking
        LogisticaDTOs.AsignacionDTO asignacion = ejecutarMatchmaking(depositoid, paqueteMatch, listaFiltrada);
        if (asignacion == null) {
            log.warn("[WORKER] Donacion {}: el matchmaking no devolvio asignacion", donacionid);
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
        metricasService.incrementarDonacionGestionada("asignada");

        if (sobrante > 0) {
            agregarAlStock(depositoid, productoid, sobrante);
            log.info("[WORKER] Donacion {}: asignacion {} creada para necesidad {} con {} unidades, sobrante de {} al stock",
                    donacionid, asignacionFinal.asignacionid(), asignacionFinal.necesidadid(), cantidadAsignada, sobrante);
        } else {
            log.info("[WORKER] Donacion {}: asignacion {} creada para necesidad {} con {} unidades",
                    donacionid, asignacionFinal.asignacionid(), asignacionFinal.necesidadid(), cantidadAsignada);
        }
    }

    // Suma a la cantidadActual de cada necesidad lo que ya esta ASIGNADO pero no entregado
    // (Donadores recien lo cuenta al reportar la entrega) y saca las que ya quedan cubiertas.
    private List<DonacionYEntiDTOs.NecesidadMaterialDTO> ajustarPorAsignacionesPendientes(
            List<DonacionYEntiDTOs.NecesidadMaterialDTO> necesidades) {
        if (necesidades == null) {
            return List.of();
        }
        return necesidades.stream()
                .map(n -> {
                    Long pendiente = asignacionRepository.sumarCantidadPorNecesidadYEstado(
                            n.necesidadid(), LogisticaDTOs.EstadoAsginacionEnum.ASIGNADA);
                    int actual = (n.cantidadActual() != null ? n.cantidadActual() : 0)
                            + (pendiente != null ? pendiente.intValue() : 0);
                    return new DonacionYEntiDTOs.NecesidadMaterialDTO(
                            n.necesidadid(), n.entidadid(), n.nivelDeUrgencia(), n.descripcion(),
                            n.cantidadObjetivo(), actual, n.productoSolicitadoid(), n.tipo());
                })
                .filter(n -> n.cantidadObjetivo() == null || n.cantidadActual() < n.cantidadObjetivo())
                .collect(Collectors.toList());
    }

    // Orden: 1) cambiar estado de la donacion (idempotente: repetirlo no hace dano)
    //        2) satisfacer la necesidad (NO idempotente: suma cantidad en Donadores)
    //        3) recien ahi marcar la asignacion COMPLETADA.
    // Si falla 1 o 2, la asignacion sigue ASIGNADA y se puede volver a reportar sin duplicar nada.
    @Transactional
    public LogisticaDTOs.ReporteEntregaResponseDTO reportarEntrega(String paqueteid) {

        Asignacion asignacion = buscarAsignacionPorPaqueteID(paqueteid);
        if (asignacion == null) {
            throw new NoSuchElementException("No existe asignación para el paquete: " + paqueteid);
        }

        String donacionId = asignacion.getDonacionid();
        Integer cantidadAEntregar = asignacion.getCantidad();

        // 1) estado de la donacion (solo si la asignacion vino de una donacion real)
        if (donacionId != null && !donacionId.isBlank()) {
            try {
                donacionesClient.cambiarEstadoDeDonacion(
                        donacionId,
                        ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum.ACEPTADA);
            } catch (Exception e) {
                metricasService.incrementarFalloIntegracion("donaciones", "cambiar_estado");
                log.error("Entrega del paquete {}: no se pudo cambiar el estado de la donacion {}", paqueteid, donacionId, e);
                throw new IntegracionException(
                        "No se pudo marcar como ACEPTADA la donacion " + donacionId + ". La entrega no se registro, reintentar.", e);
            }
        }

        // 2) satisfacer la necesidad con la cantidad que realmente se le asigno
        if (cantidadAEntregar != null && cantidadAEntregar > 0) {
            try {
                donadoresYEntidadesClient.satisfacerNecesidad(asignacion.getNecesidadId(), cantidadAEntregar);
                metricasService.incrementarNecesidadesSatisfechas();
            } catch (Exception e) {
                metricasService.incrementarFalloIntegracion("donadoresyentidades", "satisfacer_necesidad");
                log.error("Entrega del paquete {}: no se pudo satisfacer la necesidad {}", paqueteid, asignacion.getNecesidadId(), e);
                throw new IntegracionException(
                        "No se pudo satisfacer la necesidad " + asignacion.getNecesidadId() + ". La entrega no se registro, reintentar.", e);
            }
        }

        // 3) recien ahora se completa la asignacion
        asignacion.setEstado(LogisticaDTOs.EstadoAsginacionEnum.COMPLETADA);
        asignacionRepository.save(asignacion);

        log.info("Entrega reportada: paquete {}, asignacion {}, necesidad {}, {} unidades",
                paqueteid, asignacion.getId(), asignacion.getNecesidadId(), cantidadAEntregar);

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
                .mapToInt(s -> s.getCantidad() != null ? s.getCantidad() : 0)
                .sum();
    }

    // crea asignaciones de necesidad con lo que hay en stock
    @Transactional
    public LogisticaDTOs.AsignacionDTO asignarPorSolicitud(String necesidadID, String productoID, Integer cantidad) {
        return asignarPorSolicitud(necesidadID, productoID, cantidad, null);
    }

    // tipo es opcional: si Donadores manda EXTRAORDINARIA y no alcanza el stock, se asigna lo que haya.
    // Si no lo manda (o es RECURRENTE), se mantiene la regla de antes: todo o nada.
    @Transactional
    public LogisticaDTOs.AsignacionDTO asignarPorSolicitud(String necesidadID, String productoID, Integer cantidad, String tipo) {

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a 0");
        }

        Integer disponible = stockDisponibleDeProducto(productoID);
        boolean aceptaParcial = "EXTRAORDINARIA".equalsIgnoreCase(tipo);

        if (aceptaParcial && disponible > 0 && disponible < cantidad) {
            log.info("Necesidad {} EXTRAORDINARIA: se asignan {} de {} unidades pedidas de {} (stock parcial)",
                    necesidadID, disponible, cantidad, productoID);
            cantidad = disponible;
        }

        if (disponible < cantidad) {
            metricasService.incrementarSolicitudDirecta("sin_stock");
            log.warn("Solicitud de la necesidad {} rechazada: stock insuficiente de {} (disponible={}, pedido={})",
                    necesidadID, productoID, disponible, cantidad);
            throw new IllegalStateException("Stock insuficiente. Disponible: " + disponible + ", solicitado: " + cantidad);
        }

        // descuenta del stock (de los depósitos que tengan ese producto)
        descontarStock(productoID, cantidad);

        // crea la asignación con origen solicitud donadores
        LogisticaDTOs.AsignacionDTO asignacionDTO = new LogisticaDTOs.AsignacionDTO(
                java.util.UUID.randomUUID().toString(),
                "paq-solicitud-" + java.util.UUID.randomUUID(), // unico: puede haber varias solicitudes por necesidad
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

        metricasService.incrementarSolicitudDirecta("ok");
        log.info("Asignacion {} creada desde stock para la necesidad {}: {} unidades de {}",
                asignacionDTO.asignacionid(), necesidadID, cantidad, productoID);
        return asignacionDTO;
    }

    public LogisticaDTOs.StockDetalladoDTO stockDetalladoDeProducto(String productoID) {
        List<StockDeposito> stocks = stockDepositoRepository.findByProductoid(productoID);

        List<LogisticaDTOs.StockPorDepositoDTO> porDeposito = stocks.stream()
                .filter(s -> s.getCantidad() != null && s.getCantidad() > 0)
                .map(s -> new LogisticaDTOs.StockPorDepositoDTO(s.getDepositoid(), s.getCantidad()))
                .collect(Collectors.toList());

        int total = porDeposito.stream()
                .mapToInt(LogisticaDTOs.StockPorDepositoDTO::disponibleEnDeposito)
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
                    int total = porDep.stream().mapToInt(LogisticaDTOs.StockPorDepositoDTO::disponibleEnDeposito).sum();
                    return new LogisticaDTOs.StockDetalladoDTO(e.getKey(), porDep, total);
                })
                .collect(Collectors.toList());
    }

    // ---------- 19: consultas de asignaciones ----------
    public List<LogisticaDTOs.AsignacionDTO> listarAsignaciones(LogisticaDTOs.EstadoAsginacionEnum estado, String necesidadid) {
        List<Asignacion> asignaciones;
        if (necesidadid != null && !necesidadid.isBlank()) {
            asignaciones = asignacionRepository.findByNecesidadid(necesidadid);
        } else if (estado != null) {
            asignaciones = asignacionRepository.findByEstado(estado);
        } else {
            asignaciones = asignacionRepository.findAll();
        }
        return asignaciones.stream()
                .filter(a -> estado == null || a.getEstado() == estado)
                .map(this::aAsignacionDTO)
                .collect(Collectors.toList());
    }

    public List<LogisticaDTOs.AsignacionDTO> asignacionesDeDonacion(String donacionid) {
        return asignacionRepository.findByDonacionid(donacionid).stream()
                .map(this::aAsignacionDTO)
                .collect(Collectors.toList());
    }

    private LogisticaDTOs.AsignacionDTO aAsignacionDTO(Asignacion a) {
        return new LogisticaDTOs.AsignacionDTO(
                a.getId(), a.getpaqueteId(), a.getNecesidadId(), a.getfecha(), a.getEstado(),
                a.getOrigen(), a.getDonacionid(), a.getProductoid(), a.getCantidad());
    }

    // ---------- 20: stock de un deposito ----------
    public List<LogisticaDTOs.StockProductoDTO> stockDeDeposito(String depositoid) {
        if (!depositoRepository.existsById(depositoid)) {
            throw new NoSuchElementException("Deposito no encontrado: " + depositoid);
        }
        return stockDepositoRepository.findByDepositoid(depositoid).stream()
                .filter(st -> st.getCantidad() != null && st.getCantidad() > 0)
                .map(st -> new LogisticaDTOs.StockProductoDTO(st.getProductoid(), st.getCantidad()))
                .collect(Collectors.toList());
    }

    //Gauge stock actual
    public double stockTotalActual() {
        return depositoRepository.findAll().stream()
                .mapToInt(d -> d.getStockActual() != null ? d.getStockActual() : 0)
                .sum();
    }

    //Gauge ocupacion promedio
    public double ocupacionPromedio() {
        var depositos = depositoRepository.findAll();
        if (depositos.isEmpty()) {
            return 0.0;
        }
        double sumaPorcentajes = 0.0;
        int contados = 0;
        for (var d : depositos) {
            Integer cap = d.getCapacidadMaxima();
            if (cap == null || cap == 0) {
                continue; // evita división por cero
            }
            int stock = d.getStockActual() != null ? d.getStockActual() : 0;
            sumaPorcentajes += (stock * 100.0) / cap;
            contados++;
        }
        return contados == 0 ? 0.0 : sumaPorcentajes / contados;
    }

    public void limpiarTodaLaBase() {
        stockDepositoRepository.deleteAll();
        asignacionRepository.deleteAll();
        necesidaddematerialRepository.deleteAll();
        depositoRepository.deleteAll();
        log.warn("Base de datos de Logistica reseteada por completo");
    }

}