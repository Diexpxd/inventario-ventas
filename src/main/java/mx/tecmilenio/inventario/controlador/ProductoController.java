package mx.tecmilenio.inventario.controlador;

import mx.tecmilenio.inventario.dto.ProductoSolicitud;
import mx.tecmilenio.inventario.excepcion.AppException;
import mx.tecmilenio.inventario.modelo.Producto;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.seguridad.SeguridadInterceptor;
import mx.tecmilenio.inventario.servicio.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Catalogo de productos: lista con busqueda, alta, edicion y baja logica. */
@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productos;

    @Autowired
    public ProductoController(ProductoService productos) {
        this.productos = productos;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String lista(@RequestParam(value = "q", required = false) String q,
                        @RequestParam(value = "categoria", required = false) Long categoria, Model model) {
        model.addAttribute("productos", productos.buscar(q, categoria, true));
        model.addAttribute("categorias", productos.categorias());
        model.addAttribute("q", q);
        model.addAttribute("categoriaSel", categoria);
        return "productos/lista";
    }

    @RequestMapping(value = "/nuevo", method = RequestMethod.GET)
    public String nuevo(Model model) {
        cargarCatalogos(model);
        model.addAttribute("producto", new Producto());
        return "productos/formulario";
    }

    @RequestMapping(value = "/{id}/editar", method = RequestMethod.GET)
    public String editar(@PathVariable Long id, Model model) {
        cargarCatalogos(model);
        model.addAttribute("producto", productos.porId(id));
        return "productos/formulario";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String crear(@ModelAttribute ProductoSolicitud s, @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u,
                        RedirectAttributes flash) {
        try {
            productos.guardar(s, u);
            flash.addFlashAttribute("ok", "Producto " + s.getCodigo() + " guardado");
            return "redirect:/productos";
        } catch (AppException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos/nuevo";
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String actualizar(@PathVariable Long id, @ModelAttribute ProductoSolicitud s, RedirectAttributes flash) {
        try {
            productos.actualizar(id, s);
            flash.addFlashAttribute("ok", "Producto actualizado");
            return "redirect:/productos";
        } catch (AppException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos/" + id + "/editar";
        }
    }

    @RequestMapping(value = "/{id}/desactivar", method = RequestMethod.POST)
    public String desactivar(@PathVariable Long id, RedirectAttributes flash) {
        productos.desactivar(id);
        flash.addFlashAttribute("ok", "Producto desactivado");
        return "redirect:/productos";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("categorias", productos.categorias());
        model.addAttribute("proveedores", productos.proveedores());
    }
}
