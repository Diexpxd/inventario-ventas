package mx.tecmilenio.inventario.excepcion;

import org.springframework.http.HttpStatus;

/** Faltan credenciales o son incorrectas (HTTP 401). */
public class NoAutenticadoException extends AppException {

    public NoAutenticadoException(String mensaje) {
        super(HttpStatus.UNAUTHORIZED, mensaje);
    }
}
