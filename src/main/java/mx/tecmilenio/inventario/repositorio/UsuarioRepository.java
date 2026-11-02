package mx.tecmilenio.inventario.repositorio;

import mx.tecmilenio.inventario.modelo.Rol;
import mx.tecmilenio.inventario.modelo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

/** Acceso a datos de los usuarios con JdbcTemplate. */
@Repository
public class UsuarioRepository {

    private static final String BASE =
            "SELECT id, username, password_hash, nombre, email, rol, activo, created_at FROM usuarios ";

    private static final RowMapper<Usuario> MAPPER = (rs, n) -> {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setNombre(rs.getString("nombre"));
        u.setEmail(rs.getString("email"));
        u.setRol(Rol.valueOf(rs.getString("rol")));
        u.setActivo(rs.getBoolean("activo"));
        u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return u;
    };

    private final JdbcTemplate jdbc;

    @Autowired
    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Usuario> listar() {
        return jdbc.query(BASE + "ORDER BY nombre", MAPPER);
    }

    public Usuario buscarPorId(Long id) {
        List<Usuario> r = jdbc.query(BASE + "WHERE id = ?", MAPPER, id);
        return r.isEmpty() ? null : r.get(0);
    }

    public Usuario buscarPorUsername(String username) {
        List<Usuario> r = jdbc.query(BASE + "WHERE username = ?", MAPPER, username);
        return r.isEmpty() ? null : r.get(0);
    }

    public Usuario buscarPorEmail(String email) {
        List<Usuario> r = jdbc.query(BASE + "WHERE email = ?", MAPPER, email);
        return r.isEmpty() ? null : r.get(0);
    }

    public Long insertar(Usuario u) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO usuarios (username, password_hash, nombre, email, rol) VALUES (?, ?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getNombre());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getRol().name());
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public void actualizar(Usuario u) {
        jdbc.update("UPDATE usuarios SET password_hash = ?, nombre = ?, email = ?, rol = ? WHERE id = ?",
                u.getPasswordHash(), u.getNombre(), u.getEmail(), u.getRol().name(), u.getId());
    }

    public void cambiarActivo(Long id, boolean activo) {
        jdbc.update("UPDATE usuarios SET activo = ? WHERE id = ?", activo, id);
    }
}
