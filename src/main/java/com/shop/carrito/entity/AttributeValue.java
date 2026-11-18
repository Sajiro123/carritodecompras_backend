package com.shop.carrito.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "atributo_valor")
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atributo_id", nullable = false)
    @JsonIgnore
    private Attribute attribute;

    @Column(nullable = false, length = 50)
    private String valor;

    public AttributeValue() {}

    public AttributeValue(Attribute attribute, String valor) {
        this.attribute = attribute;
        this.valor = valor;
    }

    public Long getId() { return id; }
    public Attribute getAttribute() { return attribute; }
    public void setAttribute(Attribute attribute) { this.attribute = attribute; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
}
