package com.minierp.backend.controller;

import com.minierp.backend.dto.catalogo.ClienteRequest;
import com.minierp.backend.entity.Cliente;
import com.minierp.backend.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<Cliente> listar(@RequestParam(defaultValue = "false") boolean activos) {
        return clienteService.listar(activos);
    }

    @GetMapping("/{id}")
    public Cliente obtener(@PathVariable Integer id) {
        return clienteService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente crear(@Valid @RequestBody ClienteRequest req) {
        return clienteService.crear(req);
    }

    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Integer id, @Valid @RequestBody ClienteRequest req) {
        return clienteService.actualizar(id, req);
    }

    @PatchMapping("/{id}/estado")
    public Cliente cambiarEstado(@PathVariable Integer id, @RequestParam boolean activo) {
        return clienteService.cambiarEstado(id, activo);
    }
}
