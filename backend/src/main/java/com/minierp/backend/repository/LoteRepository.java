package com.minierp.backend.repository;

import jakarta.persistence.LockModeType;
import com.minierp.backend.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoteRepository extends JpaRepository<Lote, Integer> {

    // PEPS: lotes con existencia, del mas antiguo al mas reciente.
    // Se bloquean para que dos ventas simultaneas no consuman el mismo lote.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lote l WHERE l.producto.idProducto = :idProducto AND l.cantidadDisponible > 0 ORDER BY l.fechaIngreso ASC, l.idLote ASC")
    List<Lote> findDisponiblesPeps(@Param("idProducto") Integer idProducto);
}
