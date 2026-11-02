package mx.tecmilenio.inventario.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

/** Datos de una venta nueva que llegan en la peticion: cliente y lista de lineas. */
@JacksonXmlRootElement(localName = "venta")
public class VentaSolicitud {

    private String cliente;
    private List<LineaVenta> detalles = new ArrayList<>();

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    @JacksonXmlElementWrapper(localName = "detalles")
    @JacksonXmlProperty(localName = "detalle")
    public List<LineaVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<LineaVenta> detalles) {
        this.detalles = detalles;
    }
}
