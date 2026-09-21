package com.minierp.backend.repository;

import com.minierp.backend.entity.Bitacora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BitacoraRepository extends JpaRepository<Bitacora, Integer> {

    List<Bitacora> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime desde, LocalDateTime hasta);

    List<Bitacora> findAllByOrderByFechaHoraDesc();
}
