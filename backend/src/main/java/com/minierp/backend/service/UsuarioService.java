package com.minierp.backend.service;

import com.minierp.backend.dto.catalogo.UsuarioRequest;
import com.minierp.backend.dto.catalogo.UsuarioResponse;
import com.minierp.backend.entity.Area;
import com.minierp.backend.entity.Usuario;
import com.minierp.backend.exception.NegocioException;
import com.minierp.backend.exception.NoEncontradoException;
import com.minierp.backend.repository.AreaRepository;
import com.minierp.backend.repository.UsuarioRepository;
import com.minierp.backend.security.UsuarioActual;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private static final String MODULO = "USUARIOS";

    private final UsuarioRepository usuarioRepository;
    private final AreaRepository areaRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioActual usuarioActual;
    private final BitacoraService bitacoraService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          AreaRepository areaRepository,
                          PasswordEncoder passwordEncoder,
                          UsuarioActual usuarioActual,
                          BitacoraService bitacoraService) {
        this.usuarioRepository = usuarioRepository;
        this.areaRepository = areaRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioActual = usuarioActual;
        this.bitacoraService = bitacoraService;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll(Sort.by("username")).stream()
                .map(UsuarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<Area> listarAreas() {
        return areaRepository.findAll(Sort.by("idArea"));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest req) {
        String username = req.username().trim().toLowerCase();
        if (usuarioRepository.existsByUsername(username)) {
            throw new NegocioException("El usuario " + username + " ya existe");
        }
        Area area = areaRepository.findById(req.idArea())
                .orElseThrow(() -> new NoEncontradoException("Area", req.idArea()));

        Usuario u = new Usuario();
        u.setNombreCompleto(req.nombreCompleto().trim());
        u.setUsername(username);
        // Se guarda solo el hash BCrypt, nunca la contrasena en texto plano
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setEstado(true);
        u.setArea(area);
        Usuario guardado = usuarioRepository.save(u);

        bitacoraService.registrar(MODULO, BitacoraService.CREAR,
                "Usuario creado: " + username + " (" + area.getNombre() + ")");
        return UsuarioResponse.from(guardado);
    }

    @Transactional
    public UsuarioResponse cambiarEstado(Integer id, boolean activo) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Usuario", id));
        if (!activo && u.getIdUsuario().equals(usuarioActual.obtener().getIdUsuario())) {
            throw new NegocioException("No puede desactivar su propio usuario");
        }
        u.setEstado(activo);
        bitacoraService.registrar(MODULO, BitacoraService.CAMBIAR_ESTADO,
                "Usuario " + u.getUsername() + (activo ? " activado" : " desactivado"));
        return UsuarioResponse.from(u);
    }
}
