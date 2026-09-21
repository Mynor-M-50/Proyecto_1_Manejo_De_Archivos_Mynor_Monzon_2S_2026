package com.minierp.backend.repository;

import com.minierp.backend.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByFechaBetweenOrderByFechaDesc(LocalDateTime desde, LocalDateTime hasta);

    // Para generar el siguiente numero de factura
    @Query("SELECT COALESCE(MAX(v.idVenta), 0) FROM Venta v")
    Integer findUltimoId();
}
