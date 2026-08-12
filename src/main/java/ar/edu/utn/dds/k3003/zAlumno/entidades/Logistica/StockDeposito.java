package ar.edu.utn.dds.k3003.zAlumno.entidades.Logistica;

import jakarta.persistence.*;

@Entity
@Table(name = "stock_deposito")
public class StockDeposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String depositoid;
    private String productoid;
    private Integer cantidad;

    public StockDeposito() {
    }

    public StockDeposito(String depositoid, String productoid, Integer cantidad) {
        this.depositoid = depositoid;
        this.productoid = productoid;
        this.cantidad = cantidad;
    }

    public Long getId() { return id; }
    public String getDepositoid() { return depositoid; }
    public String getProductoid() { return productoid; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}