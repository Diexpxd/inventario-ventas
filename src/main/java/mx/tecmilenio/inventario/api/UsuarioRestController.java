package mx.tecmilenio.inventario.api;

import mx.tecmilenio.inventario.dto.UsuarioSolicitud;
import mx.tecmilenio.inventario.modelo.Coleccion;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.seguridad.SeguridadInterceptor;
import mx.tecmilenio.inventario.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/** Solo para administradores. DELETE desactiva el usuario, no lo borra. */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    private final UsuarioService usuarios;
    private final Enlaces enlaces;

    @Autowired
    public UsuarioRestController(UsuarioService usuarios, Enlaces enlaces) {
        this.usuarios = usuarios;
        this.enlaces = enlaces;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Coleccion<Usuario> listar() {
        return enlaces.usuarios(new Coleccion<>(usuarios.listar()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Usuario obtener(@PathVariable Long id) {
        return enlaces.usuario(usuarios.porId(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Usuario> crear(@RequestBody UsuarioSolicitud s) {
        Usuario u = usuarios.guardar(s);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(u.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(enlaces.usuario(u));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Usuario actualizar(@PathVariable Long id, @RequestBody UsuarioSolicitud s,
                              @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario actual) {
        return enlaces.usuario(usuarios.actualizar(id, s, actual));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Usuario desactivar(@PathVariable Long id, @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario actual) {
        return enlaces.usuario(usuarios.cambiarEstado(id, false, actual));
    }
}
