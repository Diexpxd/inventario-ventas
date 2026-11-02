package mx.tecmilenio.inventario.modelo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/** Un enlace hipermedia: que relacion tiene, a donde apunta y con que metodo HTTP se usa. */
public class Enlace {

    private String rel;
    private String href;
    private String method;

    public Enlace() {
    }

    public Enlace(String rel, String href, String method) {
        this.rel = rel;
        this.href = href;
        this.method = method;
    }

    @JacksonXmlProperty(isAttribute = true)
    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    @JacksonXmlProperty(isAttribute = true)
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @JacksonXmlProperty(isAttribute = true)
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
