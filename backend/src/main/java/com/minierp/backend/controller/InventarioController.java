package com.minierp.backend.controller;

import com.minierp.backend.dto.inventario.LoteResponse;
import com.minierp.backend.dto.inventario.MovimientoResponse;
import com.minierp.backend.service.InventarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/productos/{id}/movimientos")
    public List<MovimientoResponse> movimientos(@PathVariable Integer id) {
        return inventarioService.historial(id);
    }

    @GetMapping("/productos/{id}/lotes")
    public List<LoteResponse> lotes(@PathVariable Integer id) {
        return inventarioService.lotes(id);
    }
}