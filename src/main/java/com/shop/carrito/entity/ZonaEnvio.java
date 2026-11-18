package com.shop.carrito.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "zona_envio")
public class ZonaEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(name = "costo_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoBase;

    @Column(name = "umbral_gratis", nullable = false, precision = 10, scale = 2)
    private BigDecimal umbralGratis = BigDecimal.valueOf(300.00);

    @Column(name = "dias_entrega", nullable = false, length = 60)
    private String diasEntrega;

    public ZonaEnvio() {}

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getCostoBase() { return costoBase; }
    public void setCostoBase(BigDecimal costoBase) { this.costoBase = costoBase; }
    public BigDecimal getUmbralGratis() { return umbralGratis; }
    public void setUmbralGratis(BigDecimal umbralGratis) { this.umbralGratis = umbralGratis; }
    public String getDiasEntrega() { return diasEntrega; }
    public void setDiasEntrega(String diasEntrega) { this.diasEntrega = diasEntrega; }
}
