package mx.tecmilenio.inventario.controlador;

import mx.tecmilenio.inventario.excepcion.AppException;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.seguridad.SeguridadInterceptor;
import mx.tecmilenio.inventario.servicio.InventarioService;
import mx.tecmilenio.inventario.servicio.ProductoService;
import mx.tecmilenio.inventario.servicio.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/** Login, logout y el dashboard con los numeros del dia. */
@Controller
public class InicioController {

    private final mx.tecmilenio.inventario.servicio.UsuarioService usuarios;
    private final ProductoService productos;
    private final InventarioService inventario;
    private final VentaService ventas;

    @Autowired
    public InicioController(mx.tecmilenio.inventario.servicio.UsuarioService usuarios, ProductoService productos,
                            InventarioService inventario, VentaService ventas) {
        this.usuarios = usuarios;
        this.productos = productos;
        this.inventario = inventario;
        this.ventas = ventas;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String inicio() {
        return "redirect:/dashboard";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String formulario() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String entrar(@RequestParam String username, @RequestParam String password,
                         @RequestParam(value = "recordar", required = false) String recordar,
                         HttpServletRequest req, Model model) {
        try {
            Usuario u = usuarios.autenticar(username, password);
            req.getSession().invalidate();
            HttpSession sesion = req.getSession(true);
            sesion.setAttribute(SeguridadInterceptor.SESION, u);
            if (recordar != null) {
                sesion.setMaxInactiveInterval(7 * 24 * 60 * 60);
            }
            return "redirect:/dashboard";
        } catch (AppException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            return "login";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String salir(HttpSession sesion) {
        sesion.invalidate();
        return "redirect:/login";
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(@RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u, Model model) {
        model.addAttribute("totalProductos", productos.contarActivos());
        model.addAttribute("bajoMinimo", inventario.productosBajoMinimo());
        model.addAttribute("ventasHoy", ventas.totalDeHoy(u));
        model.addAttribute("ultimasVentas", ventas.historial(u, 5));
        return "dashboard";
    }
}
