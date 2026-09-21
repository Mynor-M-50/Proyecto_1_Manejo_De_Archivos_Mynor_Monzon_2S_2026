package com.minierp.backend.service;

import com.minierp.backend.dto.catalogo.ProductoRequest;
import com.minierp.backend.dto.catalogo.ProductoResponse;
import com.minierp.backend.entity.Categoria;
import com.minierp.backend.entity.Producto;
import com.minierp.backend.exception.NegocioException;
import com.minierp.backend.exception.NoEncontradoException;
import com.minierp.backend.repository.ProductoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private static final String MODULO = "PRODUCTOS";

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;
    private final BitacoraService bitacoraService;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaService categoriaService,
                           BitacoraService bitacoraService) {
        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;
        this.bitacoraService = bitacoraService;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(boolean soloActivos) {
        List<Producto> productos = soloActivos
                ? productoRepository.findByEstadoTrueOrderByNombreAsc()
                : productoRepository.findAll(Sort.by("nombre"));
        return productos.stream().map(ProductoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Integer id) {
        return ProductoResponse.from(buscar(id));
    }

    // Productos que requieren atencion por estar en o bajo su stock minimo
    @Transactional(readOnly = true)
    public List<ProductoResponse> stockBajo() {
        return productoRepository.findStockBajo().stream().map(ProductoResponse::from).toList();
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest req) {
        String codigo = req.codigo().trim().toUpperCase();
        if (productoRepository.existsByCodigo(codigo)) {
            throw new NegocioException("Ya existe un producto con el codigo " + codigo);
        }
        Producto p = new Producto();
        p.setCodigo(codigo);
        aplicarDatos(p, req);
        // La existencia inicia en 0: solo aumenta con compras
        p.setExistenciaActual(0);
        p.setEstado(true);
        Producto guardado = productoRepository.save(p);
        bitacoraService.registrar(MODULO, BitacoraService.CREAR,
                "Producto creado: " + guardado.getCodigo() + " - " + guardado.getNombre());
        return ProductoResponse.from(guardado);
    }

    @Transactional
    public ProductoResponse actualizar(Integer id, ProductoRequest req) {
        Producto p = buscar(id);
        String codigo = req.codigo().trim().toUpperCase();
        if (!p.getCodigo().equals(codigo) && productoRepository.existsByCodigo(codigo)) {
            throw new NegocioException("Ya existe un producto con el codigo " + codigo);
        }
        p.setCodigo(codigo);
        aplicarDatos(p, req);
        bitacoraService.registrar(MODULO, BitacoraService.ACTUALIZAR,
                "Producto actualizado: " + p.getCodigo() + " - " + p.getNombre());
        return ProductoResponse.from(p);
    }

    // Borrado logico: el producto deja de comercializarse pero su historial se conserva
    @Transactional
    public ProductoResponse cambiarEstado(Integer id, boolean activo) {
        Producto p = buscar(id);
        p.setEstado(activo);
        bitacoraService.registrar(MODULO, BitacoraService.CAMBIAR_ESTADO,
                "Producto " + p.getCodigo() + (activo ? " activado" : " desactivado"));
        return ProductoResponse.from(p);
    }

    // Usado por otros servicios (compras, ventas, inventario)
    @Transactional(readOnly = true)
    public Producto buscar(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Producto", id));
    }

    private void aplicarDatos(Producto p, ProductoRequest req) {
        Categoria categoria = categoriaService.obtener(req.idCategoria());
        p.setNombre(req.nombre().trim());
        p.setDescripcion(req.descripcion());
        p.setUnidadMedida(req.unidadMedida() == null || req.unidadMedida().isBlank()
                ? "UNIDAD" : req.unidadMedida().trim().toUpperCase());
        p.setPrecioVenta(req.precioVenta());
        p.setStockMinimo(req.stockMinimo());
        p.setCategoria(categoria);
    }
}
