package com.minierp.backend.controller;

import com.minierp.backend.dto.venta.VentaRequest;
import com.minierp.backend.dto.venta.VentaResponse;
import com.minierp.backend.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // GET /api/ventas?desde=2026-09-01&hasta=2026-09-30&idCliente=3  (todos opcionales)
    @GetMapping
    public List<VentaResponse> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer idCliente) {
        return ventaService.listar(desde, hasta, idCliente);
    }

    @GetMapping("/{id}")
    public VentaResponse obtener(@PathVariable Integer id) {
        return ventaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaResponse registrar(@Valid @RequestBody VentaRequest req) {
        return ventaService.registrar(req);
    }

    @GetMapping("/{id}/factura")
    public ResponseEntity<byte[]> factura(@PathVariable Integer id) {
        byte[] pdf = ventaService.generarFactura(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=factura-" + id + ".pdf")
                .body(pdf);
    }
}