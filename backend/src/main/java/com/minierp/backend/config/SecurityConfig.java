package com.minierp.backend.config;

import com.minierp.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ADMIN = "ADMINISTRACION";
    private static final String COMPRAS = "COMPRAS";
    private static final String INVENTARIO = "INVENTARIO";
    private static final String VENTAS = "VENTAS";

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // API REST sin sesiones ni cookies: CSRF no aplica
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Cada peticion se autentica con su token, el servidor no guarda sesion
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Publico
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Solo administracion
                        .requestMatchers("/api/usuarios/**").hasRole(ADMIN)
                        .requestMatchers("/api/bitacora/**").hasRole(ADMIN)
                        .requestMatchers("/api/reportes/logs/**").hasRole(ADMIN)

                        // Reportes de cada area: la propia area y administracion
                        .requestMatchers("/api/reportes/inventario/**").hasAnyRole(ADMIN, INVENTARIO)
                        .requestMatchers("/api/reportes/compras/**").hasAnyRole(ADMIN, COMPRAS)
                        .requestMatchers("/api/reportes/ventas/**").hasAnyRole(ADMIN, VENTAS)

                        // Catalogo de productos: todos lo consultan, inventario lo administra
                        .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias/**").authenticated()
                        .requestMatchers("/api/productos/**", "/api/categorias/**").hasAnyRole(ADMIN, INVENTARIO)
                        .requestMatchers("/api/inventario/**").hasAnyRole(ADMIN, INVENTARIO)

                        // Compras
                        .requestMatchers("/api/proveedores/**").hasAnyRole(ADMIN, COMPRAS)
                        .requestMatchers("/api/compras/**").hasAnyRole(ADMIN, COMPRAS)

                        // Ventas
                        .requestMatchers("/api/clientes/**").hasAnyRole(ADMIN, VENTAS)
                        .requestMatchers("/api/ventas/**").hasAnyRole(ADMIN, VENTAS)

                        // Cualquier otra ruta requiere estar autenticado
                        .anyRequest().authenticated()
                )

                // Respuestas JSON para 401 y 403 generados por el filtro de seguridad
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) ->
                                escribirError(res, 401, "Unauthorized", "Debe iniciar sesion"))
                        .accessDeniedHandler((req, res, ex) ->
                                escribirError(res, 403, "Forbidden", "No tiene permiso para realizar esta operacion"))
                )

                // Nuestro filtro JWT se ejecuta antes del filtro de login estandar
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCrypt con costo 10 por defecto
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Permite que Angular (puerto 4200) consuma la API (puerto 8080)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Content-Disposition"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private static void escribirError(jakarta.servlet.http.HttpServletResponse res,
                                      int estado, String error, String mensaje) throws java.io.IOException {
        res.setStatus(estado);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(
                "{\"estado\":" + estado + ",\"error\":\"" + error + "\",\"mensaje\":\"" + mensaje + "\"}");
    }
}
