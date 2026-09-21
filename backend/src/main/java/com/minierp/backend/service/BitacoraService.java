package com.minierp.backend.service;

import com.minierp.backend.dto.reporte.BitacoraResponse;
import com.minierp.backend.entity.Bitacora;
import com.minierp.backend.repository.BitacoraRepository;
import com.minierp.backend.security.UsuarioActual;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BitacoraService {

    public static final String CREAR = "CREAR";
    public static final String ACTUALIZAR = "ACTUALIZAR";
    public static final String CAMBIAR_ESTADO = "CAMBIAR_ESTADO";

    private final BitacoraRepository bitacoraRepository;
    private final UsuarioActual usuarioActual;

    public BitacoraService(BitacoraRepository bitacoraRepository, UsuarioActual usuarioActual) {
        this.bitacoraRepository = bitacoraRepository;
        this.usuarioActual = usuarioActual;
    }

    // Se llama dentro de la misma transaccion de la operacion:
    // si la operacion falla y hace rollback, el registro tampoco queda.
    @Transactional
    public void registrar(String modulo, String accion, String descripcion) {
        Bitacora b = new Bitacora();
        b.setModulo(modulo);
        b.setAccion(accion);
        b.setDescripcion(descripcion);
        b.setUsuario(usuarioActual.obtener());
        bitacoraRepository.save(b);
    }

    @Transactional(readOnly = true)
    public List<BitacoraResponse> listar(LocalDate desde, LocalDate hasta) {
        List<Bitacora> registros = (desde != null && hasta != null)
                ? bitacoraRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(
                        desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay())
                : bitacoraRepository.findAllByOrderByFechaHoraDesc();
        return registros.stream().map(BitacoraResponse::from).toList();
    }
}
