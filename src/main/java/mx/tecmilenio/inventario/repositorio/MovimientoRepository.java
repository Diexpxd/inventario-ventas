package mx.tecmilenio.inventario.repositorio;

import mx.tecmilenio.inventario.modelo.MovimientoInventario;
import mx.tecmilenio.inventario.modelo.Producto;
import mx.tecmilenio.inventario.modelo.TipoMovimiento;
import mx.tecmilenio.inventario.modelo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

/** Acceso a datos de los movimientos de inventario con JdbcTemplate. */
@Repository
public class MovimientoRepository {

    private static final String BASE =
            "SELECT m.id, m.tipo, m.cantidad, m.fecha, m.referencia, p.id AS prod_id, p.codigo, p.nombre AS prod_nombre, "
                    + "u.id AS usu_id, u.username, u.nombre AS usu_nombre "
                    + "FROM movimientos_inventario m JOIN productos p ON p.id = m.id_producto "
                    + "JOIN usuarios u ON u.id = m.id_usuario ";

    private static final RowMapper<MovimientoInventario> MAPPER = (rs, n) -> {
        MovimientoInventario m = new MovimientoInventario();
        m.setId(rs.getLong("id"));
        m.setTipo(TipoMovimiento.valueOf(rs.getString("tipo")));
        m.setCantidad(rs.getInt("cantidad"));
        m.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        m.setReferencia(rs.getString("referencia"));
        Producto p = new Producto();
        p.setId(rs.getLong("prod_id"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("prod_nombre"));
        m.setProducto(p);
        Usuario u = new Usuario();
        u.setId(rs.getLong("usu_id"));
        u.setUsername(rs.getString("username"));
        u.setNombre(rs.getString("usu_nombre"));
        m.setUsuario(u);
        return m;
    };

    private final JdbcTemplate jdbc;

    @Autowired
    public MovimientoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<MovimientoInventario> ultimos(int limite) {
        return jdbc.query(BASE + "ORDER BY m.fecha DESC, m.id DESC LIMIT ?", MAPPER, limite);
    }

    public MovimientoInventario buscarPorId(Long id) {
        List<MovimientoInventario> r = jdbc.query(BASE + "WHERE m.id = ?", MAPPER, id);
        return r.isEmpty() ? null : r.get(0);
    }

    public Long insertar(Long idProducto, TipoMovimiento tipo, int cantidad, Long idUsuario, String referencia) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO movimientos_inventario (id_producto, tipo, cantidad, id_usuario, referencia) "
                            + "VALUES (?, ?, ?, ?, ?)", new String[]{"id"});
            ps.setLong(1, idProducto);
            ps.setString(2, tipo.name());
            ps.setInt(3, cantidad);
            ps.setLong(4, idUsuario);
            ps.setString(5, referencia);
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }
}
