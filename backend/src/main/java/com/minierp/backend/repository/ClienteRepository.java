package com.minierp.backend.repository;

import com.minierp.backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    boolean existsByNit(String nit);

    List<Cliente> findByEstadoTrueOrderByNombreAsc();
}
