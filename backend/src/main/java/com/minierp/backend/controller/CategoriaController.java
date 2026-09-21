package com.minierp.backend.controller;

import com.minierp.backend.dto.catalogo.CategoriaRequest;
import com.minierp.backend.entity.Categoria;
import com.minierp.backend.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<Categoria> listar() {
        return categoriaService.listar();
    }

    @GetMapping("/{id}")
    public Categoria obtener(@PathVariable Integer id) {
        return categoriaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria crear(@Valid @RequestBody CategoriaRequest req) {
        return categoriaService.crear(req);
    }

    @PutMapping("/{id}")
    public Categoria actualizar(@PathVariable Integer id, @Valid @RequestBody CategoriaRequest req) {
        return categoriaService.actualizar(id, req);
    }
}
