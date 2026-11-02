package mx.tecmilenio.inventario.excepcion;

import org.springframework.http.HttpStatus;

/** El usuario esta autenticado pero su rol no permite la accion (HTTP 403). */
public class AccesoDenegadoException extends AppException {

    public AccesoDenegadoException(String mensaje) {
        super(HttpStatus.FORBIDDEN, mensaje);
    }
}
