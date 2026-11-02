package mx.tecmilenio.inventario.servicio;

import mx.tecmilenio.inventario.excepcion.ValidacionException;

/** Validaciones chicas que se repiten en los servicios. */
final class Validar {

    private Validar() {
    }

    static String texto(String valor, String campo, int max) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException("El campo " + campo + " es obligatorio");
        }
        String limpio = valor.trim();
        if (limpio.length() > max) {
            throw new ValidacionException("El campo " + campo + " no puede pasar de " + max + " caracteres");
        }
        return limpio;
    }

    static String textoOpcional(String valor, String campo, int max) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return texto(valor, campo, max);
    }
}
