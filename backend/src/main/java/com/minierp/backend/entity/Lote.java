package com.minierp.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Capa de inventario: cada compra crea un lote con su propio costo.
// PEPS consume primero los lotes con fecha_ingreso mas antigua.
@Entity
@Table(name = "lote")
@Getter
@Setter
@NoArgsConstructor
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Integer idLote;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "cantidad_inicial", nullable = false)
    private Integer cantidadInicial;

    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible;

    @Column(name = "costo_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoUnitario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    // Detalle de compra que origino el lote (puede ser null en un ajuste)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle_compra")
    private DetalleCompra detalleCompra;

    @PrePersist
    void alCrear() {
        if (fechaIngreso == null) {
            fechaIngreso = LocalDateTime.now();
        }
    }
}
