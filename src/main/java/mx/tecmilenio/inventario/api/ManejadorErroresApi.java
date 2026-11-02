package mx.tecmilenio.inventario.api;

import mx.tecmilenio.inventario.excepcion.AppException;
import mx.tecmilenio.inventario.excepcion.ErrorRespuesta;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Convierte las excepciones de la API en un cuerpo de error. Como el cuerpo es un objeto Java normal,
 * sale en JSON o en XML segun lo que pida el cliente en Accept, igual que las respuestas buenas.
 */
@RestControllerAdvice(basePackages = "mx.tecmilenio.inventario.api")
public class ManejadorErroresApi {

    private static final Logger LOG = Logger.getLogger(ManejadorErroresApi.class.getName());

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorRespuesta> aplicacion(AppException e, HttpServletRequest req) {
        ResponseEntity.BodyBuilder b = ResponseEntity.status(e.getStatus());
        if (e.getStatus() == HttpStatus.UNAUTHORIZED) {
            b.header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"inventario\"");
        }
        return b.body(cuerpo(e.getStatus(), e.getMessage(), req));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> cuerpoIlegible(HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST,
                "El cuerpo de la peticion no se pudo leer: revisa que sea JSON o XML valido y que los valores sean correctos", req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRespuesta> tipoIncorrecto(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, "El parametro " + e.getName() + " tiene un valor que no es valido", req);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorRespuesta> falta(MissingServletRequestParameterException e, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, "Falta el parametro " + e.getParameterName(), req);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorRespuesta> tipoNoSoportado(HttpServletRequest req) {
        return responder(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content-Type no soportado: usa application/json o application/xml", req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorRespuesta> metodo(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "El metodo " + e.getMethod() + " no esta permitido aqui", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> inesperado(Exception e, HttpServletRequest req) {
        LOG.log(Level.SEVERE, "Error inesperado en " + req.getRequestURI(), e);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error interno en el servidor", req);
    }

    private ResponseEntity<ErrorRespuesta> responder(HttpStatus status, String mensaje, HttpServletRequest req) {
        return ResponseEntity.status(status).body(cuerpo(status, mensaje, req));
    }

    private ErrorRespuesta cuerpo(HttpStatus status, String mensaje, HttpServletRequest req) {
        return new ErrorRespuesta(status.value(), status.getReasonPhrase(), mensaje, req.getRequestURI());
    }
}
