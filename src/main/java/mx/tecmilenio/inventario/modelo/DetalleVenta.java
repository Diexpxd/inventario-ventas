package mx.tecmilenio.inventario.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/** Renglon de una venta: producto, cantidad y precio al momento de vender. */
public class DetalleVenta {

    private Long id;
    private Producto producto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public BigDecimal calcularSubtotal() {
        subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        return subtotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnoreProperties({"links", "descripcion", "precio", "stock", "stockMinimo", "activo",
            "createdAt", "updatedAt", "categoria", "proveedor", "necesitaReorden"})
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
