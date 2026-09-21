package com.minierp.backend.controller;

import com.minierp.backend.dto.catalogo.ProveedorRequest;
import com.minierp.backend.entity.Proveedor;
import com.minierp.backend.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public List<Proveedor> listar(@RequestParam(defaultValue = "false") boolean activos) {
        return proveedorService.listar(activos);
    }

    @GetMapping("/{id}")
    public Proveedor obtener(@PathVariable Integer id) {
        return proveedorService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Proveedor crear(@Valid @RequestBody ProveedorRequest req) {
        return proveedorService.crear(req);
    }

    @PutMapping("/{id}")
    public Proveedor actualizar(@PathVariable Integer id, @Valid @RequestBody ProveedorRequest req) {
        return proveedorService.actualizar(id, req);
    }

    @PatchMapping("/{id}/estado")
    public Proveedor cambiarEstado(@PathVariable Integer id, @RequestParam boolean activo) {
        return proveedorService.cambiarEstado(id, activo);
    }
}
