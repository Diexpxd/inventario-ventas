package mx.tecmilenio.inventario.controlador;

import mx.tecmilenio.inventario.excepcion.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Errores de las pantallas: muestran la pagina error.jsp con el codigo HTTP que toca. */
@ControllerAdvice(basePackages = "mx.tecmilenio.inventario.controlador")
public class ManejadorErroresWeb {

    private static final Logger LOG = Logger.getLogger(ManejadorErroresWeb.class.getName());

    @ExceptionHandler(AppException.class)
    public ModelAndView aplicacion(AppException e, HttpServletResponse res) {
        return pagina(e.getStatus(), e.getMessage(), res);
    }

    @ExceptionHandler({BindException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ModelAndView peticionIncorrecta(HttpServletResponse res) {
        return pagina(HttpStatus.BAD_REQUEST, "Hay datos con un formato que no es valido. Revisa el formulario.", res);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView metodo(HttpServletResponse res) {
        return pagina(HttpStatus.METHOD_NOT_ALLOWED, "Esa accion no esta permitida con ese metodo.", res);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView inesperado(Exception e, HttpServletResponse res) {
        LOG.log(Level.SEVERE, "Error inesperado en una pantalla", e);
        return pagina(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error interno. Intenta de nuevo.", res);
    }

    private ModelAndView pagina(HttpStatus status, String mensaje, HttpServletResponse res) {
        res.setStatus(status.value());
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("codigo", status.value());
        mv.addObject("tituloError", status.getReasonPhrase());
        mv.addObject("mensaje", mensaje);
        return mv;
    }
}
