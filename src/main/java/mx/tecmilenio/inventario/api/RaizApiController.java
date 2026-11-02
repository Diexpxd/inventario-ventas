package mx.tecmilenio.inventario.api;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import mx.tecmilenio.inventario.excepcion.RecursoNoEncontradoException;
import mx.tecmilenio.inventario.modelo.Recurso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashSet;
import java.util.Set;

/** Punto de entrada de la API: lista los recursos disponibles como enlaces. */
@RestController
public class RaizApiController {

    @JacksonXmlRootElement(localName = "api")
    public static class Indice extends Recurso {
        public String getNombre() {
            return "API de Inventario y Ventas";
        }

        public String getVersion() {
            return "1.0.0";
        }
    }

    private final Enlaces enlaces;
    private final RequestMappingHandlerMapping mapeo;

    @Autowired
    public RaizApiController(Enlaces enlaces, RequestMappingHandlerMapping mapeo) {
        this.enlaces = enlaces;
        this.mapeo = mapeo;
    }

    @RequestMapping(value = "/api", method = RequestMethod.GET)
    public Indice raiz() {
        return enlaces.raiz(new Indice());
    }

    /**
     * Cualquier otra ruta de /api cae aqui. Si la ruta existe pero con otro metodo HTTP
     * responde 405; si no existe, 404. En los dos casos en JSON o XML.
     */
    @RequestMapping("/api/**")
    public void noExiste(HttpServletRequest req) throws HttpRequestMethodNotSupportedException {
        Set<String> admitidos = new LinkedHashSet<String>();
        for (RequestMappingInfo info : mapeo.getHandlerMethods().keySet()) {
            if (info.getPatternsCondition().getPatterns().contains("/api/**")) {
                continue;
            }
            if (info.getPatternsCondition().getMatchingCondition(req) != null) {
                for (RequestMethod m : info.getMethodsCondition().getMethods()) {
                    admitidos.add(m.name());
                }
            }
        }
        if (!admitidos.isEmpty()) {
            throw new HttpRequestMethodNotSupportedException(req.getMethod(), admitidos);
        }
        throw new RecursoNoEncontradoException("Ese recurso no existe");
    }
}
