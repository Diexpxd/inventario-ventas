package mx.tecmilenio.inventario.excepcion;

import org.springframework.http.HttpStatus;

/** El recurso pedido no existe (HTTP 404). */
public class RecursoNoEncontradoException extends AppException {

    public RecursoNoEncontradoException(String mensaje) {
        super(HttpStatus.NOT_FOUND, mensaje);
    }
}
