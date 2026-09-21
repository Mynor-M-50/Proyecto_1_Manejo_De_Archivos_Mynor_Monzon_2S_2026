package com.minierp.backend.service;

import com.minierp.backend.dto.catalogo.CategoriaRequest;
import com.minierp.backend.entity.Categoria;
import com.minierp.backend.exception.NegocioException;
import com.minierp.backend.exception.NoEncontradoException;
import com.minierp.backend.repository.CategoriaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private static final String MODULO = "CATEGORIAS";

    private final CategoriaRepository categoriaRepository;
    private final BitacoraService bitacoraService;

    public CategoriaService(CategoriaRepository categoriaRepository, BitacoraService bitacoraService) {
        this.categoriaRepository = categoriaRepository;
        this.bitacoraService = bitacoraService;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAll(Sort.by("nombre"));
    }

    @Transactional(readOnly = true)
    public Categoria obtener(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Categoria", id));
    }

    @Transactional
    public Categoria crear(CategoriaRequest req) {
        if (categoriaRepository.existsByNombreIgnoreCase(req.nombre().trim())) {
            throw new NegocioException("Ya existe una categoria con el nombre " + req.nombre());
        }
        Categoria c = new Categoria();
        c.setNombre(req.nombre().trim());
        c.setDescripcion(req.descripcion());
        Categoria guardada = categoriaRepository.save(c);
        bitacoraService.registrar(MODULO, BitacoraService.CREAR, "Categoria creada: " + guardada.getNombre());
        return guardada;
    }

    @Transactional
    public Categoria actualizar(Integer id, CategoriaRequest req) {
        Categoria c = obtener(id);
        String nuevoNombre = req.nombre().trim();
        if (!c.getNombre().equalsIgnoreCase(nuevoNombre)
                && categoriaRepository.existsByNombreIgnoreCase(nuevoNombre)) {
            throw new NegocioException("Ya existe una categoria con el nombre " + nuevoNombre);
        }
        c.setNombre(nuevoNombre);
        c.setDescripcion(req.descripcion());
        bitacoraService.registrar(MODULO, BitacoraService.ACTUALIZAR, "Categoria actualizada: " + c.getNombre());
        return c;
    }
}
