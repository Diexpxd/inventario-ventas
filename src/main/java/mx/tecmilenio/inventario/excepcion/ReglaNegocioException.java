package mx.tecmilenio.inventario.excepcion;

import org.springframework.http.HttpStatus;

/** Los datos estan bien pero la regla no lo permite: stock insuficiente, codigo repetido, etc. */
public class ReglaNegocioException extends AppException {

    public ReglaNegocioException(String mensaje) {
        super(HttpStatus.CONFLICT, mensaje);
    }
}
