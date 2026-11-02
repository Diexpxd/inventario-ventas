package mx.tecmilenio.inventario.dto;

/** Una linea de la venta que llega en la peticion: producto y cantidad. */
public class LineaVenta {

    private Long idProducto;
    private int cantidad;

    public LineaVenta() {
    }

    public LineaVenta(Long idProducto, int cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
