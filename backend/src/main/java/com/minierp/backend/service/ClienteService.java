package com.minierp.backend.service;

import com.minierp.backend.dto.catalogo.ClienteRequest;
import com.minierp.backend.entity.Cliente;
import com.minierp.backend.exception.NegocioException;
import com.minierp.backend.exception.NoEncontradoException;
import com.minierp.backend.repository.ClienteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private static final String MODULO = "CLIENTES";

    private final ClienteRepository clienteRepository;
    private final BitacoraService bitacoraService;

    public ClienteService(ClienteRepository clienteRepository, BitacoraService bitacoraService) {
        this.clienteRepository = clienteRepository;
        this.bitacoraService = bitacoraService;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar(boolean soloActivos) {
        return soloActivos
                ? clienteRepository.findByEstadoTrueOrderByNombreAsc()
                : clienteRepository.findAll(Sort.by("nombre"));
    }

    @Transactional(readOnly = true)
    public Cliente obtener(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Cliente", id));
    }

    @Transactional
    public Cliente crear(ClienteRequest req) {
        String nit = req.nit().trim().toUpperCase();
        if (clienteRepository.existsByNit(nit)) {
            throw new NegocioException("Ya existe un cliente con el NIT " + nit);
        }
        Cliente c = new Cliente();
        c.setNit(nit);
        aplicarDatos(c, req);
        c.setEstado(true);
        Cliente guardado = clienteRepository.save(c);
        bitacoraService.registrar(MODULO, BitacoraService.CREAR, "Cliente creado: " + guardado.getNombre());
        return guardado;
    }

    @Transactional
    public Cliente actualizar(Integer id, ClienteRequest req) {
        Cliente c = obtener(id);
        String nit = req.nit().trim().toUpperCase();
        if (!c.getNit().equals(nit) && clienteRepository.existsByNit(nit)) {
            throw new NegocioException("Ya existe un cliente con el NIT " + nit);
        }
        c.setNit(nit);
        aplicarDatos(c, req);
        bitacoraService.registrar(MODULO, BitacoraService.ACTUALIZAR, "Cliente actualizado: " + c.getNombre());
        return c;
    }

    // Borrado logico: sus ventas historicas se conservan
    @Transactional
    public Cliente cambiarEstado(Integer id, boolean activo) {
        Cliente c = obtener(id);
        c.setEstado(activo);
        bitacoraService.registrar(MODULO, BitacoraService.CAMBIAR_ESTADO,
                "Cliente " + c.getNombre() + (activo ? " activado" : " desactivado"));
        return c;
    }

    private void aplicarDatos(Cliente c, ClienteRequest req) {
        c.setNombre(req.nombre().trim());
        c.setTelefono(req.telefono());
        c.setEmail(req.email());
        c.setDireccion(req.direccion());
    }
}
