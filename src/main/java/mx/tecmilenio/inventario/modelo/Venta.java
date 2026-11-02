package mx.tecmilenio.inventario.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Venta con sus detalles, total, vendedor y estado. */
public class Venta extends Recurso {

    private Long id;
    private String folio;
    private LocalDateTime fecha;
    private String cliente;
    private BigDecimal total = BigDecimal.ZERO;
    private EstadoVenta estado = EstadoVenta.PENDIENTE;
    private Usuario vendedor;
    private List<DetalleVenta> detalles = new ArrayList<>();

    public void agregarDetalle(DetalleVenta detalle) {
        detalle.calcularSubtotal();
        detalles.add(detalle);
    }

    public BigDecimal calcularTotal() {
        total = BigDecimal.ZERO;
        for (DetalleVenta d : detalles) {
            total = total.add(d.calcularSubtotal());
        }
        return total;
    }

    public void cancelar() {
        if (estado == EstadoVenta.CANCELADA) {
            throw new IllegalStateException("La venta ya estaba cancelada");
        }
        estado = EstadoVenta.CANCELADA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    @JsonIgnoreProperties({"links", "email", "rol", "activo", "createdAt"})
    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }

    @JacksonXmlElementWrapper(localName = "detalles")
    @JacksonXmlProperty(localName = "detalle")
    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
}
