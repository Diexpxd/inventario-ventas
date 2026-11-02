package mx.tecmilenio.inventario.excepcion;

import org.springframework.http.HttpStatus;

/** Datos mal capturados: campos vacios, precios negativos, etc. */
public class ValidacionException extends AppException {

    public ValidacionException(String mensaje) {
        super(HttpStatus.BAD_REQUEST, mensaje);
    }
}
