package mx.tecmilenio.inventario.api;

import mx.tecmilenio.inventario.modelo.Categoria;
import mx.tecmilenio.inventario.modelo.Coleccion;
import mx.tecmilenio.inventario.modelo.EstadoVenta;
import mx.tecmilenio.inventario.modelo.MovimientoInventario;
import mx.tecmilenio.inventario.modelo.Producto;
import mx.tecmilenio.inventario.modelo.Recurso;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.modelo.Venta;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Arma los enlaces hipermedia de cada recurso. Solo se ofrecen las acciones que el rol
 * del usuario puede hacer y que tienen sentido en el estado actual del recurso.
 */
@Component
public class Enlaces {

    private String base() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/api").toUriString();
    }

    public <T extends Recurso> T raiz(T r) {
        r.agregarEnlace("self", base(), "GET");
        r.agregarEnlace("productos", base() + "/productos", "GET");
        r.agregarEnlace("categorias", base() + "/categorias", "GET");
        r.agregarEnlace("ventas", base() + "/ventas", "GET");
        r.agregarEnlace("movimientos", base() + "/movimientos", "GET");
        r.agregarEnlace("alertas", base() + "/inventario/alertas", "GET");
        r.agregarEnlace("usuarios", base() + "/usuarios", "GET");
        return r;
    }

    public Producto producto(Producto p, Usuario u) {
        String self = base() + "/productos/" + p.getId();
        p.agregarEnlace("self", self, "GET");
        p.agregarEnlace("coleccion", base() + "/productos", "GET");
        if (u.tienePermiso("productos.editar") && p.isActivo()) {
            p.agregarEnlace("actualizar", self, "PUT");
            p.agregarEnlace("desactivar", self, "DELETE");
        }
        if (u.tienePermiso("movimientos") && p.isActivo()) {
            p.agregarEnlace("registrar-movimiento", base() + "/movimientos", "POST");
        }
        return p;
    }

    public Coleccion<Producto> productos(Coleccion<Producto> c, Usuario u) {
        c.agregarEnlace("self", base() + "/productos", "GET");
        c.agregarEnlace("raiz", base(), "GET");
        if (u.tienePermiso("productos.editar")) {
            c.agregarEnlace("crear", base() + "/productos", "POST");
        }
        c.getElementos().forEach(p -> producto(p, u));
        return c;
    }

    public Categoria categoria(Categoria c) {
        c.agregarEnlace("coleccion", base() + "/categorias", "GET");
        c.agregarEnlace("productos", base() + "/productos?categoria=" + c.getId(), "GET");
        return c;
    }

    public Coleccion<Categoria> categorias(Coleccion<Categoria> c, Usuario u) {
        c.agregarEnlace("self", base() + "/categorias", "GET");
        c.agregarEnlace("raiz", base(), "GET");
        if (u.tienePermiso("productos.editar")) {
            c.agregarEnlace("crear", base() + "/categorias", "POST");
        }
        c.getElementos().forEach(this::categoria);
        return c;
    }

    public Venta venta(Venta v) {
        String self = base() + "/ventas/" + v.getId();
        v.agregarEnlace("self", self, "GET");
        v.agregarEnlace("coleccion", base() + "/ventas", "GET");
        if (v.getEstado() != EstadoVenta.CANCELADA) {
            v.agregarEnlace("cancelar", self, "DELETE");
        }
        return v;
    }

    public Coleccion<Venta> ventas(Coleccion<Venta> c) {
        c.agregarEnlace("self", base() + "/ventas", "GET");
        c.agregarEnlace("raiz", base(), "GET");
        c.agregarEnlace("crear", base() + "/ventas", "POST");
        c.getElementos().forEach(this::venta);
        return c;
    }

    public MovimientoInventario movimiento(MovimientoInventario m) {
        m.agregarEnlace("self", base() + "/movimientos/" + m.getId(), "GET");
        m.agregarEnlace("coleccion", base() + "/movimientos", "GET");
        m.agregarEnlace("producto", base() + "/productos/" + m.getProducto().getId(), "GET");
        return m;
    }

    public Coleccion<MovimientoInventario> movimientos(Coleccion<MovimientoInventario> c) {
        c.agregarEnlace("self", base() + "/movimientos", "GET");
        c.agregarEnlace("raiz", base(), "GET");
        c.agregarEnlace("crear", base() + "/movimientos", "POST");
        c.getElementos().forEach(this::movimiento);
        return c;
    }

    public Usuario usuario(Usuario u) {
        String self = base() + "/usuarios/" + u.getId();
        u.agregarEnlace("self", self, "GET");
        u.agregarEnlace("coleccion", base() + "/usuarios", "GET");
        u.agregarEnlace("actualizar", self, "PUT");
        if (u.isActivo()) {
            u.agregarEnlace("desactivar", self, "DELETE");
        }
        return u;
    }

    public Coleccion<Usuario> usuarios(Coleccion<Usuario> c) {
        c.agregarEnlace("self", base() + "/usuarios", "GET");
        c.agregarEnlace("raiz", base(), "GET");
        c.agregarEnlace("crear", base() + "/usuarios", "POST");
        c.getElementos().forEach(this::usuario);
        return c;
    }
}
