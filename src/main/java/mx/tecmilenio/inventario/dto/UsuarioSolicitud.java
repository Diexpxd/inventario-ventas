package mx.tecmilenio.inventario.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import mx.tecmilenio.inventario.modelo.Rol;

/** Alta o edicion de usuario. En una edicion el password puede ir vacio y se conserva el anterior. */
@JacksonXmlRootElement(localName = "usuario")
public class UsuarioSolicitud {

    private String username;
    private String password;
    private String nombre;
    private String email;
    private Rol rol = Rol.VENDEDOR;
    private Boolean activo;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    /** Solo se usa al editar: null deja el estado como estaba. */
    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
