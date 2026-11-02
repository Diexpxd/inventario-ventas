package mx.tecmilenio.inventario.seguridad;

import mx.tecmilenio.inventario.excepcion.AccesoDenegadoException;
import mx.tecmilenio.inventario.excepcion.NoAutenticadoException;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Revisa quien hace la peticion. Las pantallas usan la sesion que se abre en /login y la API
 * pide HTTP Basic en cada llamada. Despues revisa que el rol alcance para el modulo.
 */
public class SeguridadInterceptor extends HandlerInterceptorAdapter {

    public static final String SESION = "usuario";
    public static final String ATRIBUTO = "usuarioActual";

    private final UsuarioService usuarios;

    @Autowired
    public SeguridadInterceptor(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String ruta = req.getRequestURI().substring(req.getContextPath().length());
        boolean api = ruta.startsWith("/api");
        if (!api && (ruta.equals("/login") || ruta.startsWith("/recursos"))) {
            return true;
        }
        Usuario u = api ? porBasic(req) : porSesion(req);
        if (u == null) {
            if (api) {
                throw new NoAutenticadoException("Hace falta autenticarse con HTTP Basic");
            }
            res.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        String modulo = Permisos.modulo(ruta, req.getMethod());
        if (modulo != null && !u.tienePermiso(modulo)) {
            throw new AccesoDenegadoException("El rol " + u.getRol() + " no tiene acceso a este modulo");
        }
        req.setAttribute(ATRIBUTO, u);
        return true;
    }

    private Usuario porSesion(HttpServletRequest req) {
        return req.getSession(false) == null ? null : (Usuario) req.getSession(false).getAttribute(SESION);
    }

    private Usuario porBasic(HttpServletRequest req) {
        String h = req.getHeader("Authorization");
        if (h == null || !h.regionMatches(true, 0, "Basic ", 0, 6)) {
            return null;
        }
        String[] partes;
        try {
            partes = new String(Base64.getDecoder().decode(h.substring(6).trim()), StandardCharsets.UTF_8).split(":", 2);
        } catch (IllegalArgumentException e) {
            throw new NoAutenticadoException("La cabecera Authorization no esta bien formada");
        }
        if (partes.length < 2) {
            throw new NoAutenticadoException("La cabecera Authorization no esta bien formada");
        }
        return usuarios.autenticar(partes[0], partes[1]);
    }
}
