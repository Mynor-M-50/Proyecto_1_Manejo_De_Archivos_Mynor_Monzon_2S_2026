package com.minierp.backend.repository;

import jakarta.persistence.LockModeType;
import com.minierp.backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    boolean existsByCodigo(String codigo);

    List<Producto> findByEstadoTrueOrderByNombreAsc();

    // Productos en o por debajo de su stock minimo (alertas de inventario)
    @Query("SELECT p FROM Producto p WHERE p.estado = true AND p.existenciaActual <= p.stockMinimo ORDER BY p.existenciaActual ASC")
    List<Producto> findStockBajo();

    // Bloquea la fila del producto mientras se modifica su existencia
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.idProducto = :id")
    Optional<Producto> findByIdParaActualizar(@Param("id") Integer id);
}
