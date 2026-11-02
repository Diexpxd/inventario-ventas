package mx.tecmilenio.inventario.servicio;

import mx.tecmilenio.inventario.dto.ProductoSolicitud;
import mx.tecmilenio.inventario.excepcion.ReglaNegocioException;
import mx.tecmilenio.inventario.excepcion.RecursoNoEncontradoException;
import mx.tecmilenio.inventario.excepcion.ValidacionException;
import mx.tecmilenio.inventario.modelo.Categoria;
import mx.tecmilenio.inventario.modelo.Producto;
import mx.tecmilenio.inventario.modelo.Proveedor;
import mx.tecmilenio.inventario.modelo.TipoMovimiento;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.repositorio.CatalogoRepository;
import mx.tecmilenio.inventario.repositorio.MovimientoRepository;
import mx.tecmilenio.inventario.repositorio.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** Reglas de negocio de los productos y las categorías. */
@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productos;
    private final CatalogoRepository catalogo;
    private final MovimientoRepository movimientos;

    @Autowired
    public ProductoService(ProductoRepository productos, CatalogoRepository catalogo, MovimientoRepository movimientos) {
        this.productos = productos;
        this.catalogo = catalogo;
        this.movimientos = movimientos;
    }

    @Transactional(readOnly = true)
    public List<Producto> buscar(String texto, Long idCategoria, boolean soloActivos) {
        return productos.listar(texto, idCategoria, soloActivos);
    }

    @Transactional(readOnly = true)
    public Producto porId(Long id) {
        Producto p = productos.buscarPorId(id);
        if (p == null) {
            throw new RecursoNoEncontradoException("No existe el producto con id " + id);
        }
        return p;
    }

    /** Da de alta un producto. Si trae stock inicial deja un movimiento de ENTRADA. */
    public Producto guardar(ProductoSolicitud s, Usuario usuario) {
        Producto p = construir(new Producto(), s);
        if (s.getStock() < 0) {
            throw new ValidacionException("El stock inicial no puede ser negativo");
        }
        if (productos.buscarPorCodigo(p.getCodigo()) != null) {
            throw new ReglaNegocioException("Ya existe un producto con el codigo " + p.getCodigo());
        }
        p.setStock(s.getStock());
        Long id = productos.insertar(p);
        if (p.getStock() > 0) {
            movimientos.insertar(id, TipoMovimiento.ENTRADA, p.getStock(), usuario.getId(), "Stock inicial");
        }
        return productos.buscarPorId(id);
    }

    /** Edita los datos del producto. El stock solo cambia con movimientos de inventario. */
    public Producto actualizar(Long id, ProductoSolicitud s) {
        Producto actual = porId(id);
        construir(actual, s);
        Producto otro = productos.buscarPorCodigo(actual.getCodigo());
        if (otro != null && !otro.getId().equals(id)) {
            throw new ReglaNegocioException("Ya existe un producto con el codigo " + actual.getCodigo());
        }
        productos.actualizar(actual);
        return productos.buscarPorId(id);
    }

    public void desactivar(Long id) {
        porId(id);
        productos.desactivar(id);
    }

    @Transactional(readOnly = true)
    public void validarStock(Long id, int cantidad) {
        Producto p = porId(id);
        if (!p.hayStock(cantidad)) {
            throw new ReglaNegocioException("Stock insuficiente de " + p.getCodigo()
                    + ": hay " + p.getStock() + " y se piden " + cantidad);
        }
    }

    @Transactional(readOnly = true)
    public int contarActivos() {
        return productos.contarActivos();
    }

    @Transactional(readOnly = true)
    public List<Categoria> categorias() {
        return catalogo.categorias();
    }

    @Transactional(readOnly = true)
    public List<Proveedor> proveedores() {
        return catalogo.proveedores();
    }

    public Categoria crearCategoria(Categoria c) {
        c.setNombre(Validar.texto(c.getNombre(), "nombre", 80));
        c.setDescripcion(Validar.textoOpcional(c.getDescripcion(), "descripcion", 500));
        if (catalogo.existeCategoria(c.getNombre())) {
            throw new ReglaNegocioException("Ya existe la categoria " + c.getNombre());
        }
        return catalogo.categoriaPorId(catalogo.insertarCategoria(c));
    }

    private Producto construir(Producto p, ProductoSolicitud s) {
        p.setCodigo(Validar.texto(s.getCodigo(), "codigo", 50));
        p.setNombre(Validar.texto(s.getNombre(), "nombre", 150));
        p.setDescripcion(Validar.textoOpcional(s.getDescripcion(), "descripcion", 1000));
        if (s.getPrecio() == null || s.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacionException("El precio debe ser mayor que cero");
        }
        p.setPrecio(s.getPrecio().setScale(2, BigDecimal.ROUND_HALF_UP));
        if (s.getStockMinimo() < 0) {
            throw new ValidacionException("El stock minimo no puede ser negativo");
        }
        p.setStockMinimo(s.getStockMinimo());
        if (s.getIdCategoria() == null) {
            throw new ValidacionException("La categoria es obligatoria");
        }
        Categoria c = catalogo.categoriaPorId(s.getIdCategoria());
        if (c == null) {
            throw new ValidacionException("La categoria " + s.getIdCategoria() + " no existe");
        }
        p.setCategoria(c);
        if (s.getIdProveedor() == null) {
            p.setProveedor(null);
        } else {
            Proveedor pr = catalogo.proveedorPorId(s.getIdProveedor());
            if (pr == null) {
                throw new ValidacionException("El proveedor " + s.getIdProveedor() + " no existe");
            }
            p.setProveedor(pr);
        }
        return p;
    }
}
