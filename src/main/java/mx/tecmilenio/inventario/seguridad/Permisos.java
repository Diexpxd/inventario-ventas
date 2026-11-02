package mx.tecmilenio.inventario.seguridad;

/** Dice que modulo hace falta para entrar a una ruta (null si basta con haber iniciado sesion). */
public final class Permisos {

    private Permisos() {
    }

    /** @param ruta ruta sin el contexto de la aplicacion, por ejemplo /api/productos/3 */
    public static String modulo(String ruta, String metodo) {
        boolean lectura = "GET".equals(metodo) || "HEAD".equals(metodo);
        String r = ruta.startsWith("/api") ? ruta.substring(4) : ruta;
        String primero = r.startsWith("/") ? r.substring(1) : r;
        int corte = primero.indexOf('/');
        String segundo = "";
        if (corte >= 0) {
            segundo = primero.substring(corte + 1);
            primero = primero.substring(0, corte);
        }
        boolean api = ruta.startsWith("/api");
        switch (primero) {
            case "dashboard":
                return "dashboard";
            case "productos":
                if (api) {
                    return lectura ? "productos" : "productos.editar";
                }
                return lectura && !segundo.contains("nuevo") && !segundo.contains("editar") ? "productos" : "productos.editar";
            case "categorias":
                return lectura ? "productos" : "productos.editar";
            case "ventas":
                return "ventas";
            case "movimientos":
            case "inventario":
                return "movimientos";
            case "usuarios":
                return "usuarios";
            default:
                return null;
        }
    }
}
