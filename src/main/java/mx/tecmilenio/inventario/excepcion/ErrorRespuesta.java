package mx.tecmilenio.inventario.excepcion;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDateTime;

/** Cuerpo que se regresa cuando algo falla. Sale como JSON o XML segun el Accept de la peticion. */
@JacksonXmlRootElement(localName = "error")
public class ErrorRespuesta {

    private int status;
    private String error;
    private String mensaje;
    private String ruta;
    private LocalDateTime fecha = LocalDateTime.now();

    public ErrorRespuesta() {
    }

    public ErrorRespuesta(int status, String error, String mensaje, String ruta) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.ruta = ruta;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
