package cl.duoc.fullstack.patientservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración CORS global para el microservicio de Pacientes.
 *
 * En DESARROLLO: Permite localhost (http, https)
 * En PRODUCCIÓN: Usar variables de entorno para especificar dominios exactos
 *
 * Permite que aplicaciones cliente (web, móvil) desde diferentes orígenes
 * consuman los endpoints REST.
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:4200,http://localhost:8080}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (configurables por entorno)
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
                "Content-Type",
                "Authorization",
                "X-Correlation-ID",
                "X-Requested-With"
        ));

        // Headers que se pueden exponer
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Correlation-ID"
        ));

        // Permitir credenciales
        configuration.setAllowCredentials(true);

        // Max age en segundos (1 hora)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}



