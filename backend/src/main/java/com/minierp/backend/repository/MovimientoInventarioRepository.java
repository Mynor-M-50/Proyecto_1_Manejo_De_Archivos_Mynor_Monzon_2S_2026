package com.minierp.backend.repository;

import com.minierp.backend.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    // Historial de movimientos de un producto
    List<MovimientoInventario> findByProductoIdProductoOrderByFechaAscIdMovimientoAsc(Integer idProducto);
}
