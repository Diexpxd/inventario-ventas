package mx.tecmilenio.inventario.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

/** Usuario del sistema con su rol; decide qué módulos puede usar. */
public class Usuario extends Recurso {

    private Long id;
    private String username;
    private String passwordHash;
    private String nombre;
    private String email;
    private Rol rol;
    private boolean activo = true;
    private LocalDateTime createdAt;

    public boolean estaActivo() {
        return activo;
    }

    /**
     * Revisa si el rol del usuario puede entrar a un modulo.
     * Modulos: dashboard, productos, productos.editar, ventas, movimientos y usuarios.
     */
    public boolean tienePermiso(String modulo) {
        if (rol == Rol.ADMIN) {
            return true;
        }
        switch (modulo) {
            case "dashboard":
            case "productos":
                return true;
            case "productos.editar":
            case "movimientos":
                return rol == Rol.ALMACENISTA;
            case "ventas":
                return rol == Rol.VENDEDOR;
            default:
                return false;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
