package com.minierp.backend.security;

import com.minierp.backend.entity.Usuario;
import com.minierp.backend.exception.NoEncontradoException;
import com.minierp.backend.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Obtiene el usuario que hizo la peticion a partir del token ya validado.
// Se usa para registrar quien hizo cada compra, venta, movimiento y accion de bitacora.
@Component
public class UsuarioActual {

    private final UsuarioRepository usuarioRepository;

    public UsuarioActual(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario obtener() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new NoEncontradoException("No hay un usuario autenticado");
        }
        return usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado"));
    }
}
