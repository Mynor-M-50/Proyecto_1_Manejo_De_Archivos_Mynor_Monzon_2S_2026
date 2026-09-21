package com.minierp.backend.controller;

import com.minierp.backend.dto.compra.CompraResponse;
import com.minierp.backend.dto.inventario.MovimientoResponse;
import com.minierp.backend.dto.reporte.*;
import com.minierp.backend.dto.venta.VentaResponse;
import com.minierp.backend.service.BitacoraService;
import com.minierp.backend.service.CompraService;
import com.minierp.backend.service.InventarioService;
import com.minierp.backend.service.ReporteService;
import com.minierp.backend.service.VentaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final InventarioService inventarioService;
    private final CompraService compraService;
    private final VentaService ventaService;
    private final BitacoraService bitacoraService;

    public ReporteController(ReporteService reporteService,
                             InventarioService inventarioService,
                             CompraService compraService,
                             VentaService ventaService,
                             BitacoraService bitacoraService) {
        this.reporteService = reporteService;
        this.inventarioService = inventarioService;
        this.compraService = compraService;
        this.ventaService = ventaService;
        this.bitacoraService = bitacoraService;
    }

    // ---------- Productos e inventario ----------

    @GetMapping("/inventario/mas-vendidos")
    public List<ProductoVendidoReporte> masVendidos(
            @RequestParam(defaultValue = "10") int limite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.productosMasVendidos(limite, desde, hasta);
    }

    @GetMapping("/inventario/menor-existencia")
    public List<ExistenciaReporte> menorExistencia(@RequestParam(defaultValue = "10") int limite) {
        return reporteService.menorExistencia(limite);
    }

    @GetMapping("/inventario/mas-movimientos")
    public List<ConteoReporte> masMovimientos(
            @RequestParam(defaultValue = "10") int limite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.masMovimientos(limite, desde, hasta);
    }

    @GetMapping("/inventario/historial/{idProducto}")
    public List<MovimientoResponse> historial(@PathVariable Integer idProducto) {
        return inventarioService.historial(idProducto);
    }

    // ---------- Compras y proveedores ----------

    @GetMapping("/compras/rango")
    public List<CompraResponse> comprasRango(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return compraService.listar(desde, hasta);
    }

    @GetMapping("/compras/top-proveedores")
    public List<ProveedorMontoReporte> topProveedores(
            @RequestParam(defaultValue = "5") int limite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.topProveedores(limite, desde, hasta);
    }

    @GetMapping("/compras/mas-adquiridos")
    public List<ConteoReporte> masAdquiridos(
            @RequestParam(defaultValue = "10") int limite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.productosMasAdquiridos(limite, desde, hasta);
    }

    // ---------- Ventas y clientes ----------

    @GetMapping("/ventas/rango")
    public List<VentaResponse> ventasRango(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ventaService.listar(desde, hasta, null);
    }

    @GetMapping("/ventas/top-clientes")
    public List<ClienteMontoReporte> topClientes(
            @RequestParam(defaultValue = "10") int limite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.topClientes(limite, desde, hasta);
    }

    @GetMapping("/ventas/top-ingresos")
    public List<ProductoVendidoReporte> topIngresos(
            @RequestParam(defaultValue = "10") int limite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.productosMasIngresos(limite, desde, hasta);
    }

    // agrupacion: DIA, SEMANA, MES o ANIO
    @GetMapping("/ventas/resumen")
    public List<ResumenPeriodoReporte> resumen(
            @RequestParam(defaultValue = "MES") String agrupacion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.resumenVentas(agrupacion, desde, hasta);
    }

    // ---------- Logs ----------

    @GetMapping("/logs")
    public List<BitacoraResponse> logs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return bitacoraService.listar(desde, hasta);
    }
}