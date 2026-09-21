package com.minierp.backend.service;

import com.minierp.backend.dto.auth.LoginRequest;
import com.minierp.backend.dto.auth.LoginResponse;
import com.minierp.backend.entity.Usuario;
import com.minierp.backend.exception.NoEncontradoException;
import com.minierp.backend.repository.UsuarioRepository;
import com.minierp.backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // Verifica usuario y contrasena contra el hash BCrypt.
        // Si no coinciden lanza BadCredentialsException (401 en GlobalExceptionHandler).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado"));

        String token = jwtService.generarToken(usuario);

        return new LoginResponse(
                token,
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getArea().getNombre()
        );
    }
}
