package mx.tecmilenio.inventario.servicio;

import mx.tecmilenio.inventario.dto.MovimientoSolicitud;
import mx.tecmilenio.inventario.excepcion.RecursoNoEncontradoException;
import mx.tecmilenio.inventario.excepcion.ReglaNegocioException;
import mx.tecmilenio.inventario.excepcion.ValidacionException;
import mx.tecmilenio.inventario.modelo.MovimientoInventario;
import mx.tecmilenio.inventario.modelo.Producto;
import mx.tecmilenio.inventario.modelo.TipoMovimiento;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.repositorio.MovimientoRepository;
import mx.tecmilenio.inventario.repositorio.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Reglas de negocio de los movimientos de inventario y las alertas de stock. */
@Service
@Transactional
public class InventarioService {

    private final ProductoRepository productos;
    private final MovimientoRepository movimientos;

    @Autowired
    public InventarioService(ProductoRepository productos, MovimientoRepository movimientos) {
        this.productos = productos;
        this.movimientos = movimientos;
    }

    /**
     * Registra una entrada, salida o ajuste y mueve el stock del producto.
     * En el ajuste la cantidad lleva signo, en entrada y salida debe ser positiva.
     */
    public MovimientoInventario registrarMovimiento(MovimientoSolicitud s, Usuario usuario) {
        if (s.getIdProducto() == null) {
            throw new ValidacionException("El producto es obligatorio");
        }
        if (s.getTipo() == null) {
            throw new ValidacionException("El tipo de movimiento es obligatorio (ENTRADA, SALIDA o AJUSTE)");
        }
        if (s.getCantidad() == 0 || (s.getTipo() != TipoMovimiento.AJUSTE && s.getCantidad() < 0)) {
            throw new ValidacionException("La cantidad debe ser mayor que cero (en un ajuste puede ser negativa pero no cero)");
        }
        Producto p = productos.buscarPorId(s.getIdProducto());
        if (p == null || !p.isActivo()) {
            throw new RecursoNoEncontradoException("No existe un producto activo con id " + s.getIdProducto());
        }
        int delta = s.getTipo() == TipoMovimiento.SALIDA ? -s.getCantidad() : s.getCantidad();
        if (!productos.ajustarStock(p.getId(), delta)) {
            throw new ReglaNegocioException("Stock insuficiente de " + p.getCodigo() + ": hay " + p.getStock()
                    + " y el movimiento pide quitar " + Math.abs(delta));
        }
        String ref = Validar.textoOpcional(s.getReferencia(), "referencia", 50);
        Long id = movimientos.insertar(p.getId(), s.getTipo(), s.getCantidad(), usuario.getId(), ref);
        return movimientos.buscarPorId(id);
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventario> movimientos(int limite) {
        return movimientos.ultimos(limite);
    }

    @Transactional(readOnly = true)
    public MovimientoInventario movimientoPorId(Long id) {
        MovimientoInventario m = movimientos.buscarPorId(id);
        if (m == null) {
            throw new RecursoNoEncontradoException("No existe el movimiento con id " + id);
        }
        return m;
    }

    @Transactional(readOnly = true)
    public List<Producto> productosBajoMinimo() {
        return productos.bajoMinimo();
    }
}
