package mx.tecmilenio.inventario.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Crea tres usuarios de demostracion (uno por rol) si no hay ninguno. Las contrasenas se guardan
 * con BCrypt igual que las de cualquier usuario nuevo. Cambiarlas antes de usar el sistema en serio.
 */
public class DatosIniciales implements InitializingBean {

    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;

    public DatosIniciales(JdbcTemplate jdbc, PasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.encoder = encoder;
    }

    @Override
    public void afterPropertiesSet() {
        Integer n;
        try {
            n = jdbc.queryForObject("SELECT COUNT(*) FROM usuarios", Integer.class);
        } catch (RuntimeException e) {
            return;
        }
        if (n != null && n > 0) {
            return;
        }
        crear("admin", "admin123", "Administrador de Ejemplo", "admin@ejemplo.com", "ADMIN");
        crear("vendedor", "vendedor123", "Vendedor de Ejemplo", "vendedor@ejemplo.com", "VENDEDOR");
        crear("almacen", "almacen123", "Almacenista de Ejemplo", "almacen@ejemplo.com", "ALMACENISTA");
    }

    private void crear(String username, String password, String nombre, String email, String rol) {
        jdbc.update("INSERT INTO usuarios (username, password_hash, nombre, email, rol) VALUES (?, ?, ?, ?, ?)",
                username, encoder.encode(password), nombre, email, rol);
    }
}
