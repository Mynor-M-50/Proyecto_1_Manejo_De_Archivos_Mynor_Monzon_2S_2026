package com.minierp.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Se ejecuta una vez en cada peticion.
// Lee el header "Authorization: Bearer <token>", lo valida y registra al usuario como autenticado.
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;
    private final UsuarioDetailService usuarioDetailService;

    public JwtAuthFilter(JwtService jwtService, UsuarioDetailService usuarioDetailService) {
        this.jwtService = jwtService;
        this.usuarioDetailService = usuarioDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(PREFIJO)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String username = jwtService.extraerUsername(header.substring(PREFIJO.length()));

            if (username != null) {
                try {
                    UserDetails usuario = usuarioDetailService.loadUserByUsername(username);
                    if (usuario.isEnabled()) {
                        var autenticacion = new UsernamePasswordAuthenticationToken(
                                usuario, null, usuario.getAuthorities());
                        autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(autenticacion);
                    }
                } catch (UsernameNotFoundException e) {
                    // Token de un usuario que ya no existe: se sigue sin autenticar
                }
            }
        }

        // Si no hay token valido la peticion continua sin usuario,
        // y SecurityConfig decide si la ruta lo requiere (401) o no.
        chain.doFilter(request, response);
    }
}
