package com.minierp.backend.service;

import com.minierp.backend.dto.catalogo.ProveedorRequest;
import com.minierp.backend.entity.Proveedor;
import com.minierp.backend.exception.NegocioException;
import com.minierp.backend.exception.NoEncontradoException;
import com.minierp.backend.repository.ProveedorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProveedorService {

    private static final String MODULO = "PROVEEDORES";

    private final ProveedorRepository proveedorRepository;
    private final BitacoraService bitacoraService;

    public ProveedorService(ProveedorRepository proveedorRepository, BitacoraService bitacoraService) {
        this.proveedorRepository = proveedorRepository;
        this.bitacoraService = bitacoraService;
    }

    @Transactional(readOnly = true)
    public List<Proveedor> listar(boolean soloActivos) {
        return soloActivos
                ? proveedorRepository.findByEstadoTrueOrderByNombreAsc()
                : proveedorRepository.findAll(Sort.by("nombre"));
    }

    @Transactional(readOnly = true)
    public Proveedor obtener(Integer id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Proveedor", id));
    }

    @Transactional
    public Proveedor crear(ProveedorRequest req) {
        String nit = req.nit().trim().toUpperCase();
        if (proveedorRepository.existsByNit(nit)) {
            throw new NegocioException("Ya existe un proveedor con el NIT " + nit);
        }
        Proveedor p = new Proveedor();
        p.setNit(nit);
        aplicarDatos(p, req);
        p.setEstado(true);
        Proveedor guardado = proveedorRepository.save(p);
        bitacoraService.registrar(MODULO, BitacoraService.CREAR, "Proveedor creado: " + guardado.getNombre());
        return guardado;
    }

    @Transactional
    public Proveedor actualizar(Integer id, ProveedorRequest req) {
        Proveedor p = obtener(id);
        String nit = req.nit().trim().toUpperCase();
        if (!p.getNit().equals(nit) && proveedorRepository.existsByNit(nit)) {
            throw new NegocioException("Ya existe un proveedor con el NIT " + nit);
        }
        p.setNit(nit);
        aplicarDatos(p, req);
        bitacoraService.registrar(MODULO, BitacoraService.ACTUALIZAR, "Proveedor actualizado: " + p.getNombre());
        return p;
    }

    // Borrado logico: sus compras historicas se conservan
    @Transactional
    public Proveedor cambiarEstado(Integer id, boolean activo) {
        Proveedor p = obtener(id);
        p.setEstado(activo);
        bitacoraService.registrar(MODULO, BitacoraService.CAMBIAR_ESTADO,
                "Proveedor " + p.getNombre() + (activo ? " activado" : " desactivado"));
        return p;
    }

    private void aplicarDatos(Proveedor p, ProveedorRequest req) {
        p.setNombre(req.nombre().trim());
        p.setContacto(req.contacto());
        p.setTelefono(req.telefono());
        p.setEmail(req.email());
        p.setDireccion(req.direccion());
    }
}
