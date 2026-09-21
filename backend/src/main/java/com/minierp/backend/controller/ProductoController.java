package com.minierp.backend.controller;

import com.minierp.backend.dto.catalogo.ProductoRequest;
import com.minierp.backend.dto.catalogo.ProductoResponse;
import com.minierp.backend.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // GET /api/productos?activos=true  -> solo los que se pueden vender o comprar
    @GetMapping
    public List<ProductoResponse> listar(@RequestParam(defaultValue = "false") boolean activos) {
        return productoService.listar(activos);
    }

    @GetMapping("/stock-bajo")
    public List<ProductoResponse> stockBajo() {
        return productoService.stockBajo();
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Integer id) {
        return productoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest req) {
        return productoService.crear(req);
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Integer id, @Valid @RequestBody ProductoRequest req) {
        return productoService.actualizar(id, req);
    }

    // PATCH /api/productos/5/estado?activo=false
    @PatchMapping("/{id}/estado")
    public ProductoResponse cambiarEstado(@PathVariable Integer id, @RequestParam boolean activo) {
        return productoService.cambiarEstado(id, activo);
    }
}
