package mx.tecmilenio.inventario.excepcion;

import org.springframework.http.HttpStatus;

/** Excepcion base de la aplicacion. Cada subclase sabe con que codigo HTTP se responde. */
public abstract class AppException extends RuntimeException {

    private final HttpStatus status;

    protected AppException(HttpStatus status, String mensaje) {
        super(mensaje);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
