package mx.tecmilenio.inventario.servicio;

import mx.tecmilenio.inventario.dto.LineaVenta;
import mx.tecmilenio.inventario.dto.VentaSolicitud;
import mx.tecmilenio.inventario.excepcion.AccesoDenegadoException;
import mx.tecmilenio.inventario.excepcion.RecursoNoEncontradoException;
import mx.tecmilenio.inventario.excepcion.ReglaNegocioException;
import mx.tecmilenio.inventario.excepcion.ValidacionException;
import mx.tecmilenio.inventario.modelo.DetalleVenta;
import mx.tecmilenio.inventario.modelo.EstadoVenta;
import mx.tecmilenio.inventario.modelo.Producto;
import mx.tecmilenio.inventario.modelo.Rol;
import mx.tecmilenio.inventario.modelo.TipoMovimiento;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.modelo.Venta;
import mx.tecmilenio.inventario.repositorio.MovimientoRepository;
import mx.tecmilenio.inventario.repositorio.ProductoRepository;
import mx.tecmilenio.inventario.repositorio.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reglas de negocio de las ventas: descuenta el stock al registrar y lo devuelve al cancelar. */
@Service
@Transactional
public class VentaService {

    private final VentaRepository ventas;
    private final ProductoRepository productos;
    private final MovimientoRepository movimientos;

    @Autowired
    public VentaService(VentaRepository ventas, ProductoRepository productos, MovimientoRepository movimientos) {
        this.ventas = ventas;
        this.productos = productos;
        this.movimientos = movimientos;
    }

    /**
     * Registra la venta ya pagada, calcula el total, descuenta el stock y deja un movimiento
     * de SALIDA por cada producto. Si algo falla no se guarda nada.
     */
    public Venta registrarVenta(VentaSolicitud s, Usuario vendedor) {
        if (s.getDetalles() == null || s.getDetalles().isEmpty()) {
            throw new ValidacionException("La venta necesita al menos un producto");
        }
        Map<Long, Integer> pedido = new LinkedHashMap<>();
        for (LineaVenta l : s.getDetalles()) {
            if (l.getIdProducto() == null) {
                throw new ValidacionException("Cada renglon necesita el id del producto");
            }
            if (l.getCantidad() <= 0) {
                throw new ValidacionException("La cantidad de cada producto debe ser mayor que cero");
            }
            pedido.merge(l.getIdProducto(), l.getCantidad(), Integer::sum);
        }

        Venta v = new Venta();
        v.setVendedor(vendedor);
        v.setCliente(Validar.textoOpcional(s.getCliente(), "cliente", 100));
        v.setEstado(EstadoVenta.PAGADA);
        for (Map.Entry<Long, Integer> e : pedido.entrySet()) {
            Producto p = productos.buscarPorId(e.getKey());
            if (p == null || !p.isActivo()) {
                throw new ValidacionException("El producto " + e.getKey() + " no existe o esta desactivado");
            }
            if (!p.hayStock(e.getValue())) {
                throw new ReglaNegocioException("Stock insuficiente de " + p.getCodigo()
                        + ": hay " + p.getStock() + " y se piden " + e.getValue());
            }
            DetalleVenta d = new DetalleVenta();
            d.setProducto(p);
            d.setCantidad(e.getValue());
            d.setPrecioUnitario(p.getPrecio());
            v.agregarDetalle(d);
        }
        v.calcularTotal();

        Long id = ventas.insertar(v);
        Venta guardada = ventas.buscarPorId(id);
        for (DetalleVenta d : v.getDetalles()) {
            actualizarStock(d.getProducto().getId(), -d.getCantidad(), TipoMovimiento.SALIDA, vendedor, guardada.getFolio());
        }
        return guardada;
    }

    /** Cancela la venta y regresa los productos al stock. */
    public Venta cancelar(Long id, Usuario usuario) {
        Venta v = porId(id, usuario);
        if (v.getEstado() == EstadoVenta.CANCELADA) {
            throw new ReglaNegocioException("La venta " + v.getFolio() + " ya estaba cancelada");
        }
        for (DetalleVenta d : v.getDetalles()) {
            actualizarStock(d.getProducto().getId(), d.getCantidad(), TipoMovimiento.ENTRADA, usuario,
                    "Cancelacion " + v.getFolio());
        }
        v.cancelar();
        ventas.cambiarEstado(id, v.getEstado());
        return ventas.buscarPorId(id);
    }

    @Transactional(readOnly = true)
    public Venta porId(Long id, Usuario usuario) {
        Venta v = ventas.buscarPorId(id);
        if (v == null) {
            throw new RecursoNoEncontradoException("No existe la venta con id " + id);
        }
        if (usuario.getRol() == Rol.VENDEDOR && !v.getVendedor().getId().equals(usuario.getId())) {
            throw new AccesoDenegadoException("Esa venta es de otro vendedor");
        }
        return v;
    }

    /** El vendedor ve solo sus ventas, el administrador ve todas. */
    @Transactional(readOnly = true)
    public List<Venta> historial(Usuario usuario, int limite) {
        return ventas.listar(usuario.getRol() == Rol.VENDEDOR ? usuario.getId() : null, limite);
    }

    /** Total vendido hoy: el vendedor ve solo lo suyo, los demas roles ven el total. */
    @Transactional(readOnly = true)
    public BigDecimal totalDeHoy(Usuario usuario) {
        return ventas.totalDeHoy(usuario.getRol() == Rol.VENDEDOR ? usuario.getId() : null);
    }

    private void actualizarStock(Long idProducto, int delta, TipoMovimiento tipo, Usuario usuario, String referencia) {
        if (!productos.ajustarStock(idProducto, delta)) {
            throw new ReglaNegocioException("Ya no hay stock suficiente del producto " + idProducto);
        }
        movimientos.insertar(idProducto, tipo, Math.abs(delta), usuario.getId(), referencia);
    }
}
