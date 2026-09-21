package com.minierp.backend.repository;

import com.minierp.backend.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    boolean existsByNit(String nit);

    // Solo proveedores activos (borrado logico)
    List<Proveedor> findByEstadoTrueOrderByNombreAsc();
}
