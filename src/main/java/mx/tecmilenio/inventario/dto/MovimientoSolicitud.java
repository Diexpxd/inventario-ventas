package mx.tecmilenio.inventario.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import mx.tecmilenio.inventario.modelo.TipoMovimiento;

/** Datos de un movimiento de inventario (entrada, salida o ajuste) que llegan en la peticion. */
@JacksonXmlRootElement(localName = "movimiento")
public class MovimientoSolicitud {

    private Long idProducto;
    private TipoMovimiento tipo;
    private int cantidad;
    private String referencia;

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
