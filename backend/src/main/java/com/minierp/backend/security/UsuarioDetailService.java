package com.minierp.backend.security;

import com.minierp.backend.entity.Usuario;
import com.minierp.backend.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Le dice a Spring Security como cargar un usuario desde la base.
// El area del usuario se convierte en su rol: ROLE_ADMINISTRACION, ROLE_VENTAS, etc.
@Service
public class UsuarioDetailService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String rol = "ROLE_" + usuario.getArea().getNombre();

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority(rol)))
                .disabled(!usuario.getEstado())
                .build();
    }
}
