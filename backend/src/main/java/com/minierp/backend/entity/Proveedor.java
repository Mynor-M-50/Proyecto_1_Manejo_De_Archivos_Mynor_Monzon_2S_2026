package com.minierp.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proveedor")
@Getter
@Setter
@NoArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @Column(name = "nit", nullable = false, unique = true, length = 20)
    private String nit;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "contacto", length = 120)
    private String contacto;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "direccion", length = 200)
    private String direccion;

    // Borrado logico: nunca se elimina fisicamente para conservar el historial
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
