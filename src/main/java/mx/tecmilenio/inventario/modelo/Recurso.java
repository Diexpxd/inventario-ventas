package mx.tecmilenio.inventario.modelo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Base de todo lo que la API regresa como recurso. Guarda los enlaces hipermedia
 * que el controlador REST le agrega antes de responder.
 */
public abstract class Recurso {

    private List<Enlace> links = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JacksonXmlElementWrapper(localName = "links")
    @JacksonXmlProperty(localName = "link")
    public List<Enlace> getLinks() {
        return links;
    }

    public void setLinks(List<Enlace> links) {
        this.links = links;
    }

    public void agregarEnlace(String rel, String href, String metodo) {
        links.add(new Enlace(rel, href, metodo));
    }
}
