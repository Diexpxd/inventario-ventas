package mx.tecmilenio.inventario.servicio;

import mx.tecmilenio.inventario.dto.UsuarioSolicitud;
import mx.tecmilenio.inventario.excepcion.NoAutenticadoException;
import mx.tecmilenio.inventario.excepcion.RecursoNoEncontradoException;
import mx.tecmilenio.inventario.excepcion.ReglaNegocioException;
import mx.tecmilenio.inventario.excepcion.ValidacionException;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Reglas de negocio de los usuarios y su autenticación con BCrypt. */
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public Usuario autenticar(String username, String password) {
        Usuario u = username == null ? null : usuarios.buscarPorUsername(username.trim());
        if (u == null || password == null || !encoder.matches(password, u.getPasswordHash())) {
            throw new NoAutenticadoException("Usuario o contrasena incorrectos");
        }
        if (!u.estaActivo()) {
            throw new NoAutenticadoException("El usuario esta desactivado");
        }
        return u;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarios.listar();
    }

    @Transactional(readOnly = true)
    public Usuario porId(Long id) {
        Usuario u = usuarios.buscarPorId(id);
        if (u == null) {
            throw new RecursoNoEncontradoException("No existe el usuario con id " + id);
        }
        return u;
    }

    public Usuario guardar(UsuarioSolicitud s) {
        Usuario u = new Usuario();
        u.setUsername(Validar.texto(s.getUsername(), "username", 50));
        if (u.getUsername().contains(" ")) {
            throw new ValidacionException("El username no puede llevar espacios");
        }
        u.setPasswordHash(encoder.encode(contrasena(s.getPassword())));
        datosComunes(u, s);
        if (usuarios.buscarPorUsername(u.getUsername()) != null) {
            throw new ReglaNegocioException("Ya existe el usuario " + u.getUsername());
        }
        if (usuarios.buscarPorEmail(u.getEmail()) != null) {
            throw new ReglaNegocioException("Ya hay un usuario con el correo " + u.getEmail());
        }
        return usuarios.buscarPorId(usuarios.insertar(u));
    }

    /** El username no cambia. Si el password viene vacio se queda el que tenia. */
    public Usuario actualizar(Long id, UsuarioSolicitud s, Usuario quienLoPide) {
        Usuario u = porId(id);
        datosComunes(u, s);
        if (s.getPassword() != null && !s.getPassword().isEmpty()) {
            u.setPasswordHash(encoder.encode(contrasena(s.getPassword())));
        }
        Usuario otro = usuarios.buscarPorEmail(u.getEmail());
        if (otro != null && !otro.getId().equals(id)) {
            throw new ReglaNegocioException("Ya hay un usuario con el correo " + u.getEmail());
        }
        usuarios.actualizar(u);
        if (s.getActivo() != null && s.getActivo() != u.isActivo()) {
            return cambiarEstado(id, s.getActivo(), quienLoPide);
        }
        return usuarios.buscarPorId(id);
    }

    public Usuario cambiarEstado(Long id, boolean activo, Usuario quienLoPide) {
        Usuario u = porId(id);
        if (!activo && u.getId().equals(quienLoPide.getId())) {
            throw new ReglaNegocioException("No puedes desactivar tu propio usuario");
        }
        usuarios.cambiarActivo(id, activo);
        return usuarios.buscarPorId(id);
    }

    private void datosComunes(Usuario u, UsuarioSolicitud s) {
        u.setNombre(Validar.texto(s.getNombre(), "nombre", 100));
        String email = Validar.texto(s.getEmail(), "email", 100);
        if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
            throw new ValidacionException("El correo no es valido");
        }
        u.setEmail(email);
        if (s.getRol() == null) {
            throw new ValidacionException("El rol es obligatorio (ADMIN, VENDEDOR o ALMACENISTA)");
        }
        u.setRol(s.getRol());
    }

    private String contrasena(String password) {
        if (password == null || password.length() < 6) {
            throw new ValidacionException("La contrasena debe tener al menos 6 caracteres");
        }
        return password;
    }
}
