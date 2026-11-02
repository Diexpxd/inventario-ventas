package mx.tecmilenio.inventario.api;

import mx.tecmilenio.inventario.dto.VentaSolicitud;
import mx.tecmilenio.inventario.modelo.Coleccion;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.modelo.Venta;
import mx.tecmilenio.inventario.seguridad.SeguridadInterceptor;
import mx.tecmilenio.inventario.servicio.VentaService;
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

/** Ventas. DELETE no borra: cancela la venta y regresa el stock. */
@RestController
@RequestMapping("/api/ventas")
public class VentaRestController {

    private final VentaService ventas;
    private final Enlaces enlaces;

    @Autowired
    public VentaRestController(VentaService ventas, Enlaces enlaces) {
        this.ventas = ventas;
        this.enlaces = enlaces;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Coleccion<Venta> historial(@RequestParam(value = "limite", defaultValue = "50") int limite,
                                      @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u) {
        return enlaces.ventas(new Coleccion<>(ventas.historial(u, Math.max(1, Math.min(limite, 200)))));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Venta obtener(@PathVariable Long id, @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u) {
        return enlaces.venta(ventas.porId(id, u));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Venta> registrar(@RequestBody VentaSolicitud s,
                                           @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u) {
        Venta v = ventas.registrarVenta(s, u);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(v.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(enlaces.venta(v));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Venta cancelar(@PathVariable Long id, @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u) {
        return enlaces.venta(ventas.cancelar(id, u));
    }
}
