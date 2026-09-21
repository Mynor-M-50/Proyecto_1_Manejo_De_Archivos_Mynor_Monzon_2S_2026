package com.minierp.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Historial de cada entrada y salida: permite saber como y por que cambio la existencia
@Entity
@Table(name = "movimiento_inv")
@Getter
@Setter
@NoArgsConstructor
public class MovimientoInventario {

    public static final String ENTRADA = "ENTRADA";
    public static final String SALIDA = "SALIDA";
    public static final String ORIGEN_COMPRA = "COMPRA";
    public static final String ORIGEN_VENTA = "VENTA";
    public static final String ORIGEN_AJUSTE = "AJUSTE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer idMovimiento;

    @Column(name = "tipo", nullable = false, length = 10)
    private String tipo;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "costo_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoUnitario = BigDecimal.ZERO;

    @Column(name = "existencia_anterior", nullable = false)
    private Integer existenciaAnterior;

    @Column(name = "existencia_nueva", nullable = false)
    private Integer existenciaNueva;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    // Origen del movimiento: COMPRA, VENTA o AJUSTE, y el id de esa operacion
    @Column(name = "origen_tipo", nullable = false, length = 20)
    private String origenTipo;

    @Column(name = "origen_id")
    private Integer origenId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @PrePersist
    void alCrear() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
