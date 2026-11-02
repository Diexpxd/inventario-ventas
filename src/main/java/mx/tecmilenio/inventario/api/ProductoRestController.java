package mx.tecmilenio.inventario.api;

import mx.tecmilenio.inventario.dto.ProductoSolicitud;
import mx.tecmilenio.inventario.modelo.Categoria;
import mx.tecmilenio.inventario.modelo.Coleccion;
import mx.tecmilenio.inventario.modelo.Producto;
import mx.tecmilenio.inventario.modelo.Usuario;
import mx.tecmilenio.inventario.seguridad.SeguridadInterceptor;
import mx.tecmilenio.inventario.servicio.ProductoService;
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

/** Productos y categorias. GET lee, POST crea, PUT reemplaza los datos y DELETE desactiva. */
@RestController
@RequestMapping("/api")
public class ProductoRestController {

    private final ProductoService productos;
    private final Enlaces enlaces;

    @Autowired
    public ProductoRestController(ProductoService productos, Enlaces enlaces) {
        this.productos = productos;
        this.enlaces = enlaces;
    }

    @RequestMapping(value = "/productos", method = RequestMethod.GET)
    public Coleccion<Producto> listar(@RequestParam(value = "q", required = false) String q,
                                      @RequestParam(value = "categoria", required = false) Long categoria,
                                      @RequestParam(value = "todos", defaultValue = "false") boolean todos,
                                      @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u) {
        return enlaces.productos(new Coleccion<>(productos.buscar(q, categoria, !todos)), u);
    }

    @RequestMapping(value = "/productos/{id}", method = RequestMethod.GET)
    public Producto obtener(@PathVariable Long id, @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u) {
        return enlaces.producto(productos.porId(id), u);
    }

    @RequestMapping(value = "/productos", method = RequestMethod.POST)
    public ResponseEntity<Producto> crear(@RequestBody ProductoSolicitud s,
                                          @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u) {
        Producto p = productos.guardar(s, u);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(p.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(enlaces.producto(p, u));
    }

    @RequestMapping(value = "/productos/{id}", method = RequestMethod.PUT)
    public Producto actualizar(@PathVariable Long id, @RequestBody ProductoSolicitud s,
                               @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u) {
        return enlaces.producto(productos.actualizar(id, s), u);
    }

    @RequestMapping(value = "/productos/{id}", method = RequestMethod.DELETE)
    public Producto desactivar(@PathVariable Long id, @RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u) {
        productos.desactivar(id);
        return enlaces.producto(productos.porId(id), u);
    }

    @RequestMapping(value = "/categorias", method = RequestMethod.GET)
    public Coleccion<Categoria> categorias(@RequestAttribute(SeguridadInterceptor.ATRIBUTO) Usuario u) {
        return enlaces.categorias(new Coleccion<>(productos.categorias()), u);
    }

    @RequestMapping(value = "/categorias", method = RequestMethod.POST)
    public ResponseEntity<Categoria> crearCategoria(@RequestBody Categoria c) {
        Categoria nueva = productos.crearCategoria(c);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(nueva.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(enlaces.categoria(nueva));
    }
}
