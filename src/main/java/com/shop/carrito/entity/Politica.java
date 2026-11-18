package com.shop.carrito.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "politica")
public class Politica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String slug;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String contenido;

    @Column(name = "actualizado_el", nullable = false)
    private Instant actualizadoEl = Instant.now();

    public Politica() {}

    public Long getId() { return id; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public Instant getActualizadoEl() { return actualizadoEl; }
    public void setActualizadoEl(Instant actualizadoEl) { this.actualizadoEl = actualizadoEl; }
}
