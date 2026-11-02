package mx.tecmilenio.inventario.api;

import mx.tecmilenio.inventario.dto.MovimientoSolicitud;
import mx.tecmilenio.inventario.modelo.Coleccion;
import mx.tecmilenio.inventario.modelo.MovimientoInventario;
import mx.tecmilenio.inventario.modelo.Producto;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.seguridad.SeguridadInterceptor;
import mx.tecmilenio.inventario.servicio.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/** API REST de movimientos de inventario y alertas de stock bajo (/api/movimientos, /api/inventario/alertas). */
@RestController
@RequestMapping("/api")
public class InventarioRestController {

    private final InventarioService inventario;
    private final Enlaces enlaces;

    @Autowired
    public InventarioRestController(InventarioService inventario, Enlaces enlaces) {
        this.inventario = inventario;
        this.enlaces = enlaces;
    }

    @RequestMapping(value = "/movimientos", method = RequestMethod.GET)
    public Coleccion<MovimientoInventario> movimientos(@RequestParam(value = "limite", defaultValue = "50") int limite) {
        return enlaces.movimientos(new Coleccion<>(inventario.movimientos(Math.max(1, Math.min(limite, 200)))));
    }

    @RequestMapping(value = "/movimientos/{id}", method = RequestMethod.GET)
    public MovimientoInventario movimiento(@PathVariable Long id) {
        return enlaces.movimiento(inventario.movimientoPorId(id));
    }

    @RequestMapping(value = "/movimientos", method = RequestMethod.POST)
    public ResponseEntity<MovimientoInventario> registrar(@RequestBody MovimientoSolicitud s,
                                                          @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u) {
        MovimientoInventario m = inventario.registrarMovimiento(s, u);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(m.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(enlaces.movimiento(m));
    }

    @RequestMapping(value = "/inventario/alertas", method = RequestMethod.GET)
    public Coleccion<Producto> alertas(@RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u) {
        Coleccion<Producto> c = new Coleccion<>(inventario.productosBajoMinimo());
        c.agregarEnlace("self", ServletUriComponentsBuilder.fromCurrentRequest().toUriString(), "GET");
        c.getElementos().forEach(p -> enlaces.producto(p, u));
        return c;
    }
}
