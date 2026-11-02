package mx.tecmilenio.inventario.controlador;

import mx.tecmilenio.inventario.dto.UsuarioSolicitud;
import mx.tecmilenio.inventario.excepcion.AppException;
import mx.tecmilenio.inventario.modelo.Rol;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.seguridad.SeguridadInterceptor;
import mx.tecmilenio.inventario.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Administracion de usuarios (solo ADMIN). */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarios;

    @Autowired
    public UsuarioController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String lista(Model model) {
        model.addAttribute("usuarios", usuarios.listar());
        return "usuarios/lista";
    }

    @RequestMapping(value = "/nuevo", method = RequestMethod.GET)
    public String nuevo(Model model) {
        model.addAttribute("roles", Rol.values());
        model.addAttribute("u", new Usuario());
        return "usuarios/formulario";
    }

    @RequestMapping(value = "/{id}/editar", method = RequestMethod.GET)
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("roles", Rol.values());
        model.addAttribute("u", usuarios.porId(id));
        return "usuarios/formulario";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String crear(@ModelAttribute UsuarioSolicitud s, RedirectAttributes flash) {
        try {
            usuarios.guardar(s);
            flash.addFlashAttribute("ok", "Usuario " + s.getUsername() + " creado");
            return "redirect:/usuarios";
        } catch (AppException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/nuevo";
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String actualizar(@PathVariable Long id, @ModelAttribute UsuarioSolicitud s,
                             @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario actual, RedirectAttributes flash) {
        try {
            usuarios.actualizar(id, s, actual);
            flash.addFlashAttribute("ok", "Usuario actualizado");
            return "redirect:/usuarios";
        } catch (AppException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/" + id + "/editar";
        }
    }

    @RequestMapping(value = "/{id}/estado", method = RequestMethod.POST)
    public String cambiarEstado(@PathVariable Long id, @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario actual,
                                RedirectAttributes flash) {
        try {
            Usuario u = usuarios.porId(id);
            usuarios.cambiarEstado(id, !u.isActivo(), actual);
            flash.addFlashAttribute("ok", "Estado de " + u.getUsername() + " actualizado");
        } catch (AppException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
