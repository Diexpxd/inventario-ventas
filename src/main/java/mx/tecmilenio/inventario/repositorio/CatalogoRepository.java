package mx.tecmilenio.inventario.repositorio;

import mx.tecmilenio.inventario.modelo.Categoria;
import mx.tecmilenio.inventario.modelo.Proveedor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Acceso a datos de categorias y proveedores. */
@Repository
public class CatalogoRepository {

    private static final RowMapper<Categoria> CATEGORIA = (rs, n) -> {
        Categoria c = new Categoria();
        c.setId(rs.getLong("id"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));
        return c;
    };

    private static final RowMapper<Proveedor> PROVEEDOR = (rs, n) -> {
        Proveedor p = new Proveedor();
        p.setId(rs.getLong("id"));
        p.setNombre(rs.getString("nombre"));
        p.setContacto(rs.getString("contacto"));
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    };

    private final JdbcTemplate jdbc;

    @Autowired
    public CatalogoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Categoria> categorias() {
        return jdbc.query("SELECT id, nombre, descripcion FROM categorias ORDER BY nombre", CATEGORIA);
    }

    public Categoria categoriaPorId(Long id) {
        List<Categoria> r = jdbc.query("SELECT id, nombre, descripcion FROM categorias WHERE id = ?", CATEGORIA, id);
        return r.isEmpty() ? null : r.get(0);
    }

    public boolean existeCategoria(String nombre) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM categorias WHERE LOWER(nombre) = LOWER(?)",
                Integer.class, nombre);
        return n != null && n > 0;
    }

    public Long insertarCategoria(Categoria c) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            java.sql.PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO categorias (nombre, descripcion) VALUES (?, ?)", new String[]{"id"});
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public List<Proveedor> proveedores() {
        return jdbc.query("SELECT id, nombre, contacto, telefono, email, activo FROM proveedores WHERE activo = 1 ORDER BY nombre",
                PROVEEDOR);
    }

    public Proveedor proveedorPorId(Long id) {
        List<Proveedor> r = jdbc.query(
                "SELECT id, nombre, contacto, telefono, email, activo FROM proveedores WHERE id = ?", PROVEEDOR, id);
        return r.isEmpty() ? null : r.get(0);
    }
}
