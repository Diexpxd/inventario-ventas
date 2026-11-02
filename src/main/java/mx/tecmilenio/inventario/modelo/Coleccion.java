package mx.tecmilenio.inventario.modelo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/** Envoltorio para listas, asi el XML y el JSON tienen la misma forma y se pueden poner enlaces. */
@JacksonXmlRootElement(localName = "coleccion")
public class Coleccion<T> extends Recurso {

    private List<T> elementos;

    public Coleccion() {
    }

    public Coleccion(List<T> elementos) {
        this.elementos = elementos;
    }

    public int getTotal() {
        return elementos == null ? 0 : elementos.size();
    }

    @JacksonXmlElementWrapper(localName = "elementos")
    @JacksonXmlProperty(localName = "elemento")
    public List<T> getElementos() {
        return elementos;
    }

    public void setElementos(List<T> elementos) {
        this.elementos = elementos;
    }
}
