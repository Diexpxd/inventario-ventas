package mx.tecmilenio.inventario.repositorio;

import mx.tecmilenio.inventario.modelo.DetalleVenta;
import mx.tecmilenio.inventario.modelo.EstadoVenta;
import mx.tecmilenio.inventario.modelo.Producto;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.modelo.Venta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Acceso a datos de ventas y sus líneas con JdbcTemplate. */
@Repository
public class VentaRepository {

    private static final String BASE =
            "SELECT v.id, v.folio, v.fecha, v.cliente, v.total, v.estado, u.id AS usu_id, u.username, u.nombre AS usu_nombre "
                    + "FROM ventas v JOIN usuarios u ON u.id = v.id_vendedor ";

    private static final RowMapper<Venta> MAPPER = (rs, n) -> {
        Venta v = new Venta();
        v.setId(rs.getLong("id"));
        v.setFolio(rs.getString("folio"));
        v.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        v.setCliente(rs.getString("cliente"));
        v.setTotal(rs.getBigDecimal("total"));
        v.setEstado(EstadoVenta.valueOf(rs.getString("estado")));
        Usuario u = new Usuario();
        u.setId(rs.getLong("usu_id"));
        u.setUsername(rs.getString("username"));
        u.setNombre(rs.getString("usu_nombre"));
        v.setVendedor(u);
        return v;
    };

    private static final RowMapper<DetalleVenta> DETALLE = (rs, n) -> {
        DetalleVenta d = new DetalleVenta();
        d.setId(rs.getLong("id"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        d.setSubtotal(rs.getBigDecimal("subtotal"));
        Producto p = new Producto();
        p.setId(rs.getLong("id_producto"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        d.setProducto(p);
        return d;
    };

    private final JdbcTemplate jdbc;

    @Autowired
    public VentaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Guarda la venta con sus detalles y le asigna el folio a partir del id. */
    public Long insertar(Venta v) {
        KeyHolder kh = new GeneratedKeyHolder();
        String folioTemporal = "TMP-" + UUID.randomUUID();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO ventas (folio, id_vendedor, cliente, total, estado) VALUES (?, ?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setString(1, folioTemporal);
            ps.setLong(2, v.getVendedor().getId());
            if (v.getCliente() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, v.getCliente());
            }
            ps.setBigDecimal(4, v.getTotal());
            ps.setString(5, v.getEstado().name());
            return ps;
        }, kh);
        Long id = kh.getKey().longValue();
        jdbc.update("UPDATE ventas SET folio = ? WHERE id = ?", String.format("V-%06d", id), id);
        for (DetalleVenta d : v.getDetalles()) {
            jdbc.update("INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) "
                            + "VALUES (?, ?, ?, ?, ?)",
                    id, d.getProducto().getId(), d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
        }
        return id;
    }

    /** Ventas de todos o solo de un vendedor, de la mas reciente a la mas vieja. */
    public List<Venta> listar(Long idVendedor, int limite) {
        if (idVendedor == null) {
            return jdbc.query(BASE + "ORDER BY v.fecha DESC, v.id DESC LIMIT ?", MAPPER, limite);
        }
        return jdbc.query(BASE + "WHERE v.id_vendedor = ? ORDER BY v.fecha DESC, v.id DESC LIMIT ?",
                MAPPER, idVendedor, limite);
    }

    public Venta buscarPorId(Long id) {
        List<Venta> r = jdbc.query(BASE + "WHERE v.id = ?", MAPPER, id);
        if (r.isEmpty()) {
            return null;
        }
        Venta v = r.get(0);
        v.setDetalles(new ArrayList<>(jdbc.query(
                "SELECT d.id, d.id_producto, d.cantidad, d.precio_unitario, d.subtotal, p.codigo, p.nombre "
                        + "FROM detalle_ventas d JOIN productos p ON p.id = d.id_producto WHERE d.id_venta = ? ORDER BY d.id",
                DETALLE, id)));
        return v;
    }

    public void cambiarEstado(Long id, EstadoVenta estado) {
        jdbc.update("UPDATE ventas SET estado = ? WHERE id = ?", estado.name(), id);
    }

    /** Suma de las ventas no canceladas de hoy; con idVendedor solo cuenta las de ese vendedor. */
    public BigDecimal totalDeHoy(Long idVendedor) {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM ventas "
                + "WHERE estado <> 'CANCELADA' AND CAST(fecha AS DATE) = CURRENT_DATE";
        BigDecimal t = idVendedor == null
                ? jdbc.queryForObject(sql, BigDecimal.class)
                : jdbc.queryForObject(sql + " AND id_vendedor = ?", BigDecimal.class, idVendedor);
        return t == null ? BigDecimal.ZERO : t;
    }
}
