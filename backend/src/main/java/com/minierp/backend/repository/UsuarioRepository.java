package com.minierp.backend.repository;

import com.minierp.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Usado por el login
    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
}
