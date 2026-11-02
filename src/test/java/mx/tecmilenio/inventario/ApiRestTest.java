package mx.tecmilenio.inventario;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import mx.tecmilenio.inventario.config.AppConfig;
import mx.tecmilenio.inventario.config.WebConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {AppConfig.class, WebConfig.class})
public class ApiRestTest {

    @Autowired
    private WebApplicationContext contexto;

    private MockMvc mvc;
    private final ObjectMapper json = new ObjectMapper();
    private final XmlMapper xml = new XmlMapper();

    @Before
    public void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
    }

    private static String basic(String usuario, String clave) {
        return "Basic " + Base64.getEncoder().encodeToString((usuario + ":" + clave).getBytes(StandardCharsets.UTF_8));
    }

    private MockHttpServletRequestBuilder como(MockHttpServletRequestBuilder b, String usuario) {
        return b.header("Authorization", basic(usuario, usuario + "123"));
    }

    private static ResultMatcher ubicacionPresente() {
        return r -> assertNotNull("falta la cabecera Location", r.getResponse().getHeader("Location"));
    }

    private JsonNode leerJson(ResultActions r) throws Exception {
        return json.readTree(r.andReturn().getResponse().getContentAsString());
    }

    private boolean tieneEnlace(JsonNode recurso, String rel) {
        for (JsonNode l : recurso.path("links")) {
            if (rel.equals(l.path("rel").asText())) {
                return true;
            }
        }
        return false;
    }

    // ---------- autenticacion y errores ----------

    @Test
    public void sinCredencialesRegresa401EnJson() throws Exception {
        ResultActions r = mvc.perform(get("/api/productos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Basic realm=\"inventario\""));
        JsonNode cuerpo = leerJson(r);
        assertEquals(401, cuerpo.path("status").asInt());
        assertEquals("/api/productos", cuerpo.path("ruta").asText());
        assertTrue(r.andReturn().getResponse().getContentType().startsWith("application/json"));
    }

    @Test
    public void sinCredencialesRegresa401EnXml() throws Exception {
        MvcResult r = mvc.perform(get("/api/productos").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isUnauthorized()).andReturn();
        assertTrue(r.getResponse().getContentType().startsWith("application/xml"));
        JsonNode cuerpo = xml.readTree(r.getResponse().getContentAsString());
        assertEquals(401, cuerpo.path("status").asInt());
        assertFalse(cuerpo.path("mensaje").asText().isEmpty());
        assertTrue(r.getResponse().getContentAsString().startsWith("<error>"));
    }

    @Test
    public void contrasenaIncorrectaRegresa401() throws Exception {
        mvc.perform(get("/api").header("Authorization", basic("admin", "mala")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void rutaInexistenteRegresa404EnJsonYXml() throws Exception {
        ResultActions j = mvc.perform(como(get("/api/no-existe").accept(MediaType.APPLICATION_JSON), "admin"))
                .andExpect(status().isNotFound());
        assertEquals(404, leerJson(j).path("status").asInt());
        MvcResult x = mvc.perform(como(get("/api/no-existe").accept(MediaType.APPLICATION_XML), "admin"))
                .andExpect(status().isNotFound()).andReturn();
        assertEquals(404, xml.readTree(x.getResponse().getContentAsString()).path("status").asInt());
    }

    @Test
    public void metodoNoAdmitidoRegresa405EnJsonYXml() throws Exception {
        ResultActions j = mvc.perform(como(patch("/api/productos/1").accept(MediaType.APPLICATION_JSON), "admin"))
                .andExpect(status().isMethodNotAllowed());
        assertEquals(405, leerJson(j).path("status").asInt());
        MvcResult x = mvc.perform(como(put("/api/ventas/1").accept(MediaType.APPLICATION_XML), "admin"))
                .andExpect(status().isMethodNotAllowed()).andReturn();
        assertEquals(405, xml.readTree(x.getResponse().getContentAsString()).path("status").asInt());
    }

    @Test
    public void recursoInexistenteRegresa404() throws Exception {
        ResultActions r = mvc.perform(como(get("/api/productos/9999"), "admin")).andExpect(status().isNotFound());
        assertEquals(404, leerJson(r).path("status").asInt());
    }

    @Test
    public void jsonMalFormadoRegresa400() throws Exception {
        mvc.perform(como(post("/api/productos"), "admin").contentType(MediaType.APPLICATION_JSON).content("{ esto no es json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void parametroConTipoIncorrectoRegresa400() throws Exception {
        mvc.perform(como(get("/api/productos/abc"), "admin")).andExpect(status().isBadRequest());
    }

    @Test
    public void contentTypeNoSoportadoRegresa415() throws Exception {
        mvc.perform(como(post("/api/productos"), "admin").contentType(MediaType.TEXT_PLAIN).content("hola"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // ---------- roles ----------

    @Test
    public void vendedorNoPuedeCrearProductos() throws Exception {
        mvc.perform(como(post("/api/productos"), "vendedor").contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigo\":\"X-1\",\"nombre\":\"x\",\"precio\":10,\"idCategoria\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void almacenistaNoPuedeVender() throws Exception {
        mvc.perform(como(get("/api/ventas"), "almacen")).andExpect(status().isForbidden());
    }

    @Test
    public void soloAdminVeUsuarios() throws Exception {
        mvc.perform(como(get("/api/usuarios"), "vendedor")).andExpect(status().isForbidden());
        ResultActions r = mvc.perform(como(get("/api/usuarios"), "admin")).andExpect(status().isOk());
        JsonNode u = leerJson(r).path("elementos").get(0);
        assertFalse("la contrasena nunca debe salir", u.has("passwordHash") || u.has("password"));
    }

    // ---------- JSON, XML e hipermedia ----------

    @Test
    public void raizListaLosRecursos() throws Exception {
        JsonNode raiz = leerJson(mvc.perform(como(get("/api"), "vendedor")).andExpect(status().isOk()));
        assertTrue(tieneEnlace(raiz, "productos"));
        assertTrue(tieneEnlace(raiz, "ventas"));
    }

    @Test
    public void productosEnJsonConEnlaces() throws Exception {
        ResultActions r = mvc.perform(como(get("/api/productos").accept(MediaType.APPLICATION_JSON), "admin")).andExpect(status().isOk());
        assertTrue(r.andReturn().getResponse().getContentType().startsWith("application/json"));
        JsonNode c = leerJson(r);
        assertTrue(c.path("total").asInt() >= 5);
        assertTrue(tieneEnlace(c, "self"));
        assertTrue(tieneEnlace(c, "crear"));
        assertTrue(tieneEnlace(c.path("elementos").get(0), "actualizar"));
    }

    @Test
    public void productosEnXmlPorAccept() throws Exception {
        MvcResult r = mvc.perform(como(get("/api/productos/1").accept(MediaType.APPLICATION_XML), "admin"))
                .andExpect(status().isOk()).andReturn();
        assertTrue(r.getResponse().getContentType().startsWith("application/xml"));
        JsonNode p = xml.readTree(r.getResponse().getContentAsString());
        assertEquals("P-001", p.path("codigo").asText());
        assertNotNull(p.path("links"));
    }

    @Test
    public void formatoSeEligeConParametro() throws Exception {
        MvcResult r = mvc.perform(como(get("/api/productos/1?formato=xml"), "admin")).andExpect(status().isOk()).andReturn();
        assertTrue(r.getResponse().getContentType().startsWith("application/xml"));
        MvcResult j = mvc.perform(como(get("/api/productos/1"), "admin")).andExpect(status().isOk()).andReturn();
        assertTrue(j.getResponse().getContentType().startsWith("application/json"));
    }

    @Test
    public void losEnlacesDependenDelRol() throws Exception {
        JsonNode comoVendedor = leerJson(mvc.perform(como(get("/api/productos/1"), "vendedor")).andExpect(status().isOk()));
        assertTrue(tieneEnlace(comoVendedor, "self"));
        assertFalse(tieneEnlace(comoVendedor, "actualizar"));
        assertFalse(tieneEnlace(comoVendedor, "desactivar"));
        JsonNode comoAdmin = leerJson(mvc.perform(como(get("/api/productos/1"), "admin")).andExpect(status().isOk()));
        assertTrue(tieneEnlace(comoAdmin, "actualizar"));
        assertTrue(tieneEnlace(comoAdmin, "desactivar"));
    }

    @Test
    public void alertasListaLosProductosBajoMinimo() throws Exception {
        JsonNode c = leerJson(mvc.perform(como(get("/api/inventario/alertas"), "almacen")).andExpect(status().isOk()));
        assertTrue(c.path("total").asInt() >= 2);
        for (JsonNode p : c.path("elementos")) {
            assertTrue(p.path("stock").asInt() <= p.path("stockMinimo").asInt());
        }
    }

    // ---------- CRUD de productos (POST, GET, PUT, DELETE) ----------

    @Test
    public void cicloCompletoDeUnProducto() throws Exception {
        String codigo = "T-" + System.nanoTime() % 1000000;
        String cuerpo = "{\"codigo\":\"" + codigo + "\",\"nombre\":\"Producto de prueba\",\"precio\":25.50,"
                + "\"stock\":10,\"stockMinimo\":2,\"idCategoria\":1}";
        MvcResult creado = mvc.perform(como(post("/api/productos"), "admin").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated()).andExpect(ubicacionPresente()).andReturn();
        String ubicacion = creado.getResponse().getHeader("Location");
        String ruta = ubicacion.substring(ubicacion.indexOf("/api"));
        JsonNode p = json.readTree(creado.getResponse().getContentAsString());
        assertEquals(codigo, p.path("codigo").asText());

        mvc.perform(como(post("/api/productos"), "admin").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isConflict());

        String cambio = "{\"codigo\":\"" + codigo + "\",\"nombre\":\"Nombre nuevo\",\"precio\":30,\"stockMinimo\":3,\"idCategoria\":1}";
        JsonNode editado = leerJson(mvc.perform(como(put(ruta), "admin").contentType(MediaType.APPLICATION_JSON).content(cambio))
                .andExpect(status().isOk()));
        assertEquals("Nombre nuevo", editado.path("nombre").asText());
        assertEquals("el PUT no toca el stock", 10, editado.path("stock").asInt());

        JsonNode baja = leerJson(mvc.perform(como(delete(ruta), "admin")).andExpect(status().isOk()));
        assertFalse(baja.path("activo").asBoolean());
        assertFalse(tieneEnlace(baja, "desactivar"));
    }

    @Test
    public void productoInvalidoRegresa400() throws Exception {
        ResultActions r = mvc.perform(como(post("/api/productos"), "admin").contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigo\":\"\",\"nombre\":\"x\",\"precio\":-5,\"idCategoria\":1}")).andExpect(status().isBadRequest());
        assertEquals(400, leerJson(r).path("status").asInt());
    }

    // ---------- ventas ----------

    private int stockDe(long id) throws Exception {
        return leerJson(mvc.perform(como(get("/api/productos/" + id), "admin"))).path("stock").asInt();
    }

    @Test
    public void ventaDescuentaStockYSeCancela() throws Exception {
        int antes = stockDe(4);
        String venta = "{\"cliente\":\"Cliente de prueba\",\"detalles\":[{\"idProducto\":4,\"cantidad\":2}]}";
        MvcResult creada = mvc.perform(como(post("/api/ventas"), "vendedor").contentType(MediaType.APPLICATION_JSON).content(venta))
                .andExpect(status().isCreated()).andExpect(ubicacionPresente()).andReturn();
        JsonNode v = json.readTree(creada.getResponse().getContentAsString());
        assertTrue(v.path("folio").asText().startsWith("V-"));
        assertEquals(258.0, v.path("total").asDouble(), 0.001);
        assertTrue(tieneEnlace(v, "cancelar"));
        assertEquals(antes - 2, stockDe(4));

        String ruta = "/api/ventas/" + v.path("id").asLong();
        JsonNode cancelada = leerJson(mvc.perform(como(delete(ruta), "vendedor")).andExpect(status().isOk()));
        assertEquals("CANCELADA", cancelada.path("estado").asText());
        assertFalse(tieneEnlace(cancelada, "cancelar"));
        assertEquals(antes, stockDe(4));

        mvc.perform(como(delete(ruta), "vendedor")).andExpect(status().isConflict());
    }

    @Test
    public void ventaSinStockSuficienteRegresa409EnJsonYXml() throws Exception {
        int antes = stockDe(5);
        String venta = "{\"detalles\":[{\"idProducto\":5,\"cantidad\":999}]}";
        ResultActions j = mvc.perform(como(post("/api/ventas"), "vendedor").contentType(MediaType.APPLICATION_JSON).content(venta))
                .andExpect(status().isConflict());
        assertEquals(409, leerJson(j).path("status").asInt());
        MvcResult x = mvc.perform(como(post("/api/ventas"), "vendedor").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML).content(venta)).andExpect(status().isConflict()).andReturn();
        assertEquals(409, xml.readTree(x.getResponse().getContentAsString()).path("status").asInt());
        assertEquals("una venta fallida no debe tocar el stock", antes, stockDe(5));
    }

    @Test
    public void ventaSinLineasRegresa400() throws Exception {
        mvc.perform(como(post("/api/ventas"), "vendedor").contentType(MediaType.APPLICATION_JSON).content("{\"detalles\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ventaSePuedeEnviarEnXml() throws Exception {
        int antes = stockDe(2);
        String venta = "<venta><cliente>Cliente XML</cliente><detalles><detalle><idProducto>2</idProducto>"
                + "<cantidad>1</cantidad></detalle></detalles></venta>";
        MvcResult r = mvc.perform(como(post("/api/ventas"), "vendedor").contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_JSON).content(venta)).andReturn();
        int codigo = r.getResponse().getStatus();
        assertEquals("esperaba 201: " + r.getResponse().getContentAsString(), 201, codigo);
        assertEquals(antes - 1, stockDe(2));
    }

    // ---------- movimientos ----------

    @Test
    public void movimientoDeEntradaSumaStock() throws Exception {
        int antes = stockDe(1);
        String m = "{\"idProducto\":1,\"tipo\":\"ENTRADA\",\"cantidad\":5,\"referencia\":\"prueba\"}";
        MvcResult r = mvc.perform(como(post("/api/movimientos"), "almacen").contentType(MediaType.APPLICATION_JSON).content(m))
                .andExpect(status().isCreated()).andExpect(ubicacionPresente()).andReturn();
        assertEquals("ENTRADA", json.readTree(r.getResponse().getContentAsString()).path("tipo").asText());
        assertEquals(antes + 5, stockDe(1));
    }

    @Test
    public void movimientoDeSalidaMayorAlStockRegresa409() throws Exception {
        mvc.perform(como(post("/api/movimientos"), "almacen").contentType(MediaType.APPLICATION_JSON)
                .content("{\"idProducto\":5,\"tipo\":\"SALIDA\",\"cantidad\":500}")).andExpect(status().isConflict());
    }

    @Test
    public void tipoDeMovimientoDesconocidoRegresa400() throws Exception {
        mvc.perform(como(post("/api/movimientos"), "almacen").contentType(MediaType.APPLICATION_JSON)
                .content("{\"idProducto\":5,\"tipo\":\"REGALO\",\"cantidad\":1}")).andExpect(status().isBadRequest());
    }
}
