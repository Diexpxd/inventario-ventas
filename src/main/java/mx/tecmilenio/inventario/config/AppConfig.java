package mx.tecmilenio.inventario.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.CompositeDatabasePopulator;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Contexto raiz: conexion a la base de datos, transacciones y las capas de servicio y repositorio.
 * Los datos de conexion salen de application.properties y se pueden cambiar con -Ddb.url=...
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {"mx.tecmilenio.inventario.servicio", "mx.tecmilenio.inventario.repositorio"})
public class AppConfig {

    @Value("${db.url}")
    private String url;
    @Value("${db.user}")
    private String usuario;
    @Value("${db.password}")
    private String password;
    @Value("${db.driver}")
    private String driver;
    @Value("${db.init}")
    private boolean inicializar;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propiedades() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(usuario);
        ds.setPassword(password);
        return ds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Crea las tablas y carga los datos de ejemplo al arrancar cuando db.init=true. */
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource ds) {
        ResourceDatabasePopulator schema = new ResourceDatabasePopulator(new ClassPathResource("db/schema.sql"));
        schema.setSqlScriptEncoding("UTF-8");
        // los datos de ejemplo llevan id fijo: si ya estaban cargados el insert falla y se ignora
        ResourceDatabasePopulator datos = new ResourceDatabasePopulator(new ClassPathResource("db/datos.sql"));
        datos.setSqlScriptEncoding("UTF-8");
        datos.setContinueOnError(true);
        CompositeDatabasePopulator populator = new CompositeDatabasePopulator(schema, datos);
        DataSourceInitializer init = new DataSourceInitializer();
        init.setDataSource(ds);
        init.setDatabasePopulator(populator);
        init.setEnabled(inicializar);
        return init;
    }

    /** Usuarios de demostracion; solo se crean si la tabla de usuarios esta vacia. */
    @Bean
    @DependsOn("dataSourceInitializer")
    public DatosIniciales datosIniciales(@Autowired JdbcTemplate jdbc, @Autowired PasswordEncoder encoder) {
        return new DatosIniciales(jdbc, encoder);
    }
}
