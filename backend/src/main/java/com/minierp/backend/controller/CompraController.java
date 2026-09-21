package com.minierp.backend.controller;

import com.minierp.backend.dto.compra.CompraRequest;
import com.minierp.backend.dto.compra.CompraResponse;
import com.minierp.backend.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public List<CompraResponse> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return compraService.listar(desde, hasta);
    }

    @GetMapping("/{id}")
    public CompraResponse obtener(@PathVariable Integer id) {
        return compraService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompraResponse registrar(@Valid @RequestBody CompraRequest req) {
        return compraService.registrar(req);
    }
}