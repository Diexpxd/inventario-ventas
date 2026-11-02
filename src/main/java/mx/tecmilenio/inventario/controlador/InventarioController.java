package mx.tecmilenio.inventario.controlador;

import mx.tecmilenio.inventario.dto.MovimientoSolicitud;
import mx.tecmilenio.inventario.excepcion.AppException;
import mx.tecmilenio.inventario.modelo.TipoMovimiento;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.seguridad.SeguridadInterceptor;
import mx.tecmilenio.inventario.servicio.InventarioService;
import mx.tecmilenio.inventario.servicio.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Movimientos de inventario y alertas de stock bajo. */
@Controller
public class InventarioController {

    private final InventarioService inventario;
    private final ProductoService productos;

    @Autowired
    public InventarioController(InventarioService inventario, ProductoService productos) {
        this.inventario = inventario;
        this.productos = productos;
    }

    @RequestMapping(value = "/movimientos", method = RequestMethod.GET)
    public String movimientos(Model model) {
        model.addAttribute("movimientos", inventario.movimientos(100));
        model.addAttribute("productos", productos.buscar(null, null, true));
        model.addAttribute("tipos", TipoMovimiento.values());
        return "inventario/movimientos";
    }

    @RequestMapping(value = "/movimientos", method = RequestMethod.POST)
    public String registrar(@ModelAttribute MovimientoSolicitud s, @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u,
                            RedirectAttributes flash) {
        try {
            inventario.registrarMovimiento(s, u);
            flash.addFlashAttribute("ok", "Movimiento registrado");
        } catch (AppException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movimientos";
    }

    @RequestMapping(value = "/inventario/alertas", method = RequestMethod.GET)
    public String alertas(Model model) {
        model.addAttribute("productos", inventario.productosBajoMinimo());
        return "inventario/alertas";
    }
}
