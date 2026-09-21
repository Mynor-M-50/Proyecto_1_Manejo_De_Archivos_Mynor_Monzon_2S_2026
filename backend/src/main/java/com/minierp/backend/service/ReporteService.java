package com.minierp.backend.service;

import com.minierp.backend.dto.reporte.ClienteMontoReporte;
import com.minierp.backend.dto.reporte.ConteoReporte;
import com.minierp.backend.dto.reporte.ExistenciaReporte;
import com.minierp.backend.dto.reporte.ProductoVendidoReporte;
import com.minierp.backend.dto.reporte.ProveedorMontoReporte;
import com.minierp.backend.dto.reporte.ResumenPeriodoReporte;
import com.minierp.backend.exception.NegocioException;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Consultas de agregacion para el modulo de reportes.
// Usan JPQL con "SELECT new": la base agrupa y suma, y el resultado llega directo al DTO.
@Service
@Transactional(readOnly = true)
public class ReporteService {

    private static final String PKG = "com.minierp.backend.dto.reporte.";

    private final EntityManager em;

    public ReporteService(EntityManager em) {
        this.em = em;
    }

    // ---------- Productos e inventario ----------

    public List<ProductoVendidoReporte> productosMasVendidos(int limite, LocalDate desde, LocalDate hasta) {
        return em.createQuery(
                        "SELECT new " + PKG + "ProductoVendidoReporte("
                                + "p.idProducto, p.codigo, p.nombre, SUM(d.cantidad), SUM(d.subtotal)) "
                                + "FROM DetalleVenta d JOIN d.producto p JOIN d.venta v "
                                + "WHERE v.fecha >= :inicio AND v.fecha < :fin "
                                + "GROUP BY p.idProducto, p.codigo, p.nombre "
                                + "ORDER BY SUM(d.cantidad) DESC", ProductoVendidoReporte.class)
                .setParameter("inicio", inicio(desde))
                .setParameter("fin", fin(hasta))
                .setMaxResults(validarLimite(limite))
                .getResultList();
    }

    public List<ExistenciaReporte> menorExistencia(int limite) {
        return em.createQuery(
                        "SELECT new " + PKG + "ExistenciaReporte("
                                + "p.idProducto, p.codigo, p.nombre, c.nombre, p.existenciaActual, p.stockMinimo) "
                                + "FROM Producto p JOIN p.categoria c "
                                + "WHERE p.estado = true "
                                + "ORDER BY p.existenciaActual ASC, p.nombre ASC", ExistenciaReporte.class)
                .setMaxResults(validarLimite(limite))
                .getResultList();
    }

    public List<ConteoReporte> masMovimientos(int limite, LocalDate desde, LocalDate hasta) {
        return em.createQuery(
                        "SELECT new " + PKG + "ConteoReporte(p.idProducto, p.codigo, p.nombre, COUNT(m)) "
                                + "FROM MovimientoInventario m JOIN m.producto p "
                                + "WHERE m.fecha >= :inicio AND m.fecha < :fin "
                                + "GROUP BY p.idProducto, p.codigo, p.nombre "
                                + "ORDER BY COUNT(m) DESC", ConteoReporte.class)
                .setParameter("inicio", inicio(desde))
                .setParameter("fin", fin(hasta))
                .setMaxResults(validarLimite(limite))
                .getResultList();
    }

    // ---------- Compras y proveedores ----------

    public List<ProveedorMontoReporte> topProveedores(int limite, LocalDate desde, LocalDate hasta) {
        return em.createQuery(
                        "SELECT new " + PKG + "ProveedorMontoReporte("
                                + "pr.idProveedor, pr.nit, pr.nombre, COUNT(c), SUM(c.total)) "
                                + "FROM Compra c JOIN c.proveedor pr "
                                + "WHERE c.fecha >= :inicio AND c.fecha < :fin "
                                + "GROUP BY pr.idProveedor, pr.nit, pr.nombre "
                                + "ORDER BY SUM(c.total) DESC", ProveedorMontoReporte.class)
                .setParameter("inicio", inicio(desde))
                .setParameter("fin", fin(hasta))
                .setMaxResults(validarLimite(limite))
                .getResultList();
    }

    // Frecuencia = en cuantas compras distintas aparece el producto
    public List<ConteoReporte> productosMasAdquiridos(int limite, LocalDate desde, LocalDate hasta) {
        return em.createQuery(
                        "SELECT new " + PKG + "ConteoReporte(p.idProducto, p.codigo, p.nombre, COUNT(d)) "
                                + "FROM DetalleCompra d JOIN d.producto p JOIN d.compra c "
                                + "WHERE c.fecha >= :inicio AND c.fecha < :fin "
                                + "GROUP BY p.idProducto, p.codigo, p.nombre "
                                + "ORDER BY COUNT(d) DESC, SUM(d.cantidad) DESC", ConteoReporte.class)
                .setParameter("inicio", inicio(desde))
                .setParameter("fin", fin(hasta))
                .setMaxResults(validarLimite(limite))
                .getResultList();
    }

    // ---------- Ventas y clientes ----------

    public List<ClienteMontoReporte> topClientes(int limite, LocalDate desde, LocalDate hasta) {
        return em.createQuery(
                        "SELECT new " + PKG + "ClienteMontoReporte("
                                + "cl.idCliente, cl.nit, cl.nombre, COUNT(v), SUM(v.total)) "
                                + "FROM Venta v JOIN v.cliente cl "
                                + "WHERE v.fecha >= :inicio AND v.fecha < :fin "
                                + "GROUP BY cl.idCliente, cl.nit, cl.nombre "
                                + "ORDER BY SUM(v.total) DESC", ClienteMontoReporte.class)
                .setParameter("inicio", inicio(desde))
                .setParameter("fin", fin(hasta))
                .setMaxResults(validarLimite(limite))
                .getResultList();
    }

    public List<ProductoVendidoReporte> productosMasIngresos(int limite, LocalDate desde, LocalDate hasta) {
        return em.createQuery(
                        "SELECT new " + PKG + "ProductoVendidoReporte("
                                + "p.idProducto, p.codigo, p.nombre, SUM(d.cantidad), SUM(d.subtotal)) "
                                + "FROM DetalleVenta d JOIN d.producto p JOIN d.venta v "
                                + "WHERE v.fecha >= :inicio AND v.fecha < :fin "
                                + "GROUP BY p.idProducto, p.codigo, p.nombre "
                                + "ORDER BY SUM(d.subtotal) DESC", ProductoVendidoReporte.class)
                .setParameter("inicio", inicio(desde))
                .setParameter("fin", fin(hasta))
                .setMaxResults(validarLimite(limite))
                .getResultList();
    }

    // Agrupacion: DIA, SEMANA, MES o ANIO. El formato sale de una lista cerrada,
    // nunca del texto del usuario, para evitar inyeccion SQL.
    public List<ResumenPeriodoReporte> resumenVentas(String agrupacion, LocalDate desde, LocalDate hasta) {
        String formato = switch (agrupacion == null ? "MES" : agrupacion.toUpperCase()) {
            case "DIA" -> "YYYY-MM-DD";
            case "SEMANA" -> "IYYY-IW";
            case "MES" -> "YYYY-MM";
            case "ANIO" -> "YYYY";
            default -> throw new NegocioException("Agrupacion invalida. Use DIA, SEMANA, MES o ANIO");
        };

        @SuppressWarnings("unchecked")
        List<Object[]> filas = em.createNativeQuery(
                        "SELECT to_char(v.fecha, :formato) AS periodo, COUNT(*), "
                                + "SUM(v.subtotal), SUM(v.iva), SUM(v.total) "
                                + "FROM venta v "
                                + "WHERE v.fecha >= :inicio AND v.fecha < :fin "
                                + "GROUP BY 1 ORDER BY 1")
                .setParameter("formato", formato)
                .setParameter("inicio", inicio(desde))
                .setParameter("fin", fin(hasta))
                .getResultList();

        return filas.stream().map(f -> new ResumenPeriodoReporte(
                (String) f[0],
                ((Number) f[1]).longValue(),
                new BigDecimal(f[2].toString()),
                new BigDecimal(f[3].toString()),
                new BigDecimal(f[4].toString())
        )).toList();
    }

    // ---------- Utilidades ----------

    private LocalDateTime inicio(LocalDate desde) {
        return desde != null ? desde.atStartOfDay() : LocalDate.of(2000, 1, 1).atStartOfDay();
    }

    private LocalDateTime fin(LocalDate hasta) {
        return hasta != null ? hasta.plusDays(1).atStartOfDay() : LocalDate.now().plusDays(1).atStartOfDay();
    }

    private int validarLimite(int limite) {
        if (limite < 1 || limite > 100) {
            throw new NegocioException("El limite debe estar entre 1 y 100");
        }
        return limite;
    }
}