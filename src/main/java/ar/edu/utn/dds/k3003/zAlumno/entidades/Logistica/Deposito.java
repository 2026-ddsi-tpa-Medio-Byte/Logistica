package ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica;


import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs.DepositoDTO;
import ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica.LogisticaDTOs.TipoAlgoritmoEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "depositos")
public class Deposito {

    @Id
    private String depositoid;
    private String nombre;
    private String direccion;
    private Integer capacidadMaxima;
    private Integer stockActual;
    @Enumerated(EnumType.STRING)
    private TipoAlgoritmoEnum algoritmo;

    public Deposito() {
    }

    public Deposito(DepositoDTO dto) {
        this.depositoid = dto.depositoid();
        this.nombre = dto.nombre();
        this.direccion = dto.direccion();
        this.capacidadMaxima = dto.capacidadMaxima();
        this.stockActual = dto.stockActual();
        this.algoritmo = dto.algoritmo();
    }

    public void setAlgoritmo(TipoAlgoritmoEnum algoritmo) { this.algoritmo = algoritmo; }
    public TipoAlgoritmoEnum getAlgoritmo() { return algoritmo; }
    public String getId() { return depositoid; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public Integer getCapacidadMaxima() { return capacidadMaxima; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockactual) { this.stockActual = stockactual; }
    public void agregarAlStock(Integer cantidad) { this.stockActual = this.stockActual + cantidad; }
    public boolean estaLleno() { return stockActual >= capacidadMaxima; }
    public int espacioDisponible() { return capacidadMaxima - stockActual; }
}
