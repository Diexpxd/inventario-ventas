package mx.tecmilenio.inventario.controlador;

import mx.tecmilenio.inventario.dto.LineaVenta;
import mx.tecmilenio.inventario.dto.VentaSolicitud;
import mx.tecmilenio.inventario.excepcion.AppException;
import mx.tecmilenio.inventario.excepcion.ValidacionException;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.modelo.Venta;
import mx.tecmilenio.inventario.seguridad.SeguridadInterceptor;
import mx.tecmilenio.inventario.servicio.ProductoService;
import mx.tecmilenio.inventario.servicio.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/** Pantallas de venta: carrito, confirmacion, historial y cancelacion. */
@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventas;
    private final ProductoService productos;

    @Autowired
    public VentaController(VentaService ventas, ProductoService productos) {
        this.ventas = ventas;
        this.productos = productos;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String historial(@RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u, Model model) {
        model.addAttribute("ventas", ventas.historial(u, 100));
        return "ventas/historial";
    }

    @RequestMapping(value = "/nueva", method = RequestMethod.GET)
    public String nueva(Model model) {
        model.addAttribute("productos", productos.buscar(null, null, true));
        return "ventas/nueva";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String detalle(@PathVariable Long id, @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u, Model model) {
        model.addAttribute("venta", ventas.porId(id, u));
        return "ventas/detalle";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String confirmar(@RequestParam(value = "cliente", required = false) String cliente,
                            @RequestParam(value = "idProducto", required = false) List<Long> ids,
                            @RequestParam(value = "cantidad", required = false) List<Integer> cantidades,
                            @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u, RedirectAttributes flash) {
        try {
            if (ids == null || cantidades == null || ids.size() != cantidades.size()) {
                throw new ValidacionException("Agrega al menos un producto a la venta");
            }
            VentaSolicitud s = new VentaSolicitud();
            s.setCliente(cliente);
            List<LineaVenta> lineas = new ArrayList<>();
            for (int i = 0; i < ids.size(); i++) {
                lineas.add(new LineaVenta(ids.get(i), cantidades.get(i)));
            }
            s.setDetalles(lineas);
            Venta v = ventas.registrarVenta(s, u);
            flash.addFlashAttribute("ok", "Venta " + v.getFolio() + " registrada por $" + v.getTotal());
            return "redirect:/ventas/" + v.getId();
        } catch (AppException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/ventas/nueva";
        }
    }

    @RequestMapping(value = "/{id}/cancelar", method = RequestMethod.POST)
    public String cancelar(@PathVariable Long id, @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u,
                           RedirectAttributes flash) {
        try {
            Venta v = ventas.cancelar(id, u);
            flash.addFlashAttribute("ok", "Venta " + v.getFolio() + " cancelada y stock regresado");
        } catch (AppException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ventas/" + id;
    }
}
