package mx.tecmilenio.inventario.repositorio;

import mx.tecmilenio.inventario.modelo.Categoria;
import mx.tecmilenio.inventario.modelo.Producto;
import mx.tecmilenio.inventario.modelo.Proveedor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de productos con JdbcTemplate. */
@Repository
public class ProductoRepository {

    private static final String BASE =
            "SELECT p.id, p.codigo, p.nombre, p.descripcion, p.precio, p.stock, p.stock_minimo, p.activo, "
                    + "p.created_at, p.updated_at, c.id AS cat_id, c.nombre AS cat_nombre, "
                    + "pr.id AS prov_id, pr.nombre AS prov_nombre "
                    + "FROM productos p JOIN categorias c ON c.id = p.id_categoria "
                    + "LEFT JOIN proveedores pr ON pr.id = p.id_proveedor ";

    private static final RowMapper<Producto> MAPPER = (rs, n) -> {
        Producto p = new Producto();
        p.setId(rs.getLong("id"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setStock(rs.getInt("stock"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setActivo(rs.getBoolean("activo"));
        Timestamp creado = rs.getTimestamp("created_at");
        p.setCreatedAt(creado == null ? null : creado.toLocalDateTime());
        Timestamp mod = rs.getTimestamp("updated_at");
        p.setUpdatedAt(mod == null ? null : mod.toLocalDateTime());
        Categoria c = new Categoria();
        c.setId(rs.getLong("cat_id"));
        c.setNombre(rs.getString("cat_nombre"));
        p.setCategoria(c);
        long idProv = rs.getLong("prov_id");
        if (!rs.wasNull()) {
            Proveedor pr = new Proveedor();
            pr.setId(idProv);
            pr.setNombre(rs.getString("prov_nombre"));
            p.setProveedor(pr);
        }
        return p;
    };

    private final JdbcTemplate jdbc;

    @Autowired
    public ProductoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Producto> listar(String busqueda, Long idCategoria, boolean soloActivos) {
        StringBuilder sql = new StringBuilder(BASE).append("WHERE 1 = 1 ");
        List<Object> args = new ArrayList<>();
        if (soloActivos) {
            sql.append("AND p.activo = 1 ");
        }
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append("AND (LOWER(p.nombre) LIKE ? OR LOWER(p.codigo) LIKE ?) ");
            String like = "%" + busqueda.trim().toLowerCase() + "%";
            args.add(like);
            args.add(like);
        }
        if (idCategoria != null) {
            sql.append("AND p.id_categoria = ? ");
            args.add(idCategoria);
        }
        sql.append("ORDER BY p.nombre");
        return jdbc.query(sql.toString(), MAPPER, args.toArray());
    }

    public Producto buscarPorId(Long id) {
        List<Producto> r = jdbc.query(BASE + "WHERE p.id = ?", MAPPER, id);
        return r.isEmpty() ? null : r.get(0);
    }

    public Producto buscarPorCodigo(String codigo) {
        List<Producto> r = jdbc.query(BASE + "WHERE p.codigo = ?", MAPPER, codigo);
        return r.isEmpty() ? null : r.get(0);
    }

    public List<Producto> bajoMinimo() {
        return jdbc.query(BASE + "WHERE p.activo = 1 AND p.stock <= p.stock_minimo ORDER BY p.stock, p.nombre", MAPPER);
    }

    public int contarActivos() {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM productos WHERE activo = 1", Integer.class);
        return n == null ? 0 : n;
    }

    public Long insertar(Producto p) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO productos (codigo, nombre, descripcion, precio, stock, stock_minimo, id_categoria, id_proveedor) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", new String[]{"id"});
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setBigDecimal(4, p.getPrecio());
            ps.setInt(5, p.getStock());
            ps.setInt(6, p.getStockMinimo());
            ps.setLong(7, p.getCategoria().getId());
            if (p.getProveedor() == null) {
                ps.setNull(8, Types.BIGINT);
            } else {
                ps.setLong(8, p.getProveedor().getId());
            }
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public void actualizar(Producto p) {
        jdbc.update("UPDATE productos SET codigo = ?, nombre = ?, descripcion = ?, precio = ?, stock_minimo = ?, "
                        + "id_categoria = ?, id_proveedor = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                p.getCodigo(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getStockMinimo(),
                p.getCategoria().getId(), p.getProveedor() == null ? null : p.getProveedor().getId(), p.getId());
    }

    public void desactivar(Long id) {
        jdbc.update("UPDATE productos SET activo = 0, updated_at = CURRENT_TIMESTAMP WHERE id = ?", id);
    }

    /** Suma o resta al stock en una sola sentencia y no deja que quede negativo. */
    public boolean ajustarStock(Long id, int delta) {
        return jdbc.update("UPDATE productos SET stock = stock + ?, updated_at = CURRENT_TIMESTAMP "
                + "WHERE id = ? AND stock + ? >= 0", delta, id, delta) == 1;
    }
}
