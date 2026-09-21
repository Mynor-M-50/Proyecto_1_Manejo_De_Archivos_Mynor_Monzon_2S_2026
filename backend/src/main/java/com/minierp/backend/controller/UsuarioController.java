package com.minierp.backend.controller;

import com.minierp.backend.dto.catalogo.UsuarioRequest;
import com.minierp.backend.dto.catalogo.UsuarioResponse;
import com.minierp.backend.entity.Area;
import com.minierp.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/areas")
    public List<Area> areas() {
        return usuarioService.listarAreas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody UsuarioRequest req) {
        return usuarioService.crear(req);
    }

    @PatchMapping("/{id}/estado")
    public UsuarioResponse cambiarEstado(@PathVariable Integer id, @RequestParam boolean activo) {
        return usuarioService.cambiarEstado(id, activo);
    }
}
